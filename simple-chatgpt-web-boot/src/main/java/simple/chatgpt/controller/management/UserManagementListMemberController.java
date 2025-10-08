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
import simple.chatgpt.util.ParamWrapper;
import simple.chatgpt.util.Response;
import simple.chatgpt.util.SafeConverter;

@RestController
@RequestMapping(value = "/management/userlistmembers", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserManagementListMemberController implements UserManagementListMemberControllerApi {

    private static final Logger logger = LogManager.getLogger(UserManagementListMemberController.class);

    private final UserManagementListMemberService memberService;

    public UserManagementListMemberController(UserManagementListMemberService memberService) {
        this.memberService = memberService;
        logger.debug("UserManagementListMemberController constructor called, memberService={}", memberService);
    }

    // ➕ CREATE MEMBER
    @PostMapping("/create")
    public ResponseEntity<Response<UserManagementListMemberPojo>> createMember(
            @RequestBody UserManagementListMemberPojo member
    ) {
        logger.debug("createMember START");
        logger.debug("createMember member={}", member);

        Map<String, Object> params = new HashMap<>();
        params.put("member", member);

        UserManagementListMemberPojo newMember = memberService.createMember(params);

        logger.debug("createMember newMember={}", newMember);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.success("Member created successfully", newMember, HttpStatus.CREATED.value()));
    }

    // 📖 GET MEMBER BY ID
    @GetMapping("/get")
    public ResponseEntity<Response<UserManagementListMemberPojo>> getMemberById(@RequestParam Long memberId) {
        logger.debug("getMemberById START");
        logger.debug("getMemberById memberId={}", memberId);

        Map<String, Object> params = new HashMap<>();
        params.put("memberId", memberId);

        UserManagementListMemberPojo member = memberService.getMemberById(params);
        
        if (member == null) {
        	logger.debug("getMemberById Member not found");
            return ResponseEntity.ok(Response.error("Member not found", null, HttpStatus.NOT_FOUND.value()));
        }

        logger.debug("getMemberById return={}", member);
        return ResponseEntity.ok(Response.success("Member fetched successfully", member, HttpStatus.OK.value()));
    }

    // 📝 UPDATE MEMBER
    @PutMapping("/update")
    public ResponseEntity<Response<UserManagementListMemberPojo>> updateMemberById(
            @RequestParam Long memberId,
            @RequestBody UserManagementListMemberPojo member
    ) {
        logger.debug("updateMemberById START");
        logger.debug("updateMemberById memberId={}", memberId);
        logger.debug("updateMemberById member={}", member);

        Map<String, Object> params = new HashMap<>();
        params.put("memberId", memberId);
        params.put("member", member);

        UserManagementListMemberPojo updatedMember = memberService.updateMemberById(params);

        logger.debug("updateMemberById return={}", updatedMember);
        return ResponseEntity.ok(Response.success("Member updated successfully", updatedMember, HttpStatus.OK.value()));
    }

    // 🗑 DELETE MEMBER
    @DeleteMapping("/delete")
    public ResponseEntity<Response<Void>> deleteMemberById(@RequestParam Long memberId) {
        logger.debug("deleteMemberById START");
        logger.debug("deleteMemberById memberId={}", memberId);

        Map<String, Object> params = new HashMap<>();
        params.put("memberId", memberId);
        memberService.deleteMemberById(params);

        logger.debug("deleteMemberById DONE");
        return ResponseEntity.ok(Response.success("Member deleted successfully", null, HttpStatus.OK.value()));
    }

    // 🔍 SEARCH MEMBERS
    @GetMapping("/search")
    public ResponseEntity<Response<PagedResult<UserManagementListMemberPojo>>> searchMembers(
            @RequestParam Map<String, Object> params
    ) {
        logger.debug("searchMembers START");
        logger.debug("searchMembers params={}", params);

        Map<String, Object> serviceParams = new HashMap<>(params);

        // hung: DONT REMOVE THIS CODE
        int page = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "page", 0), 0); 
        int size = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "size", 20), 20);
        int offset = page * size;

        serviceParams.put("page", page);
        serviceParams.put("size", size);
        serviceParams.put("offset", offset);
        serviceParams.put("limit", size);
        serviceParams.put("sortField", ParamWrapper.unwrap(params, "sortField", "id"));
        serviceParams.put("sortDirection", ParamWrapper.unwrap(params, "sortDirection", "ASC").toUpperCase());
        serviceParams.put("listId", ParamWrapper.unwrap(params, "listId"));

        logger.debug("searchMembers serviceParams={}", serviceParams);

        PagedResult<UserManagementListMemberPojo> members = memberService.searchMembers(serviceParams);
        
        logger.debug("searchMembers return={}", members);
        return ResponseEntity.ok(Response.success("Members fetched successfully", members, HttpStatus.OK.value()));
    }

    // ------------------ COUNT MEMBERS ------------------
    @GetMapping("/count")
    public ResponseEntity<Response<Long>> countMembers(@RequestParam Map<String, Object> params) {
        logger.debug("countMembers START");
        logger.debug("countMembers params={}", params);

        Map<String, Object> serviceParams = new HashMap<>(params);
        serviceParams.put("listId", ParamWrapper.unwrap(params, "listId"));

        long count = memberService.countMembers(serviceParams);
        
        logger.debug("countMembers return={}", count);
        return ResponseEntity.ok(Response.success("Count fetched successfully", count, HttpStatus.OK.value()));
    }

}
