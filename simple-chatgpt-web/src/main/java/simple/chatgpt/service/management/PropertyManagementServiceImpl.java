package simple.chatgpt.service.management;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import simple.chatgpt.mapper.management.PropertyManagementMapper;
import simple.chatgpt.pojo.management.PropertyManagementPojo;
import simple.chatgpt.util.GenericCache;
import simple.chatgpt.util.PagedResult;
import simple.chatgpt.util.PropertyKey;

@Service
public class PropertyManagementServiceImpl implements PropertyManagementService {

    private static final Logger logger = LogManager.getLogger(PropertyManagementServiceImpl.class);

    private final PropertyManagementMapper mapper;
    private final GenericCache<String, PropertyManagementPojo> cache;
    private final Validator validator;

    @Autowired
    public PropertyManagementServiceImpl(PropertyManagementMapper mapper,
                                         @Qualifier("propertyCache") GenericCache<String, PropertyManagementPojo> cache) {
        this.mapper = mapper;
        this.cache = cache;

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();

        initDefaults();
    }

    @PostConstruct
    private void initDefaults() {
        logger.debug("Initializing default properties...");
        for (PropertyKey key : PropertyKey.values()) {
            PropertyManagementPojo existing = mapper.findByPropertyKey(key.getKey());
            if (existing == null) {
                PropertyManagementPojo prop = new PropertyManagementPojo();
                prop.setPropertyKey(key.getKey());
                prop.setPropertyName(key.getKey());
                prop.setType(key.getTypeName());
                prop.setValue(String.valueOf(key.getDefaultValue()));
                mapper.insertProperty(prop);
                logger.debug("Inserted default property: {}", prop);
            }
        }
    }

    private PropertyManagementPojo getCachedProperty(PropertyKey key) {
        return cache.get(key.getKey(), k -> {
            logger.debug("getCachedProperty not in cache, fetching from DB: {}", k);
            PropertyManagementPojo prop = mapper.findByPropertyKey(k);
            if (prop != null) {
                logger.debug("Found property in DB: {}", prop);
                return prop;
            }

            // Fallback to default
            for (PropertyKey pk : PropertyKey.values()) {
                if (pk.getKey().equals(k)) {
                    PropertyManagementPojo defaultProp = new PropertyManagementPojo();
                    defaultProp.setPropertyKey(pk.getKey());
                    defaultProp.setPropertyName(pk.getKey());
                    defaultProp.setType(pk.getTypeName());
                    defaultProp.setValue(String.valueOf(pk.getDefaultValue()));
                    logger.debug("Returning default property: {}", defaultProp);
                    return defaultProp;
                }
            }
            logger.debug("Property key {} not found and no default available", k);
            return null;
        });
    }

    // ---------------- Typed Getters ----------------
    @Override
    public boolean getBoolean(PropertyKey key) {
        PropertyManagementPojo prop = getCachedProperty(key);
        boolean value = Boolean.parseBoolean(prop.getValue());
        logger.debug("getBoolean key={} -> {}", key.getKey(), value);
        return value;
    }

    @Override
    public int getInteger(PropertyKey key) {
        logger.debug("#############");
        PropertyManagementPojo prop = getCachedProperty(key);
        logger.debug("getInteger key={}", key);
        logger.debug("getInteger prop={}", prop);
        logger.debug("#############");
        int value;
        try {
            value = Integer.parseInt(prop.getValue());
        } catch (Exception e) {
            logger.debug("Invalid integer for key={} value='{}', defaulting to 0", key.getKey(), prop.getValue());
            value = 0;
        }
        logger.debug("getInteger key={} -> {}", key.getKey(), value);
        return value;
    }

    @Override
    public BigDecimal getDecimal(PropertyKey key) {
        logger.debug("#############");
        PropertyManagementPojo prop = getCachedProperty(key);
        logger.debug("getDecimal key={}", key);
        logger.debug("getDecimal prop={}", prop);
        logger.debug("#############");
        BigDecimal value;
        try {
            value = new BigDecimal(prop.getValue());
        } catch (Exception e) {
            logger.debug("Invalid decimal for key={} value='{}', defaulting to 0", key.getKey(), prop.getValue());
            value = BigDecimal.ZERO;
        }
        logger.debug("getDecimal key={} -> {}", key.getKey(), value);
        return value;
    }

    @Override
    public String getString(PropertyKey key) {
        logger.debug("#############");
        PropertyManagementPojo prop = getCachedProperty(key);
        logger.debug("getString key={}", key);
        logger.debug("getString prop={}", prop);
        logger.debug("#############");
        logger.debug("getString key={} -> {}", key.getKey(), prop.getValue());
        return prop.getValue();
    }

    // ---------------- Update Property ----------------
    
    /*
    note to myself
    1>Majority of validation (~70–80%) is done in controllers.
      Service-level validation (~20–30%) is mainly for extra safety, 
      internal rules, or objects not coming from controllers.
    2>so we will add @Validated and @Valid to controller.  also you 
      cant do annotation at service level.  service level only allows 
      manual like this.
    3>also we have to do manual validation here, because we dont have 
      PropertyManagementPojo with proper annotation as input.
    4>also this is probably legacy code that will never get called
      from new controller. but i leave it...
    */
    @Override
    public void updateProperty(PropertyKey key, String newValue) {
        logger.debug("#############");
        logger.debug("updateProperty key={} newValue={}", key.getKey(), newValue);

        PropertyManagementPojo prop = new PropertyManagementPojo();
        prop.setPropertyKey(key.getKey());
        prop.setType(key.getTypeName());
        prop.setValue(newValue);

        // 1. Validate
        Set<ConstraintViolation<PropertyManagementPojo>> violations = validator.validate(prop);
        if (!violations.isEmpty()) {
            for (ConstraintViolation<PropertyManagementPojo> violation : violations) {
                logger.debug("Validation error: {} for value '{}'", violation.getMessage(), newValue);
            }
            throw new IllegalArgumentException("Property validation failed.");
        }

        // 2. Update DB
        mapper.updatePropertyByPropertyKey(prop);
        logger.debug("Property updated in DB: {}", prop);

        // 3. Invalidate cache
        cache.invalidate(key.getKey());
        logger.debug("Cache invalidated for key={}", key.getKey());
        logger.debug("#############");
    }

    // ---------------- CRUD Operations ----------------
    @Override
    public PropertyManagementPojo getPropertyById(Long id) {
        logger.debug("getPropertyById id={}", id);
        PropertyManagementPojo prop = mapper.findById(id);
        logger.debug("Result: {}", prop);
        return prop;
    }

    @Override
    public PropertyManagementPojo getByPropertyName(String propertyName) {
        logger.debug("getByPropertyName propertyName={}", propertyName);
        PropertyManagementPojo prop = mapper.findByPropertyName(propertyName);
        logger.debug("Result: {}", prop);
        return prop;
    }

    @Override
    public PropertyManagementPojo getByPropertyKey(String propertyKey) {
        logger.debug("getByPropertyKey propertyKey={}", propertyKey);
        PropertyManagementPojo prop = mapper.findByPropertyKey(propertyKey);
        logger.debug("Result: {}", prop);
        return prop;
    }

    @Override
    public PropertyManagementPojo createProperty(PropertyManagementPojo property) {
        logger.debug("createProperty: {}", property);
        updateDbAndInvalidateCache("createProperty", property, () -> mapper.insertProperty(property));
        return property;
    }

    @Override
    public PropertyManagementPojo updatePropertyById(Long id, PropertyManagementPojo property) {
        property.setId(id);
        updateDbAndInvalidateCache("updatePropertyById", property, () -> mapper.updateProperty(property));
        return property;
    }

    @Override
    public PropertyManagementPojo updatePropertyByPropertyName(String propertyName, PropertyManagementPojo property) {
        property.setPropertyName(propertyName);
        updateDbAndInvalidateCache("updatePropertyByPropertyName", property, () -> mapper.updatePropertyByPropertyName(property));
        return property;
    }

    @Override
    public PropertyManagementPojo updatePropertyByPropertyKey(String propertyKey, PropertyManagementPojo property) {
        property.setPropertyKey(propertyKey);
        updateDbAndInvalidateCache("updatePropertyByPropertyKey", property, () -> mapper.updatePropertyByPropertyKey(property));
        return property;
    }

    @Override
    public void deletePropertyById(Long id) {
        logger.debug("deletePropertyById id={}", id);
        mapper.deleteById(id);
        logger.debug("Property deleted by ID: {}", id);
    }

    @Override
    public void deletePropertyByPropertyName(String propertyName) {
        logger.debug("deletePropertyByPropertyName propertyName={}", propertyName);
        mapper.deleteByPropertyName(propertyName);
        logger.debug("Property deleted by propertyName: {}", propertyName);
    }

    @Override
    public void deletePropertyByPropertyKey(String propertyKey) {
        logger.debug("deletePropertyByPropertyKey propertyKey={}", propertyKey);
        mapper.deleteByPropertyKey(propertyKey);
        logger.debug("Property deleted by propertyKey: {}", propertyKey);
    }

    // ---------------- SEARCH / LIST ----------------
    @Override
    public PagedResult<PropertyManagementPojo> searchProperties(Map<String, String> params) {
        logger.debug("#############");
        logger.debug("searchProperties called with params={}", params);

        int page = 0;
        int size = 20;
        try {
            page = Integer.parseInt(params.getOrDefault("page", "0"));
        } catch (NumberFormatException e) {
            logger.warn("Invalid page parameter: {}, defaulting to 0", params.get("page"), e);
        }
        try {
            size = Integer.parseInt(params.getOrDefault("size", "20"));
        } catch (NumberFormatException e) {
            logger.warn("Invalid size parameter: {}, defaulting to 20", params.get("size"), e);
        }
        int offset = page * size;

        Map<String, Object> sqlParams = new java.util.HashMap<>();
        sqlParams.putAll(params);
        sqlParams.put("offset", offset);
        sqlParams.put("limit", size);

        // Resolve sortField
        String sortField = params.get("sortField");
        String sortDirection = params.getOrDefault("sortDirection", "ASC").toUpperCase();
        sqlParams.put("sortField", sortField);
        sqlParams.put("sortDirection", sortDirection);

        logger.debug("searchProperties sqlParams={}", sqlParams);

        List<PropertyManagementPojo> items = null;
        long totalCount = 0;

        try {
            items = mapper.findProperties(sqlParams);
            logger.debug("searchProperties items={}", items);
            totalCount = mapper.countProperties(sqlParams);
            logger.debug("searchProperties totalCount={}", totalCount);
        } catch (Exception e) {
            logger.error("Error executing searchProperties query with params={}", sqlParams, e);
            throw new RuntimeException("Database error during searchProperties", e);
        }

        PagedResult<PropertyManagementPojo> result = new PagedResult<>(items, totalCount, page, size);
        logger.debug("searchProperties result={}", result);
        logger.debug("#############");

        return result;
    }

    // ---------------- Helper Method ----------------
    private void updateDbAndInvalidateCache(String actionDesc, PropertyManagementPojo property, Runnable dbUpdateAction) {
        logger.debug("#############");
        logger.debug("{} property={}", actionDesc, property);

        // 2. Update DB
        dbUpdateAction.run();
        logger.debug("Property updated in DB: {}", property);

        // 3. Invalidate cache
        cache.invalidate(property.getPropertyKey());
        logger.debug("Cache invalidated for key={}", property.getPropertyKey());
        logger.debug("#############");
    }

    // ---------------- Helper Method for sort mapping ----------------
    private String resolveSortField(String frontEndField) {
        Map<String, String> sortFieldMap = Map.of(
            "propertyName", "property_name",
            "propertyKey", "property_key",
            "type", "type",
            "value", "value",
            "id", "id"
        );

        String dbColumn = sortFieldMap.get(frontEndField);
        if (dbColumn == null) {
            logger.debug("Invalid sortField '{}', defaulting to 'id'", frontEndField);
            dbColumn = "id";
        } else {
            logger.debug("Resolved sortField '{}' -> '{}'", frontEndField, dbColumn);
        }
        return dbColumn;
    }
}
