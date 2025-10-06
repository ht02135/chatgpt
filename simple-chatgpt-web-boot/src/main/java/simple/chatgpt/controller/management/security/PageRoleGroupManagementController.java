package simple.chatgpt.controller.management.security;

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
        logger.debug("create called, pageRoleGroup={}", pageRoleGroup);

        Map<String, Object> params = Map.of("pageRoleGroup", pageRoleGroup);
        PageRoleGroupManagementPojo created = pageRoleGroupService.create(params);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.success("Page role group created successfully", created, HttpStatus.CREATED.value()));
    }

    // 📖 GET PAGE ROLE GROUP BY ID
    @GetMapping("/getById")
    public ResponseEntity<Response<PageRoleGroupManagementPojo>> getById(@RequestParam Long id) {
        logger.debug("getById called, id={}", id);

        Map<String, Object> params = Map.of("id", id);
        PageRoleGroupManagementPojo group = pageRoleGroupService.getById(params);

        if (group == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.error("Page role group not found", null, HttpStatus.NOT_FOUND.value()));
        }

        return ResponseEntity.ok(Response.success("Page role group fetched successfully", group, HttpStatus.OK.value()));
    }

    // 📖 GET PAGE ROLE GROUP BY URL PATTERN
    @GetMapping("/getByUrlPattern")
    public ResponseEntity<Response<PageRoleGroupManagementPojo>> getByUrlPattern(@RequestParam String urlPattern) {
        logger.debug("getByUrlPattern called, urlPattern={}", urlPattern);

        Map<String, Object> params = Map.of("urlPattern", urlPattern);
        PageRoleGroupManagementPojo group = pageRoleGroupService.getByUrlPattern(params);

        if (group == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.error("Page role group not found", null, HttpStatus.NOT_FOUND.value()));
        }

        return ResponseEntity.ok(Response.success("Page role group fetched successfully", group, HttpStatus.OK.value()));
    }

    // 📖 GET PAGE ROLE GROUPS BY ROLE GROUP ID
    @GetMapping("/getByRoleGroupId")
    public ResponseEntity<Response<PagedResult<PageRoleGroupManagementPojo>>> getByRoleGroupId(@RequestParam Long roleGroupId) {
        logger.debug("getByRoleGroupId called, roleGroupId={}", roleGroupId);

        Map<String, Object> params = Map.of("roleGroupId", roleGroupId);
        PagedResult<PageRoleGroupManagementPojo> paged = pageRoleGroupService.getByRoleGroupId(params);

        logger.debug("getByRoleGroupId returned {} items, totalCount={}", paged.getItems().size(), paged.getTotalCount());
        return ResponseEntity.ok(Response.success("Page role groups fetched successfully", paged, HttpStatus.OK.value()));
    }

    // 🔍 SEARCH / PAGINATION
    @GetMapping("/searchPageRoleGroups")
    public ResponseEntity<Response<PagedResult<PageRoleGroupManagementPojo>>> searchPageRoleGroups(@RequestParam Map<String, Object> requestParams) {
        logger.debug("searchPageRoleGroups called, requestParams={}", requestParams);

        PagedResult<PageRoleGroupManagementPojo> paged = pageRoleGroupService.searchPageRoleGroups(requestParams);

        logger.debug("searchPageRoleGroups returned {} items, totalCount={}", paged.getItems().size(), paged.getTotalCount());
        return ResponseEntity.ok(Response.success("Page role groups fetched successfully", paged, HttpStatus.OK.value()));
    }

    // 🔍 LIST ALL PAGE ROLE GROUPS
    @GetMapping("/findAll")
    public ResponseEntity<Response<PagedResult<PageRoleGroupManagementPojo>>> findAll(@RequestParam Map<String, Object> requestParams) {
        logger.debug("findAll called, requestParams={}", requestParams);

        PagedResult<PageRoleGroupManagementPojo> paged = pageRoleGroupService.findAll(requestParams);

        logger.debug("findAll returned {} items, totalCount={}", paged.getItems().size(), paged.getTotalCount());
        return ResponseEntity.ok(Response.success("All page role groups fetched successfully", paged, HttpStatus.OK.value()));
    }

    // 📝 UPDATE PAGE ROLE GROUP
    @PutMapping("/update")
    public ResponseEntity<Response<PageRoleGroupManagementPojo>> update(@RequestParam Long id,
                                                                         @RequestBody PageRoleGroupManagementPojo pageRoleGroup) {
        logger.debug("update called, id={} pageRoleGroup={}", id, pageRoleGroup);

        Map<String, Object> params = Map.of("id", id, "pageRoleGroup", pageRoleGroup);
        PageRoleGroupManagementPojo updated = pageRoleGroupService.update(params);

        return ResponseEntity.ok(Response.success("Page role group updated successfully", updated, HttpStatus.OK.value()));
    }

    // 🗑 DELETE PAGE ROLE GROUP
    @DeleteMapping("/delete")
    public ResponseEntity<Response<Void>> delete(@RequestParam Long id) {
        logger.debug("delete called, id={}", id);

        pageRoleGroupService.delete(Map.of("id", id));
        return ResponseEntity.ok(Response.success("Page role group deleted successfully", null, HttpStatus.OK.value()));
    }
}
