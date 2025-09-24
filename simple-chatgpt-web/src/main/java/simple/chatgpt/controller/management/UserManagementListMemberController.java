package simple.chatgpt.controller.management;

import java.util.HashMap;
import java.util.List;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import simple.chatgpt.pojo.management.UserManagementListMemberPojo;
import simple.chatgpt.service.management.UserManagementListMemberService;
import simple.chatgpt.util.Response;

@RestController
@RequestMapping(value = "/management/userlistmembers", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserManagementListMemberController {

    private static final Logger logger = LogManager.getLogger(UserManagementListMemberController.class);

    private final UserManagementListMemberService memberService;

    public UserManagementListMemberController(UserManagementListMemberService memberService) {
        this.memberService = memberService;
    }

    // ➕ CREATE MEMBER
    @PostMapping("/create")
    public ResponseEntity<Response<UserManagementListMemberPojo>> createMember(
            @RequestPart("member") UserManagementListMemberPojo member
    ) {
        logger.debug("createMember #############");
        logger.debug("createMember member={}", member);
        logger.debug("createMember member.userName={}", member.getUserName());
        logger.debug("createMember member.firstName={}", member.getFirstName());
        logger.debug("createMember member.lastName={}", member.getLastName());
        logger.debug("createMember member.email={}", member.getEmail());
        logger.debug("createMember #############");

        Map<String, Object> params = new HashMap<>();
        params.put("member", member);
        memberService.createMember(params);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.success("Member created successfully", member, HttpStatus.CREATED.value()));
    }

    // 📖 GET MEMBER BY ID
    @GetMapping("/get")
    public ResponseEntity<Response<UserManagementListMemberPojo>> getMember(@RequestParam Long memberId) {
        logger.debug("getMember #############");
        logger.debug("getMember memberId={}", memberId);
        logger.debug("getMember #############");

        Map<String, Object> params = new HashMap<>();
        params.put("id", memberId);

        UserManagementListMemberPojo member = memberService.getMemberById(params);
        if (member == null) {
            return ResponseEntity.ok(Response.error("Member not found", null, HttpStatus.NOT_FOUND.value()));
        }

        return ResponseEntity.ok(Response.success("Member fetched successfully", member, HttpStatus.OK.value()));
    }

    // 📝 UPDATE MEMBER
    @PutMapping("/update")
    public ResponseEntity<Response<UserManagementListMemberPojo>> updateMember(
            @RequestParam Long id,
            @RequestBody UserManagementListMemberPojo member
    ) {
        logger.debug("updateMember #############");
        logger.debug("updateMember id={}", id);
        logger.debug("updateMember member={}", member);
        logger.debug("updateMember member.userName={}", member.getUserName());
        logger.debug("updateMember member.firstName={}", member.getFirstName());
        logger.debug("updateMember member.lastName={}", member.getLastName());
        logger.debug("updateMember member.email={}", member.getEmail());
        logger.debug("updateMember #############");

        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("member", member);

        UserManagementListMemberPojo updatedMember = memberService.updateMemberById(params);
        return ResponseEntity.ok(Response.success("Member updated successfully", updatedMember, HttpStatus.OK.value()));
    }

    // 🗑 DELETE MEMBER
    @DeleteMapping("/delete")
    public ResponseEntity<Response<Void>> deleteMember(@RequestParam Long memberId) {
        logger.debug("deleteMember #############");
        logger.debug("deleteMember memberId={}", memberId);
        logger.debug("deleteMember #############");

        Map<String, Object> params = new HashMap<>();
        params.put("id", memberId);
        memberService.deleteMemberById(params);

        return ResponseEntity.ok(Response.success("Member deleted successfully", null, HttpStatus.OK.value()));
    }

    // 🔍 SEARCH MEMBERS BY PARAMETERS
 // ------------------ SEARCH MEMBERS ------------------
    @GetMapping("/search")
    public ResponseEntity<Response<List<UserManagementListMemberPojo>>> searchMembers(
            @RequestParam Map<String, Object> params
    ) {
        logger.debug("searchMembers #############");
        logger.debug("searchMembers params={}", params);
        logger.debug("searchMembers #############");

        Map<String, Object> serviceParams = new HashMap<>(params);

        // Convert page & size
        int page = params.get("page") != null ? Integer.parseInt(params.get("page").toString()) : 0;
        int size = params.get("size") != null ? Integer.parseInt(params.get("size").toString()) : 20;
        int offset = page * size;

        serviceParams.put("page", page);
        serviceParams.put("size", size);
        serviceParams.put("offset", offset);
        serviceParams.put("limit", size);

        // Ensure sort defaults
        serviceParams.put("sortField", params.getOrDefault("sortField", "id"));
        serviceParams.put("sortDirection", params.getOrDefault("sortDirection", "ASC"));

        List<UserManagementListMemberPojo> members = memberService.searchMembers(serviceParams);
        return ResponseEntity.ok(Response.success("Members fetched successfully", members, HttpStatus.OK.value()));
    }

    // ------------------ COUNT MEMBERS ------------------
    @GetMapping("/count")
    public ResponseEntity<Response<Long>> countMembers(
            @RequestParam Map<String, Object> params
    ) {
        logger.debug("countMembers #############");
        logger.debug("countMembers params={}", params);
        logger.debug("countMembers #############");

        Map<String, Object> serviceParams = new HashMap<>(params);

        // Convert numeric params if present
        if (params.get("userListId") != null) {
            serviceParams.put("userListId", Long.parseLong(params.get("userListId").toString()));
        }

        long count = memberService.countMembers(serviceParams);
        return ResponseEntity.ok(Response.success("Count fetched successfully", count, HttpStatus.OK.value()));
    }

}
