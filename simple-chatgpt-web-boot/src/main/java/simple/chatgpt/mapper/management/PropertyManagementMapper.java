package simple.chatgpt.mapper.management;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import simple.chatgpt.pojo.management.PropertyManagementPojo;

public interface PropertyManagementMapper {

	// ======= 5 CORE METHODS (on top) =======
	void create(@Param("property") PropertyManagementPojo property);
	void update(@Param("id") Long id, @Param("property") PropertyManagementPojo property);
	List<PropertyManagementPojo> search(@Param("params") Map<String, Object> params);
	PropertyManagementPojo get(@Param("id") Long id);
	void delete(@Param("id") Long id);

	// ======= OTHER METHODS =======

	PropertyManagementPojo findByPropertyKey(@Param("propertyKey") String propertyKey);
}
