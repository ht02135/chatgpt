package simple.chatgpt.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import simple.chatgpt.pojo.mybatis.Property;

import java.util.List;

@Mapper
public interface PropertyMapper {
    String selectValue(@Param("key") String key);
    void insertProperty(@Param("key") String key, @Param("value") String value);
    void updateProperty(@Param("key") String key, @Param("value") String value);
    List<Property> selectAllProperties();
    List<Property> selectPropertiesPaged(@Param("key") String key, @Param("type") String type, @Param("offset") int offset, @Param("size") int size, @Param("sort") String sort, @Param("order") String order);
    int countProperties(@Param("key") String key, @Param("type") String type);
    Property selectByKey(@Param("key") String key);
}