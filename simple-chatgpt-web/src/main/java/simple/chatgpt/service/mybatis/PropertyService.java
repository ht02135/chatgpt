package simple.chatgpt.service.mybatis;

import java.math.BigDecimal;
import java.util.List;

import simple.chatgpt.pojo.mybatis.MyBatisProperty;
import simple.chatgpt.util.PropertyKey;

public interface PropertyService {
    // Return all properties (cached or DB values)
	List<MyBatisProperty> getAllProperties();

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

    List<MyBatisProperty> getProperties(String key, String type, int page, int size, String sort, String order);
    int countProperties(String key, String type);
}