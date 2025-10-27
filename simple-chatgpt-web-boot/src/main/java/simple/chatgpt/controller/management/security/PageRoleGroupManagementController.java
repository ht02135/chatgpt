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
import simple.chatgpt.util.SafeConverter;

@RestController
@RequestMapping(value = "/management/pagerolegroups", produces = MediaType.APPLICATION_JSON_VALUE)
public class PageRoleGroupManagementController {

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

}
