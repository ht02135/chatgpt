package simple.chatgpt.controller.management.security;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import simple.chatgpt.pojo.management.security.PageRoleGroupManagementPojo;
import simple.chatgpt.service.management.security.PageRoleGroupManagementService;
import simple.chatgpt.util.PagedResult;
import simple.chatgpt.util.Response;

@RestController
@RequestMapping(value = "/management/pagerolegroups", produces = MediaType.APPLICATION_JSON_VALUE)
public class PageRoleGroupManagementController {

    private static final Logger logger = LogManager.getLogger(PageRoleGroupManagementController.class);

    private final PageRoleGroupManagementService pageRoleGroupService;

    public PageRoleGroupManagementController(PageRoleGroupManagementService pageRoleGroupService) {
        this.pageRoleGroupService = pageRoleGroupService;
        logger.debug("PageRoleGroupManagementController constructor called, pageRoleGroupService={}", pageRoleGroupService);
    }

    // ➕ CREATE PAGE ROLE GROUP
    @PostMapping("/create")
    public ResponseEntity<Response<PageRoleGroupManagementPojo>> create(@RequestBody PageRoleGroupManagementPojo pageRoleGroup) {
        logger.debug("create called");
        logger.debug("create pageRoleGroup={}", pageRoleGroup);

        Map<String, Object> params = new HashMap<>();
        params.put("pageRoleGroup", pageRoleGroup);

        PageRoleGroupManagementPojo created = pageRoleGroupService.create(params);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.success("Page role group created successfully", created, HttpStatus.CREATED.value()));
    }

    // 📖 GET PAGE ROLE GROUP BY ID
    @GetMapping("/get")
    public ResponseEntity<Response<PageRoleGroupManagementPojo>> getById(@RequestParam Long id) {
        logger.debug("getById called, id={}", id);

        Map<String, Object> params = new HashMap<>();
        params.put("id", id);

        PageRoleGroupManagementPojo group = pageRoleGroupService.getById(params);
        if (group == null) {
            return ResponseEntity.ok(Response.error("Page role group not found", null, HttpStatus.NOT_FOUND.value()));
        }

        return ResponseEntity.ok(Response.success("Page role group fetched successfully", group, HttpStatus.OK.value()));
    }

    // 📝 UPDATE PAGE ROLE GROUP
    @PutMapping("/update")
    public ResponseEntity<Response<PageRoleGroupManagementPojo>> update(
            @RequestParam Long id,
            @RequestBody PageRoleGroupManagementPojo pageRoleGroup
    ) {
        logger.debug("update called, id={}", id);
        logger.debug("update pageRoleGroup={}", pageRoleGroup);

        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("pageRoleGroup", pageRoleGroup);

        PageRoleGroupManagementPojo updated = pageRoleGroupService.update(params);
        return ResponseEntity.ok(Response.success("Page role group updated successfully", updated, HttpStatus.OK.value()));
    }

    // 🗑 DELETE PAGE ROLE GROUP
    @DeleteMapping("/delete")
    public ResponseEntity<Response<Void>> delete(@RequestParam Long id) {
        logger.debug("delete called, id={}", id);

        Map<String, Object> params = new HashMap<>();
        params.put("id", id);

        pageRoleGroupService.delete(params);
        return ResponseEntity.ok(Response.success("Page role group deleted successfully", null, HttpStatus.OK.value()));
    }

    // 🔍 SEARCH / PAGINATION
    @GetMapping("/search")
    public ResponseEntity<Response<PagedResult<PageRoleGroupManagementPojo>>> search(@RequestParam Map<String, Object> requestParams) {
        logger.debug("search called, requestParams={}", requestParams);

        Map<String, Object> params = new HashMap<>(requestParams);
        int page = requestParams.get("page") != null ? Integer.parseInt(requestParams.get("page").toString()) : 0;
        int size = requestParams.get("size") != null ? Integer.parseInt(requestParams.get("size").toString()) : 20;
        int offset = page * size;

        params.put("page", page);
        params.put("size", size);
        params.put("offset", offset);
        params.put("limit", size);
        params.put("sortField", requestParams.getOrDefault("sortField", "id"));
        params.put("sortDirection", requestParams.getOrDefault("sortDirection", "ASC"));

        PagedResult<PageRoleGroupManagementPojo> paged = pageRoleGroupService.searchPageRoleGroups(params);
        return ResponseEntity.ok(Response.success("Page role groups fetched successfully", paged, HttpStatus.OK.value()));
    }
}
