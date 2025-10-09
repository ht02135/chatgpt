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

import simple.chatgpt.pojo.management.security.RoleGroupManagementPojo;
import simple.chatgpt.service.management.security.RoleGroupManagementService;
import simple.chatgpt.util.PagedResult;
import simple.chatgpt.util.Response;
import simple.chatgpt.util.SafeConverter;

@RestController
@RequestMapping(value = "/management/rolegroups", produces = MediaType.APPLICATION_JSON_VALUE)
public class RoleGroupManagementController implements RoleGroupManagementControllerApi {

	private static final Logger logger = LogManager.getLogger(RoleGroupManagementController.class);

	private final RoleGroupManagementService roleGroupService;

	public RoleGroupManagementController(RoleGroupManagementService roleGroupService) {
		this.roleGroupService = roleGroupService;
		logger.debug("RoleGroupManagementController constructor called, roleGroupService={}", roleGroupService);
	}

    // ==============================================================
    // ================ 5 CORE METHODS (on top) =====================
    // ==============================================================

    @PostMapping("/create")
    public ResponseEntity<Response<RoleGroupManagementPojo>> create(
        @RequestBody(required = false) RoleGroupManagementPojo group) 
    {
        logger.debug("create called");
        logger.debug("create group={}", group);

        if (group == null) {
            logger.debug("create: missing group payload");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error("Missing group payload", null, HttpStatus.BAD_REQUEST.value()));
        }

        RoleGroupManagementPojo created = roleGroupService.create(group);

        logger.debug("create return={}", created);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.success("Role group created successfully", created, HttpStatus.CREATED.value()));
    }

    @PutMapping("/update")
    public ResponseEntity<Response<RoleGroupManagementPojo>> update(
        @RequestParam(required = false) Long id,
        @RequestBody(required = false) RoleGroupManagementPojo group) 
    {
        logger.debug("update called");
        logger.debug("update id={}", id);
        logger.debug("update group={}", group);

        if (id == null) {
            logger.debug("update: missing id");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error("Missing id parameter", null, HttpStatus.BAD_REQUEST.value()));
        }
        if (group == null) {
            logger.debug("update: missing group payload");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error("Missing group payload", null, HttpStatus.BAD_REQUEST.value()));
        }

        RoleGroupManagementPojo updated = roleGroupService.update(id, group);

        logger.debug("update return={}", updated);
        return ResponseEntity.ok(Response.success("Role group updated successfully", updated, HttpStatus.OK.value()));
    }

    @GetMapping("/search")
    public ResponseEntity<Response<PagedResult<RoleGroupManagementPojo>>> search(
        @RequestParam Map<String, String> params) 
    {
        logger.debug("search called");
        logger.debug("search params={}", params);

        if (params == null || params.isEmpty()) {
            logger.debug("search: missing parameters");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error("Missing parameters", null, HttpStatus.BAD_REQUEST.value()));
        }

        // Default pagination
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

        PagedResult<RoleGroupManagementPojo> result = roleGroupService.search(params);

        logger.debug("search return totalCount={}", result.getTotalCount());
        return ResponseEntity.ok(Response.success("Role groups fetched successfully", result, HttpStatus.OK.value()));
    }

    @GetMapping("/get")
    public ResponseEntity<Response<RoleGroupManagementPojo>> get(
        @RequestParam(required = false) Long id) 
    {
        logger.debug("get called");
        logger.debug("get id={}", id);

        if (id == null) {
            logger.debug("get: missing id");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error("Missing id parameter", null, HttpStatus.BAD_REQUEST.value()));
        }

        RoleGroupManagementPojo result = roleGroupService.get(id);

        logger.debug("get return={}", result);
        return ResponseEntity.ok(Response.success("Role group fetched successfully", result, HttpStatus.OK.value()));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Response<Void>> delete(
        @RequestParam(required = false) Long id) 
    {
        logger.debug("delete called");
        logger.debug("delete id={}", id);

        if (id == null) {
            logger.debug("delete: missing id");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error("Missing id parameter", null, HttpStatus.BAD_REQUEST.value()));
        }

        roleGroupService.delete(id);

        logger.debug("delete successful for id={}", id);
        return ResponseEntity.ok(Response.success("Role group deleted successfully", null, HttpStatus.OK.value()));
    }

	// ==============================================================
	// ================ EXISTING METHODS (without URL mapping) ======
	// ==============================================================

	// ---------------- CREATE ----------------
	@PostMapping("/insertRoleGroup")
	public ResponseEntity<Response<RoleGroupManagementPojo>> insertRoleGroup(
			@RequestBody RoleGroupManagementPojo group) {
		logger.debug("insertRoleGroup START");
		logger.debug("insertRoleGroup group={}", group);

		Map<String, Object> params = new HashMap<>();
		params.put("group", group);

		RoleGroupManagementPojo inserted = roleGroupService.insertRoleGroup(params);

		logger.debug("insertRoleGroup return={}", inserted);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(Response.success("Role group inserted successfully", inserted, HttpStatus.CREATED.value()));
	}

	// ---------------- UPDATE ----------------
	@PutMapping("/updateRoleGroup")
	public ResponseEntity<Response<RoleGroupManagementPojo>> updateRoleGroup(@RequestBody Map<String, Object> params) {
		logger.debug("updateRoleGroup START");
		logger.debug("updateRoleGroup params={}", params);

		RoleGroupManagementPojo updated = roleGroupService.updateRoleGroup(params);

		logger.debug("updateRoleGroup return={}", updated);
		return ResponseEntity.ok(Response.success("Role group updated successfully", updated, HttpStatus.OK.value()));
	}

	// ---------------- DELETE ----------------
	@DeleteMapping("/deleteRoleGroupById")
	public ResponseEntity<Response<Void>> deleteRoleGroupById(@RequestParam Long roleGroupId) {
		logger.debug("deleteRoleGroupById START");
		logger.debug("deleteRoleGroupById roleGroupId={}", roleGroupId);

		Map<String, Object> params = new HashMap<>();
		params.put("roleGroupId", roleGroupId);

		roleGroupService.deleteRoleGroupById(params);

		logger.debug("deleteRoleGroupById DONE");
		return ResponseEntity
				.ok(Response.success("Role group deleted by ID successfully", null, HttpStatus.OK.value()));
	}

	// ---------------- READ ----------------
	@GetMapping("/findRoleGroupById")
	public ResponseEntity<Response<RoleGroupManagementPojo>> findRoleGroupById(@RequestParam Long roleGroupId) {
		logger.debug("findRoleGroupById START");
		logger.debug("findRoleGroupById roleGroupId={}", roleGroupId);

		Map<String, Object> params = new HashMap<>();
		params.put("roleGroupId", roleGroupId);

		RoleGroupManagementPojo group = roleGroupService.findRoleGroupById(params);

		logger.debug("findRoleGroupById return={}", group);
		return ResponseEntity.ok(Response.success("Role group fetched successfully", group, HttpStatus.OK.value()));
	}

	// ---------------- LIST ALL ----------------
	@GetMapping("/findAllRoleGroups")
	public ResponseEntity<Response<PagedResult<RoleGroupManagementPojo>>> findAllRoleGroups() {
		logger.debug("findAllRoleGroups START");

		PagedResult<RoleGroupManagementPojo> result = roleGroupService.findAllRoleGroups();

		logger.debug("findAllRoleGroups return={}", result);
		return ResponseEntity
				.ok(Response.success("All role groups fetched successfully", result, HttpStatus.OK.value()));
	}

	@GetMapping("/getAllRoleGroups")
	public ResponseEntity<Response<PagedResult<RoleGroupManagementPojo>>> getAllRoleGroups() {
		logger.debug("getAllRoleGroups START");

		PagedResult<RoleGroupManagementPojo> result = roleGroupService.getAllRoleGroups();

		logger.debug("getAllRoleGroups return={}", result);
		return ResponseEntity
				.ok(Response.success("All role groups fetched successfully", result, HttpStatus.OK.value()));
	}

	// ---------------- SEARCH / PAGINATION ----------------
	@GetMapping("/findRoleGroups")
	public ResponseEntity<Response<PagedResult<RoleGroupManagementPojo>>> findRoleGroups(
			@RequestParam Map<String, Object> params) {
		logger.debug("findRoleGroups START");
		logger.debug("findRoleGroups params={}", params);

		PagedResult<RoleGroupManagementPojo> result = roleGroupService.findRoleGroups(params);

		logger.debug("findRoleGroups return={}", result);
		return ResponseEntity
				.ok(Response.success("Filtered role groups fetched successfully", result, HttpStatus.OK.value()));
	}

	@GetMapping("/searchRoleGroups")
	public ResponseEntity<Response<PagedResult<RoleGroupManagementPojo>>> searchRoleGroups(
			@RequestParam Map<String, Object> params) {
		logger.debug("searchRoleGroups START");
		logger.debug("searchRoleGroups params={}", params);

		PagedResult<RoleGroupManagementPojo> result = roleGroupService.searchRoleGroups(params);

		logger.debug("searchRoleGroups return={}", result);
		return ResponseEntity
				.ok(Response.success("Searched role groups fetched successfully", result, HttpStatus.OK.value()));
	}

	// ---------------- COUNT ----------------
	@GetMapping("/countRoleGroups")
	public ResponseEntity<Response<Long>> countRoleGroups(@RequestParam Map<String, Object> params) {
		logger.debug("countRoleGroups START");
		logger.debug("countRoleGroups params={}", params);

		long count = roleGroupService.countRoleGroups(params);

		logger.debug("countRoleGroups return={}", count);
		return ResponseEntity.ok(Response.success("Count fetched successfully", count, HttpStatus.OK.value()));
	}
}
