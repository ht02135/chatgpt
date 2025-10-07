package simple.chatgpt.mapper.management;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import simple.chatgpt.pojo.management.PropertyManagementPojo;

public interface PropertyManagementMapper {

    // 🔎 SEARCH / LIST
    List<PropertyManagementPojo> findProperties(@Param("params") Map<String, Object> params);

    long countProperties(@Param("params") Map<String, Object> params);

    // 📖 READ
    PropertyManagementPojo findById(@Param("id") Long id);

    PropertyManagementPojo findByPropertyName(@Param("propertyName") String propertyName);

    PropertyManagementPojo findByPropertyKey(@Param("propertyKey") String propertyKey);

    // ➕ CREATE
    void insertProperty(@Param("property") PropertyManagementPojo property);

    // ✏️ UPDATE
    void updateProperty(@Param("property") PropertyManagementPojo property);

    void updatePropertyByPropertyName(@Param("property") PropertyManagementPojo property);

    void updatePropertyByPropertyKey(@Param("property") PropertyManagementPojo property);

    // 🗑 DELETE
    void deleteById(@Param("id") Long id);

    void deleteByPropertyName(@Param("propertyName") String propertyName);

    void deleteByPropertyKey(@Param("propertyKey") String propertyKey);
}
