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
import simple.chatgpt.util.SafeConverter;

@RestController
@RequestMapping(value = "/management/pagerolegroups", produces = MediaType.APPLICATION_JSON_VALUE)
public class PageRoleGroupManagementController implements PageRoleGroupManagementControllerApi {

    private static final Logger logger = LogManager.getLogger(PageRoleGroupManagementController.class);

    private final PageRoleGroupManagementService pageRoleGroupService;

    public PageRoleGroupManagementController(PageRoleGroupManagementService pageRoleGroupService) {
        this.pageRoleGroupService = pageRoleGroupService;
        logger.debug("PageRoleGroupManagementController constructor called, pageRoleGroupService={}", pageRoleGroupService);
    }
    
    // ==============================================================
    // ================ 5 CORE METHODS (on top) =====================
    // ==============================================================

    @PostMapping("/create")
    public ResponseEntity<Response<PageRoleGroupManagementPojo>> create(
        @RequestBody(required = false) PageRoleGroupManagementPojo pageRoleGroup) 
    {
        logger.debug("create called");
        logger.debug("create pageRoleGroup={}", pageRoleGroup);

        if (pageRoleGroup == null) {
            logger.debug("create: missing pageRoleGroup payload");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error("Missing pageRoleGroup payload", null, HttpStatus.BAD_REQUEST.value()));
        }

        PageRoleGroupManagementPojo created = pageRoleGroupService.create(pageRoleGroup);
        logger.debug("create return={}", created);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.success("Page role group created successfully", created, HttpStatus.CREATED.value()));
    }

    @PutMapping("/update")
    public ResponseEntity<Response<PageRoleGroupManagementPojo>> update(
        @RequestParam(required = false) Long id,
        @RequestBody(required = false) PageRoleGroupManagementPojo pageRoleGroup) 
    {
        logger.debug("update called");
        logger.debug("update id={}", id);
        logger.debug("update pageRoleGroup={}", pageRoleGroup);

        if (id == null) {
            logger.debug("update: missing pageRoleGroupId");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error("Missing pageRoleGroupId parameter", null, HttpStatus.BAD_REQUEST.value()));
        }
        if (pageRoleGroup == null) {
            logger.debug("update: missing pageRoleGroup payload");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error("Missing pageRoleGroup payload", null, HttpStatus.BAD_REQUEST.value()));
        }

        PageRoleGroupManagementPojo updated = pageRoleGroupService.update(id, pageRoleGroup);
        logger.debug("update return={}", updated);

        return ResponseEntity.ok(Response.success("Page role group updated successfully", updated, HttpStatus.OK.value()));
    }

    @GetMapping("/search")
    public ResponseEntity<Response<PagedResult<PageRoleGroupManagementPojo>>> search(
        @RequestParam Map<String, String> params) 
    {
        logger.debug("search called");
        logger.debug("search params={}", params);

        if (params == null || params.isEmpty()) {
            logger.debug("search: missing parameters");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error("Missing parameters", null, HttpStatus.BAD_REQUEST.value()));
        }

        // Default pagination and sorting
        if (!params.containsKey("page")) params.put("page", "0");
        if (!params.containsKey("size")) params.put("size", "20");

        int page = SafeConverter.toIntOrDefault(params.get("page"), 0);
        int size = SafeConverter.toIntOrDefault(params.get("size"), 20);
        int offset = page * size;

        if (!params.containsKey("offset")) params.put("offset", String.valueOf(offset));
        if (!params.containsKey("limit")) params.put("limit", String.valueOf(size));
        if (!params.containsKey("sortField")) params.put("sortField", "id");
        if (!params.containsKey("sortDirection")) params.put("sortDirection", "ASC");
        params.put("sortDirection", params.get("sortDirection").toUpperCase());

        PagedResult<PageRoleGroupManagementPojo> result = pageRoleGroupService.search(params);
        logger.debug("search return totalCount={}", result.getTotalCount());

        return ResponseEntity.ok(Response.success("Page role groups fetched successfully", result, HttpStatus.OK.value()));
    }

    @GetMapping("/get")
    public ResponseEntity<Response<PageRoleGroupManagementPojo>> get(
        @RequestParam(required = false) Long id) 
    {
        logger.debug("get called");
        logger.debug("get id={}", id);

        if (id == null) {
            logger.debug("get: missing pageRoleGroupId");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error("Missing pageRoleGroupId parameter", null, HttpStatus.BAD_REQUEST.value()));
        }

        PageRoleGroupManagementPojo result = pageRoleGroupService.get(id);
        logger.debug("get return={}", result);

        return ResponseEntity.ok(Response.success("Page role group fetched successfully", result, HttpStatus.OK.value()));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Response<Void>> delete(
        @RequestParam(required = false) Long id) 
    {
        logger.debug("delete called");
        logger.debug("delete id={}", id);

        if (id == null) {
            logger.debug("delete: missing pageRoleGroupId");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error("Missing pageRoleGroupId parameter", null, HttpStatus.BAD_REQUEST.value()));
        }

        pageRoleGroupService.delete(id);
        logger.debug("delete successful for id={}", id);

        return ResponseEntity.ok(Response.success("Page role group deleted successfully", null, HttpStatus.OK.value()));
    }

    // ==============================================================
    // ================ EXISTING METHODS (without URL mapping) ======
    // ==============================================================

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
