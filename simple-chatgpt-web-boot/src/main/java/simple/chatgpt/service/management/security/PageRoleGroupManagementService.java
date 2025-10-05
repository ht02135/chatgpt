package simple.chatgpt.service.management.security;

import java.util.List;
import java.util.Map;

import simple.chatgpt.pojo.management.security.PageRoleGroupManagementPojo;
import simple.chatgpt.util.PagedResult;

public interface PageRoleGroupManagementService {

    // 🔎 LIST / SEARCH
    PagedResult<PageRoleGroupManagementPojo> searchPageRoleGroups(Map<String, String> params);

    List<PageRoleGroupManagementPojo> findAll();

    // 📖 READ
    PageRoleGroupManagementPojo getById(Long id);

    PageRoleGroupManagementPojo getByUrlPattern(String urlPattern);

    List<PageRoleGroupManagementPojo> getByRoleGroupId(Long roleGroupId);

    // ➕ CREATE
    PageRoleGroupManagementPojo create(PageRoleGroupManagementPojo pageRoleGroup);

    // ✏️ UPDATE
    PageRoleGroupManagementPojo updateById(Long id, PageRoleGroupManagementPojo pageRoleGroup);

    // 🗑 DELETE
    void deleteById(Long id);
}
