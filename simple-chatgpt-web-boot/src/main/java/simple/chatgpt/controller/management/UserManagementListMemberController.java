package simple.chatgpt.controller.management;

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

import simple.chatgpt.pojo.management.UserManagementListMemberPojo;
import simple.chatgpt.service.management.UserManagementListMemberService;
import simple.chatgpt.util.PagedResult;
import simple.chatgpt.util.Response;
import simple.chatgpt.util.SafeConverter;

@RestController
@RequestMapping(value = "/management/userlistmembers", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserManagementListMemberController {

	private static final Logger logger = LogManager.getLogger(UserManagementListMemberController.class);

	private final UserManagementListMemberService memberService;

	public UserManagementListMemberController(UserManagementListMemberService memberService) {
		this.memberService = memberService;
		logger.debug("UserManagementListMemberController constructor called, memberService={}", memberService);
	}

    // ==============================================================
    // ================ 5 CORE METHODS (on top) =====================
    // ==============================================================

    @PostMapping("/create")
    public ResponseEntity<Response<UserManagementListMemberPojo>> create(
            @RequestBody(required = false) UserManagementListMemberPojo member) {
        logger.debug("create called");
        logger.debug("create member={}", member);

        if (member == null) {
            logger.debug("create: missing member payload");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error("Missing member payload", null, HttpStatus.BAD_REQUEST.value()));
        }

        UserManagementListMemberPojo saved = memberService.create(member);
        return ResponseEntity.ok(Response.success("Created successfully", saved, HttpStatus.OK.value()));
    }

    @PutMapping("/update")
    public ResponseEntity<Response<UserManagementListMemberPojo>> update(
            @RequestParam(required = false) Long id,
            @RequestBody(required = false) UserManagementListMemberPojo member) {
        logger.debug("update called");
        logger.debug("update id={}", id);
        logger.debug("update member={}", member);

        if (id == null) {
            logger.debug("update: missing memberId");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error("Missing memberId parameter", null, HttpStatus.BAD_REQUEST.value()));
        }
        if (member == null) {
            logger.debug("update: missing member payload");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error("Missing member payload", null, HttpStatus.BAD_REQUEST.value()));
        }

        UserManagementListMemberPojo updated = memberService.update(id, member);
        return ResponseEntity.ok(Response.success("Updated successfully", updated, HttpStatus.OK.value()));
    }

    @GetMapping("/search")
    public ResponseEntity<Response<PagedResult<UserManagementListMemberPojo>>> search(
            @RequestParam Map<String, String> params) {
        logger.debug("search called");
        logger.debug("search params={}", params);

        if (params == null || params.isEmpty()) {
            logger.debug("search: missing parameters");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error("Missing parameters", null, HttpStatus.BAD_REQUEST.value()));
        }

        // Add only params used in XML
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

        PagedResult<UserManagementListMemberPojo> result = memberService.search(params);
        return ResponseEntity.ok(Response.success("Fetched successfully", result, HttpStatus.OK.value()));
    }

    @GetMapping("/get")
    public ResponseEntity<Response<UserManagementListMemberPojo>> get(
            @RequestParam(required = false) Long id) {
        logger.debug("get called");
        logger.debug("get id={}", id);

        if (id == null) {
            logger.debug("get: missing memberId");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error("Missing memberId parameter", null, HttpStatus.BAD_REQUEST.value()));
        }

        UserManagementListMemberPojo member = memberService.get(id);
        return ResponseEntity.ok(Response.success("Fetched successfully", member, HttpStatus.OK.value()));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Response<Void>> delete(
            @RequestParam(required = false) Long id) {
        logger.debug("delete called");
        logger.debug("delete memberId={}", id);

        if (id == null) {
            logger.debug("delete: missing memberId");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error("Missing memberId parameter", null, HttpStatus.BAD_REQUEST.value()));
        }

        memberService.delete(id);
        return ResponseEntity.ok(Response.success("Deleted successfully", null, HttpStatus.OK.value()));
    }

	// ==============================================================
	// ================ EXISTING METHODS (without URL mapping) ======
	// ==============================================================

}
