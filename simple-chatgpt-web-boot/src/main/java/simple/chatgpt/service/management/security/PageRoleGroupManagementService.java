package simple.chatgpt.service.management.security;

import java.util.Map;

import simple.chatgpt.pojo.management.security.PageRoleGroupManagementPojo;
import simple.chatgpt.util.PagedResult;

public interface PageRoleGroupManagementService {

    // ---------------- CREATE ----------------
    PageRoleGroupManagementPojo insertPageRoleGroup(Map<String, Object> params);

    // ---------------- UPDATE ----------------
    PageRoleGroupManagementPojo updatePageRoleGroup(Map<String, Object> params);

    // ---------------- DELETE ----------------
    void deletePageRoleGroupById(Map<String, Object> params);

    // ---------------- READ ----------------
    PagedResult<PageRoleGroupManagementPojo> findAllPageRoleGroups();

    PageRoleGroupManagementPojo findById(Map<String, Object> params);

    PageRoleGroupManagementPojo findByUrlPattern(Map<String, Object> params);

    PagedResult<PageRoleGroupManagementPojo> findByRoleGroupId(Map<String, Object> params);

    // ---------------- SEARCH / PAGINATION ----------------
    PagedResult<PageRoleGroupManagementPojo> findPageRoleGroups(Map<String, Object> params);

    PagedResult<PageRoleGroupManagementPojo> searchPageRoleGroups(Map<String, Object> params);

    // ---------------- COUNT ----------------
    long countPageRoleGroups(Map<String, Object> params);
}
