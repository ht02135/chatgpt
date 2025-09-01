package simple.chatgpt.service.mybatis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import simple.chatgpt.mapper.PropertyMapper;
import simple.chatgpt.util.PropertyKey;
import simple.chatgpt.util.GenericCache;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;

@Service
public class PropertyService {
    private final PropertyMapper mapper;
    private final GenericCache<String, String> cache;

    @Autowired
    public PropertyService(PropertyMapper mapper) {
        this.mapper = mapper;
        this.cache = GenericCache.getInstance(30, 1000); // 30 min expiry, 1000 max size
        initDefaults();
    }

    // Initialize defaults into DB and cache
    private void initDefaults() {
        for (PropertyKey key : PropertyKey.values()) {
            String value = mapper.selectValue(key.key());
            if (value == null) {
                String defaultVal = key.defaultValue().toString();
                mapper.insertProperty(key.key(), defaultVal);
                cache.put(key.key(), defaultVal);
            } else {
                cache.put(key.key(), value);
            }
        }
    }

    // Fetch property value with cache fallback
    private String getValue(PropertyKey key) {
        return cache.get(key.key(), k -> {
            String value = mapper.selectValue(k);
            if (value == null) {
                String defaultVal = key.defaultValue().toString();
                mapper.insertProperty(k, defaultVal);
                return defaultVal;
            }
            return value;
        });
    }

    // Return all properties (cached or DB values)
    public Map<PropertyKey, String> getAllProperties() {
        Map<PropertyKey, String> result = new EnumMap<>(PropertyKey.class);
        for (PropertyKey key : PropertyKey.values()) {
            result.put(key, getValue(key));
        }
        return result;
    }

    // Update a property value and invalidate cache
    public void updateProperty(PropertyKey key, String newValue) {
        mapper.updateProperty(key.key(), newValue);
        cache.invalidate(key.key());
        cache.put(key.key(), newValue);
    }

    // Typed getters
    /*
    String someString = propertyService.getString(PropertyKey.SOME_STRING);
    boolean someBoolean = propertyService.getBoolean(PropertyKey.SOME_BOOLEAN);
    int someInteger = propertyService.getInteger(PropertyKey.SOME_INTEGER);
    BigDecimal someDecimal = propertyService.getDecimal(PropertyKey.SOME_DECIMAL);
    */
    public boolean getBoolean(PropertyKey key) {
        return Boolean.parseBoolean(getValue(key));
    }

    public int getInteger(PropertyKey key) {
        return Integer.parseInt(getValue(key));
    }

    public BigDecimal getDecimal(PropertyKey key) {
        return new BigDecimal(getValue(key));
    }

    public String getString(PropertyKey key) {
        return getValue(key);
    }
}
