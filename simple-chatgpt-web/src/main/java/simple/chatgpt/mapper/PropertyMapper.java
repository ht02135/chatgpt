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
}