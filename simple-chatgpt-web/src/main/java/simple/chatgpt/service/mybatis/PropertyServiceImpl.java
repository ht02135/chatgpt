package simple.chatgpt.service.mybatis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import simple.chatgpt.mapper.PropertyMapper;
import simple.chatgpt.pojo.mybatis.Property;
import simple.chatgpt.util.GenericCache;
import simple.chatgpt.util.PropertyKey;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import java.util.List;

@Service
public class PropertyServiceImpl implements PropertyService {
    @Autowired
    private PropertyMapper propertyMapper;

    private final GenericCache<String, String> cache = GenericCache.getInstance(30, 1000); // 30 min expiry, 1000 max size

    @Override
    public List<Property> getAllProperties() {
        return propertyMapper.selectAllProperties();
    }

    @Override
    public void updateProperty(PropertyKey key, String newValue) {
        propertyMapper.updateProperty(key.name(), newValue);
        cache.put(key.name(), newValue); // update cache
    }

    @Override
    public boolean getBoolean(PropertyKey key) {
        String value = getCachedValue(key);
        return Boolean.parseBoolean(value);
    }

    @Override
    public int getInteger(PropertyKey key) {
        String value = getCachedValue(key);
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public BigDecimal getDecimal(PropertyKey key) {
        String value = getCachedValue(key);
        try {
            return new BigDecimal(value);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    @Override
    public String getString(PropertyKey key) {
        return getCachedValue(key);
    }

    private String getCachedValue(PropertyKey key) {
        return cache.get(key.name(), k -> propertyMapper.selectValue(k));
    }
}