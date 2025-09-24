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
        this.mapper = mapper;
    }

    // ------------------ SEARCH / LIST ------------------
    @Override
    public PagedResult<UserManagementListMemberPojo> searchMembers(Map<String, Object> params) {
        logger.debug("searchMembers called with params={}", params);

        // safely parse page & size
        int page = 0;
        int size = 20;
        try {
            if (params.get("page") != null) page = Integer.parseInt(params.get("page").toString());
            if (params.get("size") != null) size = Integer.parseInt(params.get("size").toString());
        } catch (NumberFormatException e) {
            logger.warn("Invalid page or size format, using defaults page=0 size=20", e);
        }

        int offset = page * size;
        String sortField = (String) params.getOrDefault("sortField", "id");
        String sortDirection = ((String) params.getOrDefault("sortDirection", "ASC")).toUpperCase();

        Map<String, Object> sqlParams = new HashMap<>(params);
        sqlParams.put("offset", offset);
        sqlParams.put("limit", size);
        sqlParams.put("sortField", sortField);
        sqlParams.put("sortDirection", sortDirection);

        // log every param individually
        for (Map.Entry<String, Object> entry : sqlParams.entrySet()) {
            logger.debug("searchMembers param {}={}", entry.getKey(), entry.getValue());
        }

        List<UserManagementListMemberPojo> members;
        long total = 0;
        try {
            members = mapper.findMembers(sqlParams);
            total = mapper.countMembers(params);  // total for all pages
            logger.debug("searchMembers result size={}", members != null ? members.size() : 0);
            logger.debug("searchMembers total count={}", total);
        } catch (Exception e) {
            logger.error("Error executing searchMembers with params={}", sqlParams, e);
            throw new RuntimeException("Database error during searchMembers", e);
        }

        return new PagedResult<>(members, total, page, size);
    }

    @Override
    public long countMembers(Map<String, Object> params) {
        logger.debug("countMembers called with params={}", params);

        Map<String, Object> sqlParams = new HashMap<>(params);
        for (Map.Entry<String, Object> entry : sqlParams.entrySet()) {
            logger.debug("countMembers param {}={}", entry.getKey(), entry.getValue());
        }

        long count;
        try {
            count = mapper.countMembers(sqlParams);
            logger.debug("countMembers result={}", count);
        } catch (Exception e) {
            logger.error("Error executing countMembers with params={}", sqlParams, e);
            throw new RuntimeException("Database error during countMembers", e);
        }

        return count;
    }

    // ------------------ READ ------------------
    @Override
    public UserManagementListMemberPojo getMemberById(Map<String, Object> params) {
        logger.debug("getMemberById called with params={}", params);
        UserManagementListMemberPojo member = mapper.getMemberById(params);
        logger.debug("getMemberById result={}", member);
        return member;
    }

    @Override
    public UserManagementListMemberPojo getMemberByUserName(Map<String, Object> params) {
        logger.debug("getMemberByUserName called with params={}", params);
        UserManagementListMemberPojo member = mapper.getMemberByUserName(params);
        logger.debug("getMemberByUserName result={}", member);
        return member;
    }

    // ------------------ CREATE ------------------
    @Override
    public UserManagementListMemberPojo createMember(Map<String, Object> params) {
        logger.debug("createMember called with params={}", params);
        mapper.insertMember(params);
        logger.debug("createMember inserted member={}", params.get("member"));
        return (UserManagementListMemberPojo) params.get("member");
    }

    @Override
    public int batchCreateMembers(Map<String, Object> params) {
        logger.debug("batchCreateMembers called with params={}", params);
        int count = mapper.batchInsertMembers(params);
        logger.debug("batchCreateMembers inserted count={}", count);
        return count;
    }

    // ------------------ UPDATE ------------------
    @Override
    public UserManagementListMemberPojo updateMemberById(Map<String, Object> params) {
        logger.debug("updateMemberById called with params={}", params);
        mapper.updateMemberById(params);
        logger.debug("updateMemberById updated member={}", params.get("member"));
        return (UserManagementListMemberPojo) params.get("member");
    }

    @Override
    public UserManagementListMemberPojo updateMemberByUserName(Map<String, Object> params) {
        logger.debug("updateMemberByUserName called with params={}", params);
        mapper.updateMemberByUserName(params);
        logger.debug("updateMemberByUserName updated member={}", params.get("member"));
        return (UserManagementListMemberPojo) params.get("member");
    }

    // ------------------ DELETE ------------------
    @Override
    public void deleteMemberById(Map<String, Object> params) {
        logger.debug("deleteMemberById called with params={}", params);
        mapper.deleteMemberById(params);
        logger.debug("deleteMemberById completed for id={}", params.get("id"));
    }

    @Override
    public void deleteMemberByUserName(Map<String, Object> params) {
        logger.debug("deleteMemberByUserName called with params={}", params);
        mapper.deleteMemberByUserName(params);
        logger.debug("deleteMemberByUserName completed for userName={}", params.get("userName"));
    }

    @Override
    public void deleteMembersByListId(Map<String, Object> params) {
        logger.debug("deleteMembersByListId called with params={}", params);
        mapper.deleteMembersByListId(params);
        logger.debug("deleteMembersByListId completed for listId={}", params.get("listId"));
    }
}
