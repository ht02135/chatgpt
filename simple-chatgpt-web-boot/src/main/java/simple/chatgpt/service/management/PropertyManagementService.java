package simple.chatgpt.service.management;

import java.math.BigDecimal;
import java.util.Map;

import simple.chatgpt.pojo.management.PropertyManagementPojo;
import simple.chatgpt.util.PagedResult;
import simple.chatgpt.util.PropertyKey;

public interface PropertyManagementService {

	// ======= 5 CORE METHODS (on top) =======
	PropertyManagementPojo create(PropertyManagementPojo property);
	PropertyManagementPojo update(Long id, PropertyManagementPojo property);
	PagedResult<PropertyManagementPojo> search(Map<String, String> params);
	PropertyManagementPojo get(Long id);
	void delete(Long id);

	// ======= OTHER METHODS =======

    // 🧰 Typed getters
    boolean getBoolean(PropertyKey key);
    int getInteger(PropertyKey key);
    BigDecimal getDecimal(PropertyKey key);
    String getString(PropertyKey key);

}
