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
				propertyMapper.insertProperty(prop);
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

        List<PropertyManagementPojo> items = propertyMapper.search((Map) params);
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
		logger.debug("updateProperty START");
		logger.debug("updateProperty key={}", key);
		logger.debug("updateProperty newValue={}", newValue);

		PropertyManagementPojo prop = new PropertyManagementPojo();
		prop.setPropertyKey(key.getKey());
		prop.setType(key.getTypeName());
		prop.setValue(newValue);

		Set<ConstraintViolation<PropertyManagementPojo>> violations = validator.validate(prop);
		if (!violations.isEmpty()) {
			throw new IllegalArgumentException("Property validation failed.");
		}

		propertyMapper.updatePropertyByPropertyKey(prop);
		cache.invalidate(key.getKey());

		logger.debug("updateProperty DONE");
	}

	// ---------------- CRUD ----------------
	@Override
	public PropertyManagementPojo getPropertyById(Long id) {
		logger.debug("getPropertyById START");
		logger.debug("getPropertyById id={}", id);

		PropertyManagementPojo prop = propertyMapper.findById(id);

		logger.debug("getPropertyById DONE");
		return prop;
	}

	@Override
	public PropertyManagementPojo getByPropertyName(String propertyName) {
		logger.debug("getByPropertyName START");
		logger.debug("getByPropertyName propertyName={}", propertyName);

		PropertyManagementPojo prop = propertyMapper.findByPropertyName(propertyName);

		logger.debug("getByPropertyName DONE");
		return prop;
	}

	@Override
	public PropertyManagementPojo getByPropertyKey(String propertyKey) {
		logger.debug("getByPropertyKey START");
		logger.debug("getByPropertyKey propertyKey={}", propertyKey);

		PropertyManagementPojo prop = propertyMapper.findByPropertyKey(propertyKey);

		logger.debug("getByPropertyKey DONE");
		return prop;
	}

	@Override
	public PropertyManagementPojo createProperty(PropertyManagementPojo property) {
		logger.debug("createProperty START");
		logger.debug("createProperty property={}", property);

		updateDbAndInvalidateCache("createProperty", property, () -> propertyMapper.insertProperty(property));

		logger.debug("createProperty DONE");
		return property;
	}

	@Override
	public PropertyManagementPojo updatePropertyById(Long id, PropertyManagementPojo property) {
		logger.debug("updatePropertyById START");
		logger.debug("updatePropertyById id={}", id);
		logger.debug("updatePropertyById property={}", property);

		property.setId(id);
		updateDbAndInvalidateCache("updatePropertyById", property, () -> propertyMapper.updateProperty(property));

		logger.debug("updatePropertyById DONE");
		return property;
	}

	@Override
	public PropertyManagementPojo updatePropertyByPropertyName(String propertyName, PropertyManagementPojo property) {
		logger.debug("updatePropertyByPropertyName START");
		logger.debug("updatePropertyByPropertyName propertyName={}", propertyName);
		logger.debug("updatePropertyByPropertyName property={}", property);

		property.setPropertyName(propertyName);
		updateDbAndInvalidateCache("updatePropertyByPropertyName", property,
				() -> propertyMapper.updatePropertyByPropertyName(property));

		logger.debug("updatePropertyByPropertyName DONE");
		return property;
	}

	@Override
	public PropertyManagementPojo updatePropertyByPropertyKey(String propertyKey, PropertyManagementPojo property) {
		logger.debug("updatePropertyByPropertyKey START");
		logger.debug("updatePropertyByPropertyKey propertyKey={}", propertyKey);
		logger.debug("updatePropertyByPropertyKey property={}", property);

		property.setPropertyKey(propertyKey);
		updateDbAndInvalidateCache("updatePropertyByPropertyKey", property,
				() -> propertyMapper.updatePropertyByPropertyKey(property));

		logger.debug("updatePropertyByPropertyKey DONE");
		return property;
	}

	@Override
	public void deletePropertyById(Long id) {
		logger.debug("deletePropertyById START");
		logger.debug("deletePropertyById id={}", id);

		propertyMapper.deleteById(id);

		logger.debug("deletePropertyById DONE");
	}

	@Override
	public void deletePropertyByPropertyName(String propertyName) {
		logger.debug("deletePropertyByPropertyName START");
		logger.debug("deletePropertyByPropertyName propertyName={}", propertyName);

		propertyMapper.deleteByPropertyName(propertyName);

		logger.debug("deletePropertyByPropertyName DONE");
	}

	@Override
	public void deletePropertyByPropertyKey(String propertyKey) {
		logger.debug("deletePropertyByPropertyKey START");
		logger.debug("deletePropertyByPropertyKey propertyKey={}", propertyKey);

		propertyMapper.deleteByPropertyKey(propertyKey);

		logger.debug("deletePropertyByPropertyKey DONE");
	}

	// ---------------- SEARCH ----------------

	@Override
	public PagedResult<PropertyManagementPojo> searchProperties(Map<String, String> params) {
		logger.debug("searchProperties START");
		logger.debug("searchProperties params={}", params);

		// hung: DONT REMOVE THIS CODE
		int page = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "page", 0), 0);
		int size = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "size", 20), 20);
		int offset = page * size;

		Map<String, Object> sqlParams = new HashMap<>(params);
		sqlParams.put("offset", offset);
		sqlParams.put("limit", size);
		sqlParams.put("sortField", ParamWrapper.unwrap(params, "sortField", "key"));
		sqlParams.put("sortDirection", ParamWrapper.unwrap(params, "sortDirection", "ASC").toUpperCase());

		List<PropertyManagementPojo> items;
		long totalCount;

		try {
			items = propertyMapper.findProperties(sqlParams);
			totalCount = propertyMapper.countProperties(sqlParams);
		} catch (Exception e) {
			logger.error("searchProperties DB error", e);
			throw new RuntimeException("Database error during searchProperties", e);
		}

		PagedResult<PropertyManagementPojo> result = new PagedResult<>(items, totalCount, page, size);
		logger.debug("searchProperties result={}", result);
		return result;
	}

	// ---------------- Helper ----------------
	private void updateDbAndInvalidateCache(String actionDesc, PropertyManagementPojo property,
			Runnable dbUpdateAction) {
		logger.debug("updateDbAndInvalidateCache START");
		logger.debug("updateDbAndInvalidateCache actionDesc={}", actionDesc);
		logger.debug("updateDbAndInvalidateCache property={}", property);

		dbUpdateAction.run();
		cache.invalidate(property.getPropertyKey());

		logger.debug("updateDbAndInvalidateCache DONE");
	}
}
