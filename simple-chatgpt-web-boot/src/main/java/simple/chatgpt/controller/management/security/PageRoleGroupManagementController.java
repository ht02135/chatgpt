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
public class PageRoleGroupManagementController implements PageRoleGroupManagementControllerApi {

    private static final Logger logger = LogManager.getLogger(PageRoleGroupManagementController.class);

    private final PageRoleGroupManagementService pageRoleGroupService;

    public PageRoleGroupManagementController(PageRoleGroupManagementService pageRoleGroupService) {
        this.pageRoleGroupService = pageRoleGroupService;
        logger.debug("PageRoleGroupManagementController constructor called, pageRoleGroupService={}", pageRoleGroupService);
    }

    // ---------------- CREATE ----------------
    @PostMapping("/insertPageRoleGroup")
    public ResponseEntity<Response<PageRoleGroupManagementPojo>> insertPageRoleGroup(@RequestBody PageRoleGroupManagementPojo pageRoleGroup) {
        logger.debug("insertPageRoleGroup START");
        logger.debug("insertPageRoleGroup pageRoleGroup={}", pageRoleGroup);

        Map<String, Object> params = ParamWrapper.wrap("pageRoleGroup", pageRoleGroup);
        PageRoleGroupManagementPojo created = pageRoleGroupService.insertPageRoleGroup(params);

        logger.debug("insertPageRoleGroup return={}", created);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.success("Page role group created successfully", created, HttpStatus.CREATED.value()));
    }

    // ---------------- UPDATE ----------------
    @PutMapping("/updatePageRoleGroup")
    public ResponseEntity<Response<PageRoleGroupManagementPojo>> updatePageRoleGroup(@RequestParam Long id,
                                                                                     @RequestBody PageRoleGroupManagementPojo pageRoleGroup) {
        logger.debug("updatePageRoleGroup START");
        logger.debug("updatePageRoleGroup id={}", id);
        logger.debug("updatePageRoleGroup pageRoleGroup={}", pageRoleGroup);

        Map<String, Object> params = ParamWrapper.wrap("id", id, "pageRoleGroup", pageRoleGroup);
        PageRoleGroupManagementPojo updated = pageRoleGroupService.updatePageRoleGroup(params);

        logger.debug("updatePageRoleGroup return={}", updated);
        return ResponseEntity.ok(Response.success("Page role group updated successfully", updated, HttpStatus.OK.value()));
    }

    // ---------------- DELETE ----------------
    @DeleteMapping("/deletePageRoleGroupById")
    public ResponseEntity<Response<Void>> deletePageRoleGroupById(@RequestParam Long id) {
        logger.debug("deletePageRoleGroupById START");
        logger.debug("deletePageRoleGroupById id={}", id);

        pageRoleGroupService.deletePageRoleGroupById(ParamWrapper.wrap("id", id));

        logger.debug("deletePageRoleGroupById DONE");
        return ResponseEntity.ok(Response.success("Page role group deleted successfully", null, HttpStatus.OK.value()));
    }

    // ---------------- READ ----------------
    @GetMapping("/findAllPageRoleGroups")
    public ResponseEntity<Response<PagedResult<PageRoleGroupManagementPojo>>> findAllPageRoleGroups() {
        logger.debug("findAllPageRoleGroups START");

        PagedResult<PageRoleGroupManagementPojo> paged = pageRoleGroupService.findAllPageRoleGroups();

        logger.debug("findAllPageRoleGroups return={}", paged);
        return ResponseEntity.ok(Response.success("All page role groups fetched successfully", paged, HttpStatus.OK.value()));
    }

    @GetMapping("/findById")
    public ResponseEntity<Response<PageRoleGroupManagementPojo>> findById(@RequestParam Long id) {
        logger.debug("findById START");
        logger.debug("findById id={}", id);

        Map<String, Object> params = ParamWrapper.wrap("id", id);
        PageRoleGroupManagementPojo group = pageRoleGroupService.findById(params);

        logger.debug("findById return={}", group);
        if (group == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.error("Page role group not found", null, HttpStatus.NOT_FOUND.value()));
        }

        return ResponseEntity.ok(Response.success("Page role group fetched successfully", group, HttpStatus.OK.value()));
    }

    @GetMapping("/findByUrlPattern")
    public ResponseEntity<Response<PageRoleGroupManagementPojo>> findByUrlPattern(@RequestParam String urlPattern) {
        logger.debug("findByUrlPattern START");
        logger.debug("findByUrlPattern urlPattern={}", urlPattern);

        Map<String, Object> params = ParamWrapper.wrap("urlPattern", urlPattern);
        PageRoleGroupManagementPojo group = pageRoleGroupService.findByUrlPattern(params);

        logger.debug("findByUrlPattern return={}", group);
        if (group == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.error("Page role group not found", null, HttpStatus.NOT_FOUND.value()));
        }

        return ResponseEntity.ok(Response.success("Page role group fetched successfully", group, HttpStatus.OK.value()));
    }

    @GetMapping("/findByRoleGroupId")
    public ResponseEntity<Response<PagedResult<PageRoleGroupManagementPojo>>> findByRoleGroupId(@RequestParam Long roleGroupId) {
        logger.debug("findByRoleGroupId START");
        logger.debug("findByRoleGroupId roleGroupId={}", roleGroupId);

        Map<String, Object> params = ParamWrapper.wrap("roleGroupId", roleGroupId);
        PagedResult<PageRoleGroupManagementPojo> paged = pageRoleGroupService.findByRoleGroupId(params);

        logger.debug("findByRoleGroupId return={}", paged);
        return ResponseEntity.ok(Response.success("Page role groups fetched successfully", paged, HttpStatus.OK.value()));
    }

    // ---------------- SEARCH / PAGINATION ----------------
    @GetMapping("/findPageRoleGroups")
    public ResponseEntity<Response<PagedResult<PageRoleGroupManagementPojo>>> findPageRoleGroups(@RequestParam Map<String, Object> params) {
        logger.debug("findPageRoleGroups START");
        logger.debug("findPageRoleGroups params={}", params);

        PagedResult<PageRoleGroupManagementPojo> paged = pageRoleGroupService.findPageRoleGroups(params);

        logger.debug("findPageRoleGroups return={}", paged);
        return ResponseEntity.ok(Response.success("Page role groups fetched successfully", paged, HttpStatus.OK.value()));
    }

    @GetMapping("/searchPageRoleGroups")
    public ResponseEntity<Response<PagedResult<PageRoleGroupManagementPojo>>> searchPageRoleGroups(@RequestParam Map<String, Object> params) {
        logger.debug("searchPageRoleGroups START");
        logger.debug("searchPageRoleGroups params={}", params);

        PagedResult<PageRoleGroupManagementPojo> paged = pageRoleGroupService.searchPageRoleGroups(params);

        logger.debug("searchPageRoleGroups return={}", paged);
        return ResponseEntity.ok(Response.success("Page role groups fetched successfully", paged, HttpStatus.OK.value()));
    }

    // ---------------- COUNT ----------------
    @GetMapping("/countPageRoleGroups")
    public ResponseEntity<Response<Long>> countPageRoleGroups(@RequestParam Map<String, Object> params) {
        logger.debug("countPageRoleGroups START");
        logger.debug("countPageRoleGroups params={}", params);

        long count = pageRoleGroupService.countPageRoleGroups(params);

        logger.debug("countPageRoleGroups return={}", count);
        return ResponseEntity.ok(Response.success("Page role groups count fetched successfully", count, HttpStatus.OK.value()));
    }
}
