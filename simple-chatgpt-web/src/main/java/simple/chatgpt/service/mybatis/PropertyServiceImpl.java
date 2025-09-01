package simple.chatgpt.service.mybatis;

import org.springframework.stereotype.Service;
import simple.chatgpt.mapper.PropertyMapper;
import simple.chatgpt.pojo.mybatis.Property;
import simple.chatgpt.util.GenericCache;
import simple.chatgpt.util.PropertyKey;
import simple.chatgpt.service.mybatis.PropertyService;
import java.math.BigDecimal;
import java.util.List;
import javax.annotation.PostConstruct;

@Service
public class PropertyServiceImpl implements PropertyService {
    private final PropertyMapper mapper;
    private final GenericCache<String, Property> cache;

    public PropertyServiceImpl(PropertyMapper mapper) {
        this.mapper = mapper;
        this.cache = GenericCache.getInstance(30, 1000);
        initDefaults();
    }

    @PostConstruct
    private void initDefaults() {
        for (PropertyKey key : PropertyKey.values()) {
            Property prop = new Property(key.getKey(), key.getTypeName(), String.valueOf(key.getDefaultValue()));
            cache.put(key.getKey(), prop);
        }
    }

    private Property getCachedProperty(PropertyKey key) {
        return cache.get(key.getKey(), k -> {
            Property prop = mapper.selectByKey(k);
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
        mapper.updateProperty(key.name(), newValue);
        Property prop = new Property(key.getKey(), key.getTypeName(), newValue);
        cache.put(key.getKey(), prop); // update cache with Property object
    }

    @Override
    public boolean getBoolean(PropertyKey key) {
        Property prop = getCachedProperty(key);
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