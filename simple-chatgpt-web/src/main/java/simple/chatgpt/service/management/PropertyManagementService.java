package simple.chatgpt.service.management;

import java.util.Map;

import simple.chatgpt.pojo.management.PropertyManagementPojo;
import simple.chatgpt.util.PagedResult;

public interface PropertyManagementService {

    // 🔎 LIST / SEARCH
    PagedResult<PropertyManagementPojo> searchProperties(Map<String, String> params);

    // 📖 READ
    PropertyManagementPojo getPropertyById(Long id);

    PropertyManagementPojo getByPropertyName(String propertyName);

    PropertyManagementPojo getByPropertyKey(String propertyKey);

    // ➕ CREATE
    PropertyManagementPojo createProperty(PropertyManagementPojo property);

    // ✏️ UPDATE
    PropertyManagementPojo updatePropertyById(Long id, PropertyManagementPojo property);

    PropertyManagementPojo updatePropertyByPropertyName(String propertyName, PropertyManagementPojo property);

    PropertyManagementPojo updatePropertyByPropertyKey(String propertyKey, PropertyManagementPojo property);

    // 🗑 DELETE
    void deletePropertyById(Long id);

    void deletePropertyByPropertyName(String propertyName);

    void deletePropertyByPropertyKey(String propertyKey);
}
