package simple.chatgpt.service.management.security;

import java.util.List;
import java.util.Map;

import simple.chatgpt.pojo.management.security.PageRoleGroupManagementPojo;
import simple.chatgpt.util.PagedResult;

public interface PageRoleGroupManagementService {

    // 🔎 LIST / SEARCH
    PagedResult<PageRoleGroupManagementPojo> searchPageRoleGroups(Map<String, Object> params);

    List<PageRoleGroupManagementPojo> findAll(Map<String, Object> params);

    // 📖 READ
    PageRoleGroupManagementPojo getById(Map<String, Object> params);

    PageRoleGroupManagementPojo getByUrlPattern(Map<String, Object> params);

    List<PageRoleGroupManagementPojo> getByRoleGroupId(Map<String, Object> params);

    // ➕ CREATE
    PageRoleGroupManagementPojo create(Map<String, Object> params);

    // ✏️ UPDATE
    PageRoleGroupManagementPojo update(Map<String, Object> params);

    // 🗑 DELETE
    void delete(Map<String, Object> params);
}
