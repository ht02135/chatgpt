package simple.chatgpt.service.management.security;

import java.util.Map;

import simple.chatgpt.pojo.management.security.PageRoleGroupManagementPojo;
import simple.chatgpt.util.PagedResult;

public interface PageRoleGroupManagementService {

    // 🔎 SEARCH / PAGINATION
    PagedResult<PageRoleGroupManagementPojo> searchPageRoleGroups(Map<String, Object> params);

    // 🔍 LIST ALL
    PagedResult<PageRoleGroupManagementPojo> findAll(Map<String, Object> params);

    // 📖 READ
    PageRoleGroupManagementPojo getById(Map<String, Object> params);

    PageRoleGroupManagementPojo getByUrlPattern(Map<String, Object> params);

    PagedResult<PageRoleGroupManagementPojo> getByRoleGroupId(Map<String, Object> params);

    // ➕ CREATE
    PageRoleGroupManagementPojo create(Map<String, Object> params);

    // ✏️ UPDATE
    PageRoleGroupManagementPojo update(Map<String, Object> params);

    // 🗑 DELETE
    void delete(Map<String, Object> params);
}
