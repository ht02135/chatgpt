package simple.chatgpt.mapper.mybatis;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import simple.chatgpt.pojo.mybatis.MyBatisProperty;

@Mapper
public interface PropertyMapper {
    String selectValue(@Param("key") String key);
    void updateProperty(@Param("key") String key, @Param("value") String value);
    List<MyBatisProperty> selectAllProperties();
    List<MyBatisProperty> selectPropertiesPaged(@Param("key") String key, @Param("type") String type, @Param("offset") int offset, @Param("size") int size, @Param("sort") String sort, @Param("order") String order);
    int countProperties(@Param("key") String key, @Param("type") String type);
    MyBatisProperty selectByKey(@Param("key") String key);
    void insertPropertyFull(@Param("key") String key, @Param("type") String type, @Param("value") String value);
}