package simple.chatgpt.service.management;

import java.math.BigDecimal;
import java.util.HashMap;
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
import simple.chatgpt.util.ParamWrapper;
import simple.chatgpt.util.PropertyKey;
import simple.chatgpt.util.SafeConverter;

@Service
public class PropertyManagementServiceImpl implements PropertyManagementService {

    private static final Logger logger = LogManager.getLogger(PropertyManagementServiceImpl.class);

    private final PropertyManagementMapper mapper;
    private final GenericCache<String, PropertyManagementPojo> cache;
    private final Validator validator;

    @Autowired
    public PropertyManagementServiceImpl(PropertyManagementMapper mapper,
                                         @Qualifier("propertyCache") GenericCache<String, PropertyManagementPojo> cache) {
        logger.debug("PropertyManagementServiceImpl constructor called");
        logger.debug("PropertyManagementServiceImpl mapper={}", mapper);
        logger.debug("PropertyManagementServiceImpl cache={}", cache);

        this.mapper = mapper;
        this.cache = cache;

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
        logger.debug("PropertyManagementServiceImpl validator initialized");
    }

    @PostConstruct
    private void initDefaults() {
        logger.debug("initDefaults called");

        if (mapper == null) {
            logger.error("initDefaults mapper is null, cannot initialize defaults");
            return;
        }

        for (PropertyKey key : PropertyKey.values()) {
            PropertyManagementPojo existing = mapper.findByPropertyKey(key.getKey());
            logger.debug("initDefaults checking key={} existing={}", key, existing);

            if (existing == null) {
                PropertyManagementPojo prop = new PropertyManagementPojo();
                prop.setPropertyKey(key.getKey());
                prop.setPropertyName(key.getKey());
                prop.setType(key.getTypeName());
                prop.setValue(String.valueOf(key.getDefaultValue()));
                mapper.insertProperty(prop);
                logger.debug("initDefaults inserted default property={}", prop);
            }
        }
    }

    private PropertyManagementPojo getCachedProperty(PropertyKey key) {
        logger.debug("getCachedProperty called");
        logger.debug("getCachedProperty key={}", key);

        return cache.get(key.getKey(), k -> {
            logger.debug("getCachedProperty not found in cache, fetching from DB for key={}", k);
            PropertyManagementPojo prop = mapper.findByPropertyKey(k);
            logger.debug("getCachedProperty fetched prop={}", prop);

            if (prop != null) {
                return prop;
            }

            for (PropertyKey pk : PropertyKey.values()) {
                if (pk.getKey().equals(k)) {
                    PropertyManagementPojo defaultProp = new PropertyManagementPojo();
                    defaultProp.setPropertyKey(pk.getKey());
                    defaultProp.setPropertyName(pk.getKey());
                    defaultProp.setType(pk.getTypeName());
                    defaultProp.setValue(String.valueOf(pk.getDefaultValue()));
                    logger.debug("getCachedProperty using defaultProp={}", defaultProp);
                    return defaultProp;
                }
            }
            logger.debug("getCachedProperty key={} not found anywhere", k);
            return null;
        });
    }

    // ---------------- Typed Getters ----------------
    @Override
    public boolean getBoolean(PropertyKey key) {
        logger.debug("getBoolean called");
        logger.debug("getBoolean key={}", key);

        PropertyManagementPojo prop = getCachedProperty(key);
        logger.debug("getBoolean prop={}", prop);

        boolean value = Boolean.parseBoolean(prop.getValue());
        logger.debug("getBoolean key={} -> {}", key.getKey(), value);
        return value;
    }

    @Override
    public int getInteger(PropertyKey key) {
        logger.debug("getInteger called");
        logger.debug("getInteger key={}", key);

        PropertyManagementPojo prop = getCachedProperty(key);
        logger.debug("getInteger prop={}", prop);

        int value;
        try {
            value = Integer.parseInt(prop.getValue());
        } catch (Exception e) {
            logger.debug("getInteger invalid integer for key={} value='{}', defaulting to 0", key.getKey(), prop.getValue());
            value = 0;
        }
        logger.debug("getInteger result key={} value={}", key.getKey(), value);
        return value;
    }

    @Override
    public BigDecimal getDecimal(PropertyKey key) {
        logger.debug("getDecimal called");
        logger.debug("getDecimal key={}", key);

        PropertyManagementPojo prop = getCachedProperty(key);
        logger.debug("getDecimal prop={}", prop);

        BigDecimal value;
        try {
            value = new BigDecimal(prop.getValue());
        } catch (Exception e) {
            logger.debug("getDecimal invalid decimal for key={} value='{}', defaulting to 0", key.getKey(), prop.getValue());
            value = BigDecimal.ZERO;
        }
        logger.debug("getDecimal result key={} value={}", key.getKey(), value);
        return value;
    }

    @Override
    public String getString(PropertyKey key) {
        logger.debug("getString called");
        logger.debug("getString key={}", key);

        PropertyManagementPojo prop = getCachedProperty(key);
        logger.debug("getString prop={}", prop);

        logger.debug("getString result key={} value={}", key.getKey(), prop.getValue());
        return prop.getValue();
    }

    // ---------------- Update Property ----------------
    
    /*
    HUNG : DOMT REMOVE
    note to myself
    1>Majority of validation (~70-80%) is done in controllers.
      Service-level validation (~20-30%) is mainly for extra safety, 
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
        logger.debug("updateProperty called");
        logger.debug("updateProperty key={}", key);
        logger.debug("updateProperty newValue={}", newValue);

        PropertyManagementPojo prop = new PropertyManagementPojo();
        prop.setPropertyKey(key.getKey());
        prop.setType(key.getTypeName());
        prop.setValue(newValue);

        Set<ConstraintViolation<PropertyManagementPojo>> violations = validator.validate(prop);
        logger.debug("updateProperty validation violations={}", violations);

        if (!violations.isEmpty()) {
            for (ConstraintViolation<PropertyManagementPojo> violation : violations) {
                logger.debug("updateProperty validation error: {}", violation.getMessage());
            }
            throw new IllegalArgumentException("Property validation failed.");
        }

        mapper.updatePropertyByPropertyKey(prop);
        logger.debug("updateProperty DB updated prop={}", prop);

        cache.invalidate(key.getKey());
        logger.debug("updateProperty cache invalidated key={}", key.getKey());
    }

    // ---------------- CRUD ----------------
    @Override
    public PropertyManagementPojo getPropertyById(Long id) {
        logger.debug("getPropertyById called");
        logger.debug("getPropertyById id={}", id);

        PropertyManagementPojo prop = mapper.findById(id);
        logger.debug("getPropertyById result={}", prop);
        return prop;
    }

    @Override
    public PropertyManagementPojo getByPropertyName(String propertyName) {
        logger.debug("getByPropertyName called");
        logger.debug("getByPropertyName propertyName={}", propertyName);

        PropertyManagementPojo prop = mapper.findByPropertyName(propertyName);
        logger.debug("getByPropertyName result={}", prop);
        return prop;
    }

    @Override
    public PropertyManagementPojo getByPropertyKey(String propertyKey) {
        logger.debug("getByPropertyKey called");
        logger.debug("getByPropertyKey propertyKey={}", propertyKey);

        PropertyManagementPojo prop = mapper.findByPropertyKey(propertyKey);
        logger.debug("getByPropertyKey result={}", prop);
        return prop;
    }

    @Override
    public PropertyManagementPojo createProperty(PropertyManagementPojo property) {
        logger.debug("createProperty called");
        logger.debug("createProperty property={}", property);

        updateDbAndInvalidateCache("createProperty", property, () -> mapper.insertProperty(property));
        return property;
    }

    @Override
    public PropertyManagementPojo updatePropertyById(Long id, PropertyManagementPojo property) {
        logger.debug("updatePropertyById called");
        logger.debug("updatePropertyById id={}", id);
        logger.debug("updatePropertyById property={}", property);

        property.setId(id);
        updateDbAndInvalidateCache("updatePropertyById", property, () -> mapper.updateProperty(property));
        return property;
    }

    @Override
    public PropertyManagementPojo updatePropertyByPropertyName(String propertyName, PropertyManagementPojo property) {
        logger.debug("updatePropertyByPropertyName called");
        logger.debug("updatePropertyByPropertyName propertyName={}", propertyName);
        logger.debug("updatePropertyByPropertyName property={}", property);

        property.setPropertyName(propertyName);
        updateDbAndInvalidateCache("updatePropertyByPropertyName", property, () -> mapper.updatePropertyByPropertyName(property));
        return property;
    }

    @Override
    public PropertyManagementPojo updatePropertyByPropertyKey(String propertyKey, PropertyManagementPojo property) {
        logger.debug("updatePropertyByPropertyKey called");
        logger.debug("updatePropertyByPropertyKey propertyKey={}", propertyKey);
        logger.debug("updatePropertyByPropertyKey property={}", property);

        property.setPropertyKey(propertyKey);
        updateDbAndInvalidateCache("updatePropertyByPropertyKey", property, () -> mapper.updatePropertyByPropertyKey(property));
        return property;
    }

    @Override
    public void deletePropertyById(Long id) {
        logger.debug("deletePropertyById called");
        logger.debug("deletePropertyById id={}", id);

        mapper.deleteById(id);
        logger.debug("deletePropertyById completed id={}", id);
    }

    @Override
    public void deletePropertyByPropertyName(String propertyName) {
        logger.debug("deletePropertyByPropertyName called");
        logger.debug("deletePropertyByPropertyName propertyName={}", propertyName);

        mapper.deleteByPropertyName(propertyName);
        logger.debug("deletePropertyByPropertyName completed propertyName={}", propertyName);
    }

    @Override
    public void deletePropertyByPropertyKey(String propertyKey) {
        logger.debug("deletePropertyByPropertyKey called");
        logger.debug("deletePropertyByPropertyKey propertyKey={}", propertyKey);

        mapper.deleteByPropertyKey(propertyKey);
        logger.debug("deletePropertyByPropertyKey completed propertyKey={}", propertyKey);
    }

    // ---------------- SEARCH ----------------
    @Override
    public PagedResult<PropertyManagementPojo> searchProperties(Map<String, String> params) {
        logger.debug("searchProperties called");
        logger.debug("searchProperties params={}", params);

        /*
        hung: DONT REMOVE THIS CODE
        */
        int page = 0;
        int size = 20;
        try {
            page = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "page", 0), 0); 
        } catch (NumberFormatException e) {
            logger.warn("Invalid page param {}, defaulting to 0", ParamWrapper.unwrap(params, "page", 0), e);
        }
        try {
            size = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "size", 20), 20);
        } catch (NumberFormatException e) {
            logger.warn("Invalid size param {}, defaulting to 20", ParamWrapper.unwrap(params, "size", 20), e);
        }
        int offset = page * size;

        Map<String, Object> sqlParams = new HashMap<>(params);
        sqlParams.put("offset", offset);
        sqlParams.put("limit", size);
        sqlParams.put("sortField", params.getOrDefault("sortField", "key"));
        sqlParams.put("sortDirection", params.getOrDefault("sortDirection", "ASC").toUpperCase());

        logger.debug("searchProperties sqlParams={}", sqlParams);

        List<PropertyManagementPojo> items;
        long totalCount;

        try {
            items = mapper.findProperties(sqlParams);
            logger.debug("searchProperties items={}", items);

            totalCount = mapper.countProperties(sqlParams);
            logger.debug("searchProperties totalCount={}", totalCount);
        } catch (Exception e) {
            logger.error("searchProperties DB error sqlParams={}", sqlParams, e);
            throw new RuntimeException("Database error during searchProperties", e);
        }

        PagedResult<PropertyManagementPojo> result = new PagedResult<>(items, totalCount, page, size);
        logger.debug("searchProperties result={}", result);
        return result;
    }

    // ---------------- Helper ----------------
    private void updateDbAndInvalidateCache(String actionDesc, PropertyManagementPojo property, Runnable dbUpdateAction) {
        logger.debug("updateDbAndInvalidateCache called");
        logger.debug("updateDbAndInvalidateCache actionDesc={}", actionDesc);
        logger.debug("updateDbAndInvalidateCache property={}", property);

        dbUpdateAction.run();
        logger.debug("{} DB update complete property={}", actionDesc, property);

        cache.invalidate(property.getPropertyKey());
        logger.debug("{} cache invalidated key={}", actionDesc, property.getPropertyKey());
    }
}
