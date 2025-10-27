package simple.chatgpt.mapper.management;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import simple.chatgpt.pojo.management.PropertyManagementPojo;

@Mapper
public interface PropertyManagementMapper {

    // 🔎 SEARCH / LIST
    List<PropertyManagementPojo> findProperties(Map<String, Object> params);

    long countProperties(Map<String, Object> params);

    // 📖 READ
    PropertyManagementPojo findById(@Param("id") Long id);

    PropertyManagementPojo findByPropertyName(@Param("propertyName") String propertyName);

    PropertyManagementPojo findByPropertyKey(@Param("propertyKey") String propertyKey);

    // ➕ CREATE
    void insertProperty(PropertyManagementPojo property);

    // ✏️ UPDATE
    void updateProperty(PropertyManagementPojo property);

    void updatePropertyByPropertyName(PropertyManagementPojo property);

    void updatePropertyByPropertyKey(PropertyManagementPojo property);

    // 🗑 DELETE
    void deleteById(@Param("id") Long id);

    void deleteByPropertyName(@Param("propertyName") String propertyName);

    void deleteByPropertyKey(@Param("propertyKey") String propertyKey);
}
