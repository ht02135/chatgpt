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
        logger.debug("initDefaults START");

        if (mapper == null) {
            logger.error("initDefaults mapper is null, cannot initialize defaults");
            return;
        }

        for (PropertyKey key : PropertyKey.values()) {
            PropertyManagementPojo existing = mapper.findByPropertyKey(key.getKey());
            if (existing == null) {
                PropertyManagementPojo prop = new PropertyManagementPojo();
                prop.setPropertyKey(key.getKey());
                prop.setPropertyName(key.getKey());
                prop.setType(key.getTypeName());
                prop.setValue(String.valueOf(key.getDefaultValue()));
                mapper.insertProperty(prop);
            }
        }

        logger.debug("initDefaults DONE");
    }

    private PropertyManagementPojo getCachedProperty(PropertyKey key) {
        logger.debug("getCachedProperty START");
        logger.debug("getCachedProperty key={}", key);

        PropertyManagementPojo result = cache.get(key.getKey(), k -> {
            PropertyManagementPojo prop = mapper.findByPropertyKey(k);
            if (prop != null) return prop;

            for (PropertyKey pk : PropertyKey.values()) {
                if (pk.getKey().equals(k)) {
                    PropertyManagementPojo defaultProp = new PropertyManagementPojo();
                    defaultProp.setPropertyKey(pk.getKey());
                    defaultProp.setPropertyName(pk.getKey());
                    defaultProp.setType(pk.getTypeName());
                    defaultProp.setValue(String.valueOf(pk.getDefaultValue()));
                    return defaultProp;
                }
            }
            return null;
        });

        logger.debug("getCachedProperty DONE result={}", result);
        return result;
    }

    // ---------------- Typed Getters ----------------
    @Override
    public boolean getBoolean(PropertyKey key) {
        logger.debug("getBoolean START key={}", key);
        PropertyManagementPojo prop = getCachedProperty(key);
        boolean value = Boolean.parseBoolean(prop.getValue());
        logger.debug("getBoolean DONE value={}", value);
        return value;
    }

    @Override
    public int getInteger(PropertyKey key) {
        logger.debug("getInteger START key={}", key);
        PropertyManagementPojo prop = getCachedProperty(key);
        int value;
        try {
            value = Integer.parseInt(prop.getValue());
        } catch (Exception e) {
            value = 0;
        }
        logger.debug("getInteger DONE value={}", value);
        return value;
    }

    @Override
    public BigDecimal getDecimal(PropertyKey key) {
        logger.debug("getDecimal START key={}", key);
        PropertyManagementPojo prop = getCachedProperty(key);
        BigDecimal value;
        try {
            value = new BigDecimal(prop.getValue());
        } catch (Exception e) {
            value = BigDecimal.ZERO;
        }
        logger.debug("getDecimal DONE value={}", value);
        return value;
    }

    @Override
    public String getString(PropertyKey key) {
        logger.debug("getString START key={}", key);
        PropertyManagementPojo prop = getCachedProperty(key);
        logger.debug("getString DONE value={}", prop.getValue());
        return prop.getValue();
    }

    // ---------------- Update Property ----------------
    @Override
    public void updateProperty(PropertyKey key, String newValue) {
        logger.debug("updateProperty START key={} newValue={}", key, newValue);

        PropertyManagementPojo prop = new PropertyManagementPojo();
        prop.setPropertyKey(key.getKey());
        prop.setType(key.getTypeName());
        prop.setValue(newValue);

        Set<ConstraintViolation<PropertyManagementPojo>> violations = validator.validate(prop);
        if (!violations.isEmpty()) {
            throw new IllegalArgumentException("Property validation failed.");
        }

        mapper.updatePropertyByPropertyKey(prop);
        cache.invalidate(key.getKey());

        logger.debug("updateProperty DONE");
    }

    // ---------------- CRUD ----------------
    @Override
    public PropertyManagementPojo getPropertyById(Long id) {
        logger.debug("getPropertyById START id={}", id);
        PropertyManagementPojo prop = mapper.findById(id);
        logger.debug("getPropertyById DONE prop={}", prop);
        return prop;
    }

    @Override
    public PropertyManagementPojo getByPropertyName(String propertyName) {
        logger.debug("getByPropertyName START propertyName={}", propertyName);
        PropertyManagementPojo prop = mapper.findByPropertyName(propertyName);
        logger.debug("getByPropertyName DONE prop={}", prop);
        return prop;
    }

    @Override
    public PropertyManagementPojo getByPropertyKey(String propertyKey) {
        logger.debug("getByPropertyKey START propertyKey={}", propertyKey);
        PropertyManagementPojo prop = mapper.findByPropertyKey(propertyKey);
        logger.debug("getByPropertyKey DONE prop={}", prop);
        return prop;
    }

    @Override
    public PropertyManagementPojo createProperty(PropertyManagementPojo property) {
        logger.debug("createProperty START property={}", property);
        updateDbAndInvalidateCache("createProperty", property, () -> mapper.insertProperty(property));
        logger.debug("createProperty DONE property={}", property);
        return property;
    }

    @Override
    public PropertyManagementPojo updatePropertyById(Long id, PropertyManagementPojo property) {
        logger.debug("updatePropertyById START id={} property={}", id, property);
        property.setId(id);
        updateDbAndInvalidateCache("updatePropertyById", property, () -> mapper.updateProperty(property));
        logger.debug("updatePropertyById DONE property={}", property);
        return property;
    }

    @Override
    public PropertyManagementPojo updatePropertyByPropertyName(String propertyName, PropertyManagementPojo property) {
        logger.debug("updatePropertyByPropertyName START propertyName={} property={}", propertyName, property);
        property.setPropertyName(propertyName);
        updateDbAndInvalidateCache("updatePropertyByPropertyName", property,
                () -> mapper.updatePropertyByPropertyName(property));
        logger.debug("updatePropertyByPropertyName DONE property={}", property);
        return property;
    }

    @Override
    public PropertyManagementPojo updatePropertyByPropertyKey(String propertyKey, PropertyManagementPojo property) {
        logger.debug("updatePropertyByPropertyKey START propertyKey={} property={}", propertyKey, property);
        property.setPropertyKey(propertyKey);
        updateDbAndInvalidateCache("updatePropertyByPropertyKey", property,
                () -> mapper.updatePropertyByPropertyKey(property));
        logger.debug("updatePropertyByPropertyKey DONE property={}", property);
        return property;
    }

    @Override
    public void deletePropertyById(Long id) {
        logger.debug("deletePropertyById START id={}", id);
        mapper.deleteById(id);
        logger.debug("deletePropertyById DONE");
    }

    @Override
    public void deletePropertyByPropertyName(String propertyName) {
        logger.debug("deletePropertyByPropertyName START propertyName={}", propertyName);
        mapper.deleteByPropertyName(propertyName);
        logger.debug("deletePropertyByPropertyName DONE");
    }

    @Override
    public void deletePropertyByPropertyKey(String propertyKey) {
        logger.debug("deletePropertyByPropertyKey START propertyKey={}", propertyKey);
        mapper.deleteByPropertyKey(propertyKey);
        logger.debug("deletePropertyByPropertyKey DONE");
    }

    // ---------------- SEARCH ----------------
    @Override
    public PagedResult<PropertyManagementPojo> searchProperties(Map<String, String> params) {
        logger.debug("searchProperties START params={}", params);

        int page = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "page", 0), 0);
        int size = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "size", 20), 20);
        int offset = page * size;

        params.put("offset", String.valueOf(offset));
        params.put("limit", String.valueOf(size));
        params.put("sortField", ParamWrapper.unwrap(params, "sortField", "key"));
        params.put("sortDirection", ParamWrapper.unwrap(params, "sortDirection", "ASC").toUpperCase());

        List<PropertyManagementPojo> items;
        long totalCount;
        try {
            items = mapper.findProperties((Map) params);
            totalCount = mapper.countProperties((Map) params);
        } catch (Exception e) {
            logger.error("searchProperties DB error", e);
            throw new RuntimeException("Database error during searchProperties", e);
        }

        PagedResult<PropertyManagementPojo> result = new PagedResult<>(items, totalCount, page, size);
        logger.debug("searchProperties DONE result={}", result);
        return result;
    }

    // ---------------- Helper ----------------
    private void updateDbAndInvalidateCache(String actionDesc, PropertyManagementPojo property, Runnable dbUpdateAction) {
        logger.debug("updateDbAndInvalidateCache START actionDesc={} property={}", actionDesc, property);
        dbUpdateAction.run();
        cache.invalidate(property.getPropertyKey());
        logger.debug("updateDbAndInvalidateCache DONE");
    }
}
