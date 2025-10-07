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
import simple.chatgpt.util.ParamWrapper;
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

        Map<String, Object> params = ParamWrapper.wrap("pageRoleGroup", pageRoleGroup);
        PageRoleGroupManagementPojo created = pageRoleGroupService.insertPageRoleGroup(params);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.success("Page role group created successfully", created, HttpStatus.CREATED.value()));
    }

    // ---------------- UPDATE ----------------
    @PutMapping("/updatePageRoleGroup")
    public ResponseEntity<Response<PageRoleGroupManagementPojo>> updatePageRoleGroup(@RequestParam Long id,
                                                                                     @RequestBody PageRoleGroupManagementPojo pageRoleGroup) {
        logger.debug("updatePageRoleGroup called, id={} pageRoleGroup={}", id, pageRoleGroup);

        Map<String, Object> params = ParamWrapper.wrap("id", id, "pageRoleGroup", pageRoleGroup);
        PageRoleGroupManagementPojo updated = pageRoleGroupService.updatePageRoleGroup(params);

        return ResponseEntity.ok(Response.success("Page role group updated successfully", updated, HttpStatus.OK.value()));
    }

    // ---------------- DELETE ----------------
    @DeleteMapping("/deletePageRoleGroupById")
    public ResponseEntity<Response<Void>> deletePageRoleGroupById(@RequestParam Long id) {
        logger.debug("deletePageRoleGroupById called, id={}", id);

        pageRoleGroupService.deletePageRoleGroupById(ParamWrapper.wrap("id", id));
        return ResponseEntity.ok(Response.success("Page role group deleted successfully", null, HttpStatus.OK.value()));
    }

    // ---------------- READ ----------------
    @GetMapping("/findAllPageRoleGroups")
    public ResponseEntity<Response<PagedResult<PageRoleGroupManagementPojo>>> findAllPageRoleGroups() {
        logger.debug("findAllPageRoleGroups called");

        PagedResult<PageRoleGroupManagementPojo> paged = pageRoleGroupService.findAllPageRoleGroups();
        logger.debug("findAllPageRoleGroups returned {} items, totalCount={}", paged.getItems().size(), paged.getTotalCount());

        return ResponseEntity.ok(Response.success("All page role groups fetched successfully", paged, HttpStatus.OK.value()));
    }

    @GetMapping("/findById")
    public ResponseEntity<Response<PageRoleGroupManagementPojo>> findById(@RequestParam Long id) {
        logger.debug("findById called, id={}", id);

        Map<String, Object> params = ParamWrapper.wrap("id", id);
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

        Map<String, Object> params = ParamWrapper.wrap("urlPattern", urlPattern);
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

        Map<String, Object> params = ParamWrapper.wrap("roleGroupId", roleGroupId);
        PagedResult<PageRoleGroupManagementPojo> paged = pageRoleGroupService.findByRoleGroupId(params);

        logger.debug("findByRoleGroupId returned {} items, totalCount={}", paged.getItems().size(), paged.getTotalCount());
        return ResponseEntity.ok(Response.success("Page role groups fetched successfully", paged, HttpStatus.OK.value()));
    }

    // ---------------- SEARCH / PAGINATION ----------------
    @GetMapping("/findPageRoleGroups")
    public ResponseEntity<Response<PagedResult<PageRoleGroupManagementPojo>>> findPageRoleGroups(@RequestParam Map<String, Object> params) {
        logger.debug("findPageRoleGroups called, params={}", params);

        PagedResult<PageRoleGroupManagementPojo> paged = pageRoleGroupService.findPageRoleGroups(params);
        logger.debug("findPageRoleGroups returned {} items, totalCount={}", paged.getItems().size(), paged.getTotalCount());

        return ResponseEntity.ok(Response.success("Page role groups fetched successfully", paged, HttpStatus.OK.value()));
    }

    @GetMapping("/searchPageRoleGroups")
    public ResponseEntity<Response<PagedResult<PageRoleGroupManagementPojo>>> searchPageRoleGroups(@RequestParam Map<String, Object> params) {
        logger.debug("searchPageRoleGroups called, params={}", params);

        PagedResult<PageRoleGroupManagementPojo> paged = pageRoleGroupService.searchPageRoleGroups(params);
        logger.debug("searchPageRoleGroups returned {} items, totalCount={}", paged.getItems().size(), paged.getTotalCount());

        return ResponseEntity.ok(Response.success("Page role groups fetched successfully", paged, HttpStatus.OK.value()));
    }

    // ---------------- COUNT ----------------
    @GetMapping("/countPageRoleGroups")
    public ResponseEntity<Response<Long>> countPageRoleGroups(@RequestParam Map<String, Object> params) {
        logger.debug("countPageRoleGroups called, params={}", params);

        long count = pageRoleGroupService.countPageRoleGroups(params);
        logger.debug("countPageRoleGroups result={}", count);

        return ResponseEntity.ok(Response.success("Page role groups count fetched successfully", count, HttpStatus.OK.value()));
    }
}

/*
HUNG : DONT REMOVE
Heres the updated mapping between JS calls and the controller URLs for your pageRoleGroup.js setup:

JS Call	Controller URL associated with a valid method	Controller Method Exists	Suggestion / Fix
Save mapping (create)	/management/pagerolegroups/insertPageRoleGroup	Yes	✅ JS already updated to call this
Save mapping (update)	/management/pagerolegroups/updatePageRoleGroup?id=...	Yes	✅ JS already updated to call this
Delete mapping (by ID)	/management/pagerolegroups/deletePageRoleGroupById?id=...	Yes	✅ JS already updated to call this
Load mapping by ID	/management/pagerolegroups/findById?id=...	Yes	✅ JS already updated to call this
Load mappings (search / list)	/management/pagerolegroups/searchPageRoleGroups?params...	Yes	✅ JS already calls /searchPageRoleGroups
Optional: list all mappings	/management/pagerolegroups/findAllPageRoleGroups	Yes	JS currently does not call; could add a loadAll method if needed
Optional: list by role group	/management/pagerolegroups/findByRoleGroupId?roleGroupId=...	Yes	JS currently does not call; could add helper if filtering by role group
Optional: find mapping by URL pattern	/management/pagerolegroups/findByUrlPattern?urlPattern=...	Yes	JS currently does not call; could add helper if needed
Count mappings	/management/pagerolegroups/countPageRoleGroups?params...	Yes	JS currently does not call; could add if needed
*/