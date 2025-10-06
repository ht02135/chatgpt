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

    // ---------------- CREATE ----------------
    @PostMapping("/insertPageRoleGroup")
    public ResponseEntity<Response<PageRoleGroupManagementPojo>> insertPageRoleGroup(@RequestBody PageRoleGroupManagementPojo pageRoleGroup) {
        logger.debug("insertPageRoleGroup called, pageRoleGroup={}", pageRoleGroup);

        Map<String, Object> params = Map.of("pageRoleGroup", pageRoleGroup);
        PageRoleGroupManagementPojo created = pageRoleGroupService.insertPageRoleGroup(params);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.success("Page role group created successfully", created, HttpStatus.CREATED.value()));
    }

    // ---------------- UPDATE ----------------
    @PutMapping("/updatePageRoleGroup")
    public ResponseEntity<Response<PageRoleGroupManagementPojo>> updatePageRoleGroup(@RequestParam Long id,
                                                                                     @RequestBody PageRoleGroupManagementPojo pageRoleGroup) {
        logger.debug("updatePageRoleGroup called, id={} pageRoleGroup={}", id, pageRoleGroup);

        Map<String, Object> params = Map.of("id", id, "pageRoleGroup", pageRoleGroup);
        PageRoleGroupManagementPojo updated = pageRoleGroupService.updatePageRoleGroup(params);

        return ResponseEntity.ok(Response.success("Page role group updated successfully", updated, HttpStatus.OK.value()));
    }

    // ---------------- DELETE ----------------
    @DeleteMapping("/deletePageRoleGroupById")
    public ResponseEntity<Response<Void>> deletePageRoleGroupById(@RequestParam Long id) {
        logger.debug("deletePageRoleGroupById called, id={}", id);

        pageRoleGroupService.deletePageRoleGroupById(Map.of("id", id));
        return ResponseEntity.ok(Response.success("Page role group deleted successfully", null, HttpStatus.OK.value()));
    }

    // ---------------- READ ----------------
    @GetMapping("/findById")
    public ResponseEntity<Response<PageRoleGroupManagementPojo>> findById(@RequestParam Long id) {
        logger.debug("findById called, id={}", id);

        Map<String, Object> params = Map.of("id", id);
        PageRoleGroupManagementPojo group = pageRoleGroupService.findById(params);

        if (group == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.error("Page role group not found", null, HttpStatus.NOT_FOUND.value()));
        }

        return ResponseEntity.ok(Response.success("Page role group fetched successfully", group, HttpStatus.OK.value()));
    }

    @GetMapping("/findByUrlPattern")
    public ResponseEntity<Response<PageRoleGroupManagementPojo>> findByUrlPattern(@RequestParam String urlPattern) {
        logger.debug("findByUrlPattern called, urlPattern={}", urlPattern);

        Map<String, Object> params = Map.of("urlPattern", urlPattern);
        PageRoleGroupManagementPojo group = pageRoleGroupService.findByUrlPattern(params);

        if (group == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.error("Page role group not found", null, HttpStatus.NOT_FOUND.value()));
        }

        return ResponseEntity.ok(Response.success("Page role group fetched successfully", group, HttpStatus.OK.value()));
    }

    @GetMapping("/findByRoleGroupId")
    public ResponseEntity<Response<PagedResult<PageRoleGroupManagementPojo>>> findByRoleGroupId(@RequestParam Long roleGroupId) {
        logger.debug("findByRoleGroupId called, roleGroupId={}", roleGroupId);

        Map<String, Object> params = Map.of("roleGroupId", roleGroupId);
        PagedResult<PageRoleGroupManagementPojo> paged = pageRoleGroupService.findByRoleGroupId(params);

        logger.debug("findByRoleGroupId returned {} items, totalCount={}", paged.getItems().size(), paged.getTotalCount());
        return ResponseEntity.ok(Response.success("Page role groups fetched successfully", paged, HttpStatus.OK.value()));
    }

    // ---------------- SEARCH / PAGINATION ----------------
    @GetMapping("/findPageRoleGroups")
    public ResponseEntity<Response<PagedResult<PageRoleGroupManagementPojo>>> findPageRoleGroups(@RequestParam Map<String, Object> requestParams) {
        logger.debug("findPageRoleGroups called, requestParams={}", requestParams);

        PagedResult<PageRoleGroupManagementPojo> paged = pageRoleGroupService.findPageRoleGroups(requestParams);

        logger.debug("findPageRoleGroups returned {} items, totalCount={}", paged.getItems().size(), paged.getTotalCount());
        return ResponseEntity.ok(Response.success("Page role groups fetched successfully", paged, HttpStatus.OK.value()));
    }

    @GetMapping("/searchPageRoleGroups")
    public ResponseEntity<Response<PagedResult<PageRoleGroupManagementPojo>>> searchPageRoleGroups(@RequestParam Map<String, Object> requestParams) {
        logger.debug("searchPageRoleGroups called, requestParams={}", requestParams);

        PagedResult<PageRoleGroupManagementPojo> paged = pageRoleGroupService.searchPageRoleGroups(requestParams);

        logger.debug("searchPageRoleGroups returned {} items, totalCount={}", paged.getItems().size(), paged.getTotalCount());
        return ResponseEntity.ok(Response.success("Page role groups fetched successfully", paged, HttpStatus.OK.value()));
    }

    @GetMapping("/findAllPageRoleGroups")
    public ResponseEntity<Response<PagedResult<PageRoleGroupManagementPojo>>> findAllPageRoleGroups() {
        logger.debug("findAllPageRoleGroups called");

        PagedResult<PageRoleGroupManagementPojo> paged = pageRoleGroupService.findAllPageRoleGroups();

        logger.debug("findAllPageRoleGroups returned {} items, totalCount={}", paged.getItems().size(), paged.getTotalCount());
        return ResponseEntity.ok(Response.success("All page role groups fetched successfully", paged, HttpStatus.OK.value()));
    }

    // ---------------- COUNT ----------------
    @GetMapping("/countPageRoleGroups")
    public ResponseEntity<Response<Long>> countPageRoleGroups(@RequestParam Map<String, Object> params) {
        logger.debug("countPageRoleGroups called, params={}", params);

        long count = pageRoleGroupService.countPageRoleGroups(params);
        return ResponseEntity.ok(Response.success("Page role groups count fetched successfully", count, HttpStatus.OK.value()));
    }
}
