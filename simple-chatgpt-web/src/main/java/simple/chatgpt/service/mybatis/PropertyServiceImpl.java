package simple.chatgpt.service.mybatis;

import java.math.BigDecimal;
import java.util.List;
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

import simple.chatgpt.mapper.PropertyMapper;
import simple.chatgpt.pojo.mybatis.Property;
import simple.chatgpt.util.GenericCache;
import simple.chatgpt.util.PropertyKey;

@Service
public class PropertyServiceImpl implements PropertyService {
	private static final Logger logger = LogManager.getLogger(PropertyServiceImpl.class);
	
	/*
	Recommendation (best practice in Spring Boot 3 / modern apps):
	1>Use constructor injection with final fields (your PropertyServiceImpl 
	is already a good example).
	2>Avoid field injection with @Autowired unless you’re wiring in test 
	code or legacy beans.
	*/
	private final Validator validator;
    private final PropertyMapper mapper;
    private final GenericCache<String, Property> cache;

    @Autowired
    public PropertyServiceImpl(PropertyMapper mapper, @Qualifier("propertyCache") GenericCache<String, Property> propertyCache) {
    	ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
        
        this.mapper = mapper;
        this.cache = propertyCache;
        initDefaults();
    }

    @PostConstruct
    private void initDefaults() {
        for (PropertyKey key : PropertyKey.values()) {
            Property existing = mapper.selectByKey(key.getKey());
            if (existing == null) {
                mapper.insertPropertyFull(key.getKey(), key.getTypeName(), String.valueOf(key.getDefaultValue()));
            }
        }
    }

    private Property getCachedProperty(PropertyKey key) {
        return cache.get(key.getKey(), k -> {
        	logger.debug("getCachedProperty not in cache fetch from db k : {}", k);
            Property prop = mapper.selectByKey(k);
            logger.debug("#############");
            logger.debug("getCachedProperty not in cache fetch from db prop : {}", prop);
            logger.debug("#############");
            if (prop != null) {
                cache.put(k, prop);
                return prop;
            }
            // Not found in DB, use enum default
            PropertyKey enumKey = null;
            for (PropertyKey pk : PropertyKey.values()) {
                if (pk.getKey().equals(k)) {
                    enumKey = pk;
                    break;
                }
            }
            Property defaultProp = (enumKey == null)
                ? new Property(k, "String", null)
                : new Property(enumKey.getKey(), enumKey.getTypeName(), String.valueOf(enumKey.getDefaultValue()));
            logger.debug("#############");
            logger.debug("getCachedProperty not in db fetch from default defaultProp : {}", defaultProp);
            logger.debug("#############");
            cache.put(k, defaultProp);
            return defaultProp;
        });
    }

    @Override
    public List<Property> getAllProperties() {
        return mapper.selectAllProperties();
    }

    @Override
    public void updateProperty(PropertyKey key, String newValue) {
    	logger.debug("updateProperty key: {}", key);
    	logger.debug("updateProperty newValue: {}", newValue);
    	
    	/*
    	per chatgpt, order of operation is
    	1. Validate
    	2. Update DB  
		3. Invalidate cache
    	*/
    	
    	//1. Validate
    	Property prop = new Property(key.getKey(), key.getTypeName(), newValue);
    	logger.debug("#############");
    	logger.debug("updateProperty prop: {}", prop);
    	logger.debug("#############");
    	
        Set<ConstraintViolation<Property>> violations = validator.validate(prop);
        if (!violations.isEmpty()) {
            // Handle validation errors
            for (ConstraintViolation<Property> violation : violations) {
            	logger.debug("Validation Error: {} for property value '{}'", violation.getMessage(), prop.getValue());
            }
            throw new IllegalArgumentException("Property validation failed.");
        }

    	//2. Update DB 
        mapper.updateProperty(key.getKey(), newValue);
        
    	//3. Invalidate cache
    	cache.invalidate(key.getKey());
    	
        //4. Update cache is avoid
        //cache.put(key.getKey(), prop); // update cache with Property object
    }

    @Override
    public boolean getBoolean(PropertyKey key) {
        Property prop = getCachedProperty(key);
        logger.debug("getBoolean prop '{}'", prop);
        return Boolean.parseBoolean(prop.getValue());
    }

    @Override
    public int getInteger(PropertyKey key) {
        Property prop = getCachedProperty(key);
        try {
            return Integer.parseInt(prop.getValue());
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public BigDecimal getDecimal(PropertyKey key) {
        Property prop = getCachedProperty(key);
        try {
            return new BigDecimal(prop.getValue());
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    @Override
    public String getString(PropertyKey key) {
        Property prop = getCachedProperty(key);
        return prop.getValue();
    }

    @Override
    public List<Property> getProperties(String key, String type, int page, int size, String sort, String order) {
        int offset = (page - 1) * size;
        return mapper.selectPropertiesPaged(key, type, offset, size, sort, order);
    }

    @Override
    public int countProperties(String key, String type) {
        return mapper.countProperties(key, type);
    }
}