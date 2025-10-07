package simple.chatgpt.controller.management.security;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;

import simple.chatgpt.pojo.management.security.PageRoleGroupManagementPojo;
import simple.chatgpt.util.PagedResult;
import simple.chatgpt.util.Response;

public interface PageRoleGroupManagementControllerApi {

    // ---------------- CREATE ----------------
    ResponseEntity<Response<PageRoleGroupManagementPojo>> insertPageRoleGroup(
            PageRoleGroupManagementPojo pageRoleGroup
    );

    // ---------------- UPDATE ----------------
    ResponseEntity<Response<PageRoleGroupManagementPojo>> updatePageRoleGroup(
            Long id,
            PageRoleGroupManagementPojo pageRoleGroup
    );

    // ---------------- DELETE ----------------
    ResponseEntity<Response<Void>> deletePageRoleGroupById(Long id);

    // ---------------- READ ----------------
    ResponseEntity<Response<PagedResult<PageRoleGroupManagementPojo>>> findAllPageRoleGroups();

    ResponseEntity<Response<PageRoleGroupManagementPojo>> findById(Long id);

    ResponseEntity<Response<PageRoleGroupManagementPojo>> findByUrlPattern(String urlPattern);

    ResponseEntity<Response<PagedResult<PageRoleGroupManagementPojo>>> findByRoleGroupId(Long roleGroupId);

    // ---------------- SEARCH / PAGINATION ----------------
    ResponseEntity<Response<PagedResult<PageRoleGroupManagementPojo>>> findPageRoleGroups(
            Map<String, Object> params
    );

    ResponseEntity<Response<PagedResult<PageRoleGroupManagementPojo>>> searchPageRoleGroups(
            Map<String, Object> params
    );

    // ---------------- COUNT ----------------
    ResponseEntity<Response<Long>> countPageRoleGroups(Map<String, Object> params);
}
