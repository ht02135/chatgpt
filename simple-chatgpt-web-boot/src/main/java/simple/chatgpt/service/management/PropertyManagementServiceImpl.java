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

	private final PropertyManagementMapper propertyMapper;
	private final GenericCache<String, PropertyManagementPojo> cache;
	private final Validator validator;

	@Autowired
	public PropertyManagementServiceImpl(PropertyManagementMapper propertyMapper,
			@Qualifier("propertyCache") GenericCache<String, PropertyManagementPojo> cache) {
		logger.debug("PropertyManagementServiceImpl constructor called");
		logger.debug("PropertyManagementServiceImpl propertyMapper={}", propertyMapper);
		logger.debug("PropertyManagementServiceImpl cache={}", cache);

		this.propertyMapper = propertyMapper;
		this.cache = cache;

		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		this.validator = factory.getValidator();
		logger.debug("PropertyManagementServiceImpl validator initialized");
	}

	@PostConstruct
	private void initDefaults() {
		logger.debug("initDefaults START");

		if (propertyMapper == null) {
			logger.error("initDefaults mapper is null, cannot initialize defaults");
			return;
		}

		for (PropertyKey key : PropertyKey.values()) {
			PropertyManagementPojo existing = propertyMapper.findByPropertyKey(key.getKey());
			if (existing == null) {
				PropertyManagementPojo prop = new PropertyManagementPojo();
				prop.setPropertyKey(key.getKey());
				prop.setPropertyName(key.getKey());
				prop.setType(key.getTypeName());
				prop.setValue(String.valueOf(key.getDefaultValue()));
				propertyMapper.create(prop);
			}
		}

		logger.debug("initDefaults DONE");
	}

    // ==============================================================
    // ================ 5 CORE METHODS (on top) =====================
    // ==============================================================

    @Override
    public PropertyManagementPojo create(PropertyManagementPojo property) {
        logger.debug("create called");
        logger.debug("create property={}", property);
        propertyMapper.create(property);
        return property;
    }

    @Override
    public PropertyManagementPojo update(Long id, PropertyManagementPojo property) {
        logger.debug("update called");
        logger.debug("update id={}", id);
        logger.debug("update property={}", property);
        
        // Invalidate cache first, then update DB
        cache.invalidate(property.getPropertyKey()); // invalidate only
        propertyMapper.update(id, property);
        return property;
    }

    @Override
    public PagedResult<PropertyManagementPojo> search(Map<String, String> params) {
        logger.debug("search called");
        logger.debug("search params={}", params);

        if (!params.containsKey("page")) params.put("page", "0");
        if (!params.containsKey("size")) params.put("size", "20");
        int page = SafeConverter.toIntOrDefault(params.get("page"), 0);
        int size = SafeConverter.toIntOrDefault(params.get("size"), 20);
        int offset = page * size;

        if (!params.containsKey("offset")) params.put("offset", String.valueOf(offset));
        if (!params.containsKey("limit")) params.put("limit", String.valueOf(size));
        if (!params.containsKey("sortField")) params.put("sortField", "id");
        if (!params.containsKey("sortDirection")) params.put("sortDirection", "ASC");
        params.put("sortDirection", params.get("sortDirection").toUpperCase());

        // Hung : mapper expect Map<String, Object> for offset and limit
    	Map<String, Object> mapperParams = new HashMap<>(params);
        mapperParams.put("offset", SafeConverter.toIntOrDefault(params.get("offset"), 0));
        mapperParams.put("limit", SafeConverter.toIntOrDefault(params.get("limit"), 10));
        
        List<PropertyManagementPojo> items = propertyMapper.search(mapperParams);
        long totalCount = items.size();
        PagedResult<PropertyManagementPojo> result = new PagedResult<>(items, totalCount, page, size);
        logger.debug("search return={}", result);
        return result;
    }

    @Override
    public PropertyManagementPojo get(Long id) {
        logger.debug("get called");
        logger.debug("get id={}", id);
        PropertyManagementPojo property = propertyMapper.get(id);
        logger.debug("get return={}", property);
        return property;
    }

    @Override
    public void delete(Long id) {
        logger.debug("delete called");
        logger.debug("delete id={}", id);
        
        // Invalidate cache first, then delete DB
        PropertyManagementPojo property = propertyMapper.get(id);
        if (property != null) {
            cache.invalidate(property.getPropertyKey()); // invalidate only
        }
        propertyMapper.delete(id);
    }

    // ======= OTHER METHODS =======
    
	private PropertyManagementPojo getCachedProperty(PropertyKey key) {
		logger.debug("getCachedProperty START");
		logger.debug("getCachedProperty key={}", key);

		PropertyManagementPojo result = cache.get(key.getKey(), k -> {
			PropertyManagementPojo prop = propertyMapper.findByPropertyKey(k);
			if (prop != null)
				return prop;

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

		logger.debug("getCachedProperty DONE");
		return result;
	}

	// ---------------- Typed Getters ----------------
	@Override
	public boolean getBoolean(PropertyKey key) {
		logger.debug("getBoolean START");
		logger.debug("getBoolean key={}", key);

		PropertyManagementPojo prop = getCachedProperty(key);
		boolean value = Boolean.parseBoolean(prop.getValue());

		logger.debug("getBoolean DONE");
		return value;
	}

	@Override
	public int getInteger(PropertyKey key) {
		logger.debug("getInteger START");
		logger.debug("getInteger key={}", key);

		PropertyManagementPojo prop = getCachedProperty(key);
		int value;
		try {
			value = Integer.parseInt(prop.getValue());
		} catch (Exception e) {
			value = 0;
		}

		logger.debug("getInteger DONE");
		return value;
	}

	@Override
	public BigDecimal getDecimal(PropertyKey key) {
		logger.debug("getDecimal START");
		logger.debug("getDecimal key={}", key);

		PropertyManagementPojo prop = getCachedProperty(key);
		BigDecimal value;
		try {
			value = new BigDecimal(prop.getValue());
		} catch (Exception e) {
			value = BigDecimal.ZERO;
		}

		logger.debug("getDecimal DONE");
		return value;
	}

	@Override
	public String getString(PropertyKey key) {
		logger.debug("getString START");
		logger.debug("getString key={}", key);

		PropertyManagementPojo prop = getCachedProperty(key);

		logger.debug("getString DONE");
		return prop.getValue();
	}

}
