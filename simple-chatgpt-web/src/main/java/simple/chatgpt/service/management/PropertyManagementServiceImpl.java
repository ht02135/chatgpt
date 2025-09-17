package simple.chatgpt.service.management;

import java.math.BigDecimal;
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
    }

    private PropertyManagementPojo getCachedProperty(PropertyKey key) {
        return cache.get(key.getKey(), k -> {
            logger.debug("getCachedProperty not in cache, fetching from DB: {}", k);
            PropertyManagementPojo prop = mapper.findByPropertyKey(k);
            if (prop != null) return prop;

            // Fallback to default
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
    }

    // ---------------- Typed Getters ----------------
    @Override
    public boolean getBoolean(PropertyKey key) {
        PropertyManagementPojo prop = getCachedProperty(key);
        return Boolean.parseBoolean(prop.getValue());
    }

    @Override
    public int getInteger(PropertyKey key) {
        PropertyManagementPojo prop = getCachedProperty(key);
        try {
            return Integer.parseInt(prop.getValue());
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public BigDecimal getDecimal(PropertyKey key) {
        PropertyManagementPojo prop = getCachedProperty(key);
        try {
            return new BigDecimal(prop.getValue());
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    @Override
    public String getString(PropertyKey key) {
        PropertyManagementPojo prop = getCachedProperty(key);
        return prop.getValue();
    }

    // ---------------- Update Property ----------------
    @Override
    public void updateProperty(PropertyKey key, String newValue) {
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

        // 3. Invalidate cache
        cache.invalidate(key.getKey());
    }

    // ---------------- CRUD Operations ----------------
    @Override
    public PropertyManagementPojo getPropertyById(Long id) {
        return mapper.findById(id);
    }

    @Override
    public PropertyManagementPojo getByPropertyName(String propertyName) {
        return mapper.findByPropertyName(propertyName);
    }

    @Override
    public PropertyManagementPojo getByPropertyKey(String propertyKey) {
        return mapper.findByPropertyKey(propertyKey);
    }

    @Override
    public PropertyManagementPojo createProperty(PropertyManagementPojo property) {
        mapper.insertProperty(property);
        return property;
    }

    @Override
    public PropertyManagementPojo updatePropertyById(Long id, PropertyManagementPojo property) {
        property.setId(id);
        mapper.updateProperty(property);
        return property;
    }

    @Override
    public PropertyManagementPojo updatePropertyByPropertyName(String propertyName, PropertyManagementPojo property) {
        property.setPropertyName(propertyName);
        mapper.updatePropertyByPropertyName(property);
        return property;
    }

    @Override
    public PropertyManagementPojo updatePropertyByPropertyKey(String propertyKey, PropertyManagementPojo property) {
        property.setPropertyKey(propertyKey);
        mapper.updatePropertyByPropertyKey(property);
        return property;
    }

    @Override
    public void deletePropertyById(Long id) {
        mapper.deleteById(id);
    }

    @Override
    public void deletePropertyByPropertyName(String propertyName) {
        mapper.deleteByPropertyName(propertyName);
    }

    @Override
    public void deletePropertyByPropertyKey(String propertyKey) {
        mapper.deleteByPropertyKey(propertyKey);
    }

    @Override
    public PagedResult<PropertyManagementPojo> searchProperties(Map<String, String> params) {
        // Example: implement pagination using mapper.findProperties and mapper.countProperties
        // Customize according to your PagedResult implementation
        return null;
    }
}
