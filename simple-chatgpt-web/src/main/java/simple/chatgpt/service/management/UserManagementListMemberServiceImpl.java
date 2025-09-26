package simple.chatgpt.service.management;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import simple.chatgpt.mapper.management.UserManagementListMemberMapper;
import simple.chatgpt.pojo.management.UserManagementListMemberPojo;
import simple.chatgpt.util.PagedResult;

@Service
public class UserManagementListMemberServiceImpl implements UserManagementListMemberService {

    private static final Logger logger = LogManager.getLogger(UserManagementListMemberServiceImpl.class);

    private final UserManagementListMemberMapper mapper;

    public UserManagementListMemberServiceImpl(UserManagementListMemberMapper mapper) {
        logger.debug("UserManagementListMemberServiceImpl constructor called");
        this.mapper = mapper;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> ensureParams(Map<String, Object> params) {
        if (params == null) {
            params = new HashMap<>();
        }
        Map<String, Object> innerParams = (Map<String, Object>) params.get("params");
        if (innerParams == null) {
            innerParams = new HashMap<>();
            params.put("params", innerParams);
        }
        return innerParams;
    }

    // ------------------ SEARCH / LIST ------------------
    @Override
    public PagedResult<UserManagementListMemberPojo> searchMembers(Map<String, Object> params) {
        Map<String, Object> innerParams = ensureParams(params);

        logger.debug("searchMembers called with innerParams={}", innerParams);
        for (Map.Entry<String, Object> entry : innerParams.entrySet()) {
            logger.debug("searchMembers param {}={}", entry.getKey(), entry.getValue());
        }

        int page = 0;
        int size = 20;
        try {
            if (innerParams.get("page") != null) page = Integer.parseInt(innerParams.get("page").toString());
            if (innerParams.get("size") != null) size = Integer.parseInt(innerParams.get("size").toString());
        } catch (NumberFormatException e) {
            logger.warn("Invalid page or size format, using defaults page=0 size=20", e);
        }

        int offset = page * size;
        String sortField = (String) innerParams.getOrDefault("sortField", "id");
        String sortDirection = ((String) innerParams.getOrDefault("sortDirection", "ASC")).toUpperCase();

        // put pagination and sorting directly into innerParams so XML can see #{params.offset} etc.
        innerParams.put("offset", offset);
        innerParams.put("limit", size);
        innerParams.put("sortField", sortField);
        innerParams.put("sortDirection", sortDirection);

        List<UserManagementListMemberPojo> members;
        long total = 0;
        try {
            members = mapper.findMembers(innerParams);
            total = mapper.countMembers(innerParams);
            logger.debug("searchMembers result size={}", members != null ? members.size() : 0);
            logger.debug("searchMembers total count={}", total);
        } catch (Exception e) {
            logger.error("Error executing searchMembers", e);
            throw new RuntimeException("Database error during searchMembers", e);
        }

        return new PagedResult<>(members, total, page, size);
    }

    @Override
    public long countMembers(Map<String, Object> params) {
        Map<String, Object> innerParams = ensureParams(params);

        logger.debug("countMembers called with innerParams={}", innerParams);
        for (Map.Entry<String, Object> entry : innerParams.entrySet()) {
            logger.debug("countMembers param {}={}", entry.getKey(), entry.getValue());
        }

        long count;
        try {
            count = mapper.countMembers(innerParams);
            logger.debug("countMembers result={}", count);
        } catch (Exception e) {
            logger.error("Error executing countMembers", e);
            throw new RuntimeException("Database error during countMembers", e);
        }

        return count;
    }

    // ------------------ READ ------------------
    @Override
    public UserManagementListMemberPojo getMemberById(Map<String, Object> params) {
        Map<String, Object> innerParams = ensureParams(params);
        logger.debug("getMemberById called with innerParams={}", innerParams);

        UserManagementListMemberPojo member = mapper.getMemberById(innerParams);
        logger.debug("getMemberById result={}", member);
        return member;
    }

    @Override
    public UserManagementListMemberPojo getMemberByUserName(Map<String, Object> params) {
        Map<String, Object> innerParams = ensureParams(params);
        logger.debug("getMemberByUserName called with innerParams={}", innerParams);

        UserManagementListMemberPojo member = mapper.getMemberByUserName(innerParams);
        logger.debug("getMemberByUserName result={}", member);
        return member;
    }

    // ------------------ CREATE ------------------
    @Override
    public UserManagementListMemberPojo createMember(Map<String, Object> params) {
        Map<String, Object> innerParams = ensureParams(params);
        logger.debug("createMember called with innerParams={}", innerParams);

        mapper.insertMember(innerParams);
        logger.debug("createMember inserted member={}", innerParams.get("member"));
        return (UserManagementListMemberPojo) innerParams.get("member");
    }

    @Override
    public int batchCreateMembers(Map<String, Object> params) {
        Map<String, Object> innerParams = ensureParams(params);
        logger.debug("batchCreateMembers called with innerParams={}", innerParams);

        int count = mapper.batchInsertMembers(innerParams);
        logger.debug("batchCreateMembers inserted count={}", count);
        return count;
    }

    // ------------------ UPDATE ------------------
    @Override
    public UserManagementListMemberPojo updateMemberById(Map<String, Object> params) {
        Map<String, Object> innerParams = ensureParams(params);
        logger.debug("updateMemberById called with innerParams={}", innerParams);

        mapper.updateMemberById(innerParams);
        logger.debug("updateMemberById updated member={}", innerParams.get("member"));
        return (UserManagementListMemberPojo) innerParams.get("member");
    }

    @Override
    public UserManagementListMemberPojo updateMemberByUserName(Map<String, Object> params) {
        Map<String, Object> innerParams = ensureParams(params);
        logger.debug("updateMemberByUserName called with innerParams={}", innerParams);

        mapper.updateMemberByUserName(innerParams);
        logger.debug("updateMemberByUserName updated member={}", innerParams.get("member"));
        return (UserManagementListMemberPojo) innerParams.get("member");
    }

    // ------------------ DELETE ------------------
    @Override
    public void deleteMemberById(Map<String, Object> params) {
        Map<String, Object> innerParams = ensureParams(params);
        logger.debug("deleteMemberById called with innerParams={}", innerParams);

        mapper.deleteMemberById(innerParams);
        logger.debug("deleteMemberById completed for id={}", innerParams.get("id"));
    }

    @Override
    public void deleteMemberByUserName(Map<String, Object> params) {
        Map<String, Object> innerParams = ensureParams(params);
        logger.debug("deleteMemberByUserName called with innerParams={}", innerParams);

        mapper.deleteMemberByUserName(innerParams);
        logger.debug("deleteMemberByUserName completed for userName={}", innerParams.get("userName"));
    }

    @Override
    public void deleteMembersByListId(Map<String, Object> params) {
        Map<String, Object> innerParams = ensureParams(params);
        logger.debug("deleteMembersByListId called with innerParams={}", innerParams);

        mapper.deleteMembersByListId(innerParams);
        logger.debug("deleteMembersByListId completed for listId={}", innerParams.get("listId"));
    }
}
