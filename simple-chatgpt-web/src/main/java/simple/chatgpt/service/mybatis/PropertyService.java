package simple.chatgpt.service.mybatis;

import simple.chatgpt.pojo.mybatis.Property;
import simple.chatgpt.util.PropertyKey;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public interface PropertyService {
    // Return all properties (cached or DB values)
	List<Property> getAllProperties();

    // Update a property value and invalidate cache
    void updateProperty(PropertyKey key, String newValue);

    // Typed getters
    /*
    String someString = propertyService.getString(PropertyKey.SOME_STRING);
    boolean someBoolean = propertyService.getBoolean(PropertyKey.SOME_BOOLEAN);
    int someInteger = propertyService.getInteger(PropertyKey.SOME_INTEGER);
    BigDecimal someDecimal = propertyService.getDecimal(PropertyKey.SOME_DECIMAL);
    */
    boolean getBoolean(PropertyKey key);

    int getInteger(PropertyKey key);

    BigDecimal getDecimal(PropertyKey key);

    String getString(PropertyKey key);
}