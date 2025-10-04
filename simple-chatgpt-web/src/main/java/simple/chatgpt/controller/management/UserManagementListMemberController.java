package simple.chatgpt.controller.management;

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

import simple.chatgpt.pojo.management.UserManagementListMemberPojo;
import simple.chatgpt.service.management.UserManagementListMemberService;
import simple.chatgpt.util.PagedResult;
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
            @RequestBody UserManagementListMemberPojo member
    ) {
        logger.debug("createMember #############");
        logger.debug("createMember member={}", member);
        logger.debug("createMember member.userName={}", member.getUserName());
        logger.debug("createMember member.userKey={}", member.getUserKey());
        logger.debug("createMember member.firstName={}", member.getFirstName());
        logger.debug("createMember member.lastName={}", member.getLastName());
        logger.debug("createMember member.email={}", member.getEmail());
        logger.debug("createMember member.addressLine1={}", member.getAddressLine1());
        logger.debug("createMember member.addressLine2={}", member.getAddressLine2());
        logger.debug("createMember member.city={}", member.getCity());
        logger.debug("createMember member.state={}", member.getState());
        logger.debug("createMember member.postCode={}", member.getPostCode());
        logger.debug("createMember member.country={}", member.getCountry());
        logger.debug("createMember #############");

        Map<String, Object> params = new HashMap<>();
        params.put("member", member);
        
        logger.debug("createMember #############");
        logger.debug("createMember params={}", params);
        logger.debug("createMember #############");
        memberService.createMember(params);
        
        logger.debug("createMember #############");
        logger.debug("createMember DONE!!!");
        logger.debug("createMember #############");

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.success("Member created successfully", member, HttpStatus.CREATED.value()));
    }

    // 📖 GET MEMBER BY ID
    @GetMapping("/get")
    public ResponseEntity<Response<UserManagementListMemberPojo>> getMemberById(@RequestParam Long memberId) {
        logger.debug("getMemberById #############");
        logger.debug("getMemberById memberId={}", memberId);
        logger.debug("getMemberById #############");

        Map<String, Object> params = new HashMap<>();
        params.put("memberId", memberId);

        UserManagementListMemberPojo member = memberService.getMemberById(params);
        if (member == null) {
            return ResponseEntity.ok(Response.error("Member not found", null, HttpStatus.NOT_FOUND.value()));
        }

        return ResponseEntity.ok(Response.success("Member fetched successfully", member, HttpStatus.OK.value()));
    }

    // 📝 UPDATE MEMBER
    @PutMapping("/update")
    public ResponseEntity<Response<UserManagementListMemberPojo>> updateMemberById(
            @RequestParam Long memberId,
            @RequestBody UserManagementListMemberPojo member
    ) {
        logger.debug("updateMemberById #############");
        logger.debug("updateMemberById memberId={}", memberId);
        logger.debug("updateMemberById member={}", member);
        logger.debug("updateMemberById member.userName={}", member.getUserName());
        logger.debug("updateMemberById member.firstName={}", member.getFirstName());
        logger.debug("updateMemberById member.lastName={}", member.getLastName());
        logger.debug("updateMemberById member.email={}", member.getEmail());
        logger.debug("updateMemberById #############");

        Map<String, Object> params = new HashMap<>();
        params.put("memberId", memberId);
        params.put("member", member);
        logger.debug("updateMemberById #############");
        logger.debug("updateMemberById params={}", params);
        logger.debug("updateMemberById #############");
        
        UserManagementListMemberPojo updatedMember = memberService.updateMemberById(params);
        return ResponseEntity.ok(Response.success("Member updated successfully", updatedMember, HttpStatus.OK.value()));
    }

    // 🗑 DELETE MEMBER
    @DeleteMapping("/delete")
    public ResponseEntity<Response<Void>> deleteMemberById(@RequestParam Long memberId) {
        logger.debug("deleteMemberById #############");
        logger.debug("deleteMemberById memberId={}", memberId);
        logger.debug("deleteMemberById #############");

        Map<String, Object> params = new HashMap<>();
        params.put("memberId", memberId);
        memberService.deleteMemberById(params);

        return ResponseEntity.ok(Response.success("Member deleted successfully", null, HttpStatus.OK.value()));
    }

    // 🔍 SEARCH MEMBERS
    @GetMapping("/search")
    public ResponseEntity<Response<PagedResult<UserManagementListMemberPojo>>> searchMembers(
            @RequestParam Map<String, Object> params
    ) {
        logger.debug("searchMembers #############");
        logger.debug("searchMembers params={}", params);
        logger.debug("searchMembers #############");

        Map<String, Object> serviceParams = new HashMap<>(params);

        int page = params.get("page") != null ? Integer.parseInt(params.get("page").toString()) : 0;
        int size = params.get("size") != null ? Integer.parseInt(params.get("size").toString()) : 20;
        int offset = page * size;

        serviceParams.put("page", page);
        serviceParams.put("size", size);
        serviceParams.put("offset", offset);
        serviceParams.put("limit", size);

        serviceParams.put("sortField", params.getOrDefault("sortField", "id"));
        serviceParams.put("sortDirection", params.getOrDefault("sortDirection", "ASC"));

        if (params.get("listId") != null) {
            serviceParams.put("listId", Long.parseLong(params.get("listId").toString()));
        }

        logger.debug("searchMembers #############");
        logger.debug("searchMembers serviceParams={}", serviceParams);
        logger.debug("searchMembers #############");
        PagedResult<UserManagementListMemberPojo> members = memberService.searchMembers(serviceParams);
        logger.debug("searchMembers #############");
        logger.debug("searchMembers members={}", members);
        logger.debug("searchMembers #############");
        
        return ResponseEntity.ok(Response.success("Members fetched successfully", members, HttpStatus.OK.value()));
    }

    // ------------------ COUNT MEMBERS ------------------
    @GetMapping("/count")
    public ResponseEntity<Response<Long>> countMembers(@RequestParam Map<String, Object> params) {
        logger.debug("countMembers #############");
        logger.debug("countMembers params={}", params);
        logger.debug("countMembers #############");

        Map<String, Object> serviceParams = new HashMap<>(params);
        if (params.get("listId") != null) {
            serviceParams.put("listId", Long.parseLong(params.get("listId").toString()));
        }

        long count = memberService.countMembers(serviceParams);
        return ResponseEntity.ok(Response.success("Count fetched successfully", count, HttpStatus.OK.value()));
    }

}
