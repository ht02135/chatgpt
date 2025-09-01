package simple.chatgpt.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PropertyMapper {
    String selectValue(@Param("key") String key);
    void insertProperty(@Param("key") String key, @Param("value") String value);
    void updateProperty(@Param("key") String key, @Param("value") String value);
}
