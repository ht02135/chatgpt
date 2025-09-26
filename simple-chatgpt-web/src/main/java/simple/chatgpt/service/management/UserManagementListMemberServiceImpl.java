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
    private Map<String, Object> getParamsMap(Map<String, Object> params) {
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
        logger.debug("searchMembers called");
        Map<String, Object> innerParams = getParamsMap(params);

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
        innerParams.put("offset", offset);
        innerParams.put("limit", size);
        innerParams.put("sortField", innerParams.getOrDefault("sortField", "id"));
        innerParams.put("sortDirection", ((String) innerParams.getOrDefault("sortDirection", "ASC")).toUpperCase());

        Map<String, Object> wrapperParam = new HashMap<>();
        wrapperParam.put("params", innerParams);

        List<UserManagementListMemberPojo> members;
        long total = 0;
        try {
            members = mapper.findMembers(wrapperParam);
            total = mapper.countMembers(wrapperParam);
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
        logger.debug("countMembers called");
        Map<String, Object> innerParams = getParamsMap(params);

        for (Map.Entry<String, Object> entry : innerParams.entrySet()) {
            logger.debug("countMembers param {}={}", entry.getKey(), entry.getValue());
        }

        Map<String, Object> wrapperParam = new HashMap<>();
        wrapperParam.put("params", innerParams);

        long count;
        try {
            count = mapper.countMembers(wrapperParam);
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
        logger.debug("getMemberById called");
        Map<String, Object> innerParams = getParamsMap(params);

        for (Map.Entry<String, Object> entry : innerParams.entrySet()) {
            logger.debug("getMemberById param {}={}", entry.getKey(), entry.getValue());
        }

        Map<String, Object> wrapperParam = new HashMap<>();
        wrapperParam.put("params", innerParams);

        UserManagementListMemberPojo member = mapper.getMemberById(wrapperParam);
        logger.debug("getMemberById result={}", member);
        return member;
    }

    @Override
    public UserManagementListMemberPojo getMemberByUserName(Map<String, Object> params) {
        logger.debug("getMemberByUserName called");
        Map<String, Object> innerParams = getParamsMap(params);

        for (Map.Entry<String, Object> entry : innerParams.entrySet()) {
            logger.debug("getMemberByUserName param {}={}", entry.getKey(), entry.getValue());
        }

        Map<String, Object> wrapperParam = new HashMap<>();
        wrapperParam.put("params", innerParams);

        UserManagementListMemberPojo member = mapper.getMemberByUserName(wrapperParam);
        logger.debug("getMemberByUserName result={}", member);
        return member;
    }

    // ------------------ CREATE ------------------
    @Override
    public UserManagementListMemberPojo createMember(Map<String, Object> params) {
        logger.debug("createMember called");
        Map<String, Object> innerParams = getParamsMap(params);

        for (Map.Entry<String, Object> entry : innerParams.entrySet()) {
            logger.debug("createMember param {}={}", entry.getKey(), entry.getValue());
        }

        Map<String, Object> wrapperParam = new HashMap<>();
        wrapperParam.put("params", innerParams);

        mapper.insertMember(wrapperParam);
        logger.debug("createMember inserted member={}", innerParams.get("member"));
        return (UserManagementListMemberPojo) innerParams.get("member");
    }

    @Override
    public int batchCreateMembers(Map<String, Object> params) {
        logger.debug("batchCreateMembers called");
        Map<String, Object> innerParams = getParamsMap(params);

        for (Map.Entry<String, Object> entry : innerParams.entrySet()) {
            logger.debug("batchCreateMembers param {}={}", entry.getKey(), entry.getValue());
        }

        Map<String, Object> wrapperParam = new HashMap<>();
        wrapperParam.put("params", innerParams);

        int count = mapper.batchInsertMembers(wrapperParam);
        logger.debug("batchCreateMembers inserted count={}", count);
        return count;
    }

    // ------------------ UPDATE ------------------
    @Override
    public UserManagementListMemberPojo updateMemberById(Map<String, Object> params) {
        logger.debug("updateMemberById called");
        Map<String, Object> innerParams = getParamsMap(params);

        for (Map.Entry<String, Object> entry : innerParams.entrySet()) {
            logger.debug("updateMemberById param {}={}", entry.getKey(), entry.getValue());
        }

        Map<String, Object> wrapperParam = new HashMap<>();
        wrapperParam.put("params", innerParams);

        mapper.updateMemberById(wrapperParam);
        logger.debug("updateMemberById updated member={}", innerParams.get("member"));
        return (UserManagementListMemberPojo) innerParams.get("member");
    }

    @Override
    public UserManagementListMemberPojo updateMemberByUserName(Map<String, Object> params) {
        logger.debug("updateMemberByUserName called");
        Map<String, Object> innerParams = getParamsMap(params);

        for (Map.Entry<String, Object> entry : innerParams.entrySet()) {
            logger.debug("updateMemberByUserName param {}={}", entry.getKey(), entry.getValue());
        }

        Map<String, Object> wrapperParam = new HashMap<>();
        wrapperParam.put("params", innerParams);

        mapper.updateMemberByUserName(wrapperParam);
        logger.debug("updateMemberByUserName updated member={}", innerParams.get("member"));
        return (UserManagementListMemberPojo) innerParams.get("member");
    }

    // ------------------ DELETE ------------------
    @Override
    public void deleteMemberById(Map<String, Object> params) {
        logger.debug("deleteMemberById called");
        Map<String, Object> innerParams = getParamsMap(params);

        for (Map.Entry<String, Object> entry : innerParams.entrySet()) {
            logger.debug("deleteMemberById param {}={}", entry.getKey(), entry.getValue());
        }

        Map<String, Object> wrapperParam = new HashMap<>();
        wrapperParam.put("params", innerParams);

        mapper.deleteMemberById(wrapperParam);
        logger.debug("deleteMemberById completed for id={}", innerParams.get("id"));
    }

    @Override
    public void deleteMemberByUserName(Map<String, Object> params) {
        logger.debug("deleteMemberByUserName called");
        Map<String, Object> innerParams = getParamsMap(params);

        for (Map.Entry<String, Object> entry : innerParams.entrySet()) {
            logger.debug("deleteMemberByUserName param {}={}", entry.getKey(), entry.getValue());
        }

        Map<String, Object> wrapperParam = new HashMap<>();
        wrapperParam.put("params", innerParams);

        mapper.deleteMemberByUserName(wrapperParam);
        logger.debug("deleteMemberByUserName completed for userName={}", innerParams.get("userName"));
    }

    @Override
    public void deleteMembersByListId(Map<String, Object> params) {
        logger.debug("deleteMembersByListId called");
        Map<String, Object> innerParams = getParamsMap(params);

        for (Map.Entry<String, Object> entry : innerParams.entrySet()) {
            logger.debug("deleteMembersByListId param {}={}", entry.getKey(), entry.getValue());
        }

        Map<String, Object> wrapperParam = new HashMap<>();
        wrapperParam.put("params", innerParams);

        mapper.deleteMembersByListId(wrapperParam);
        logger.debug("deleteMembersByListId completed for listId={}", innerParams.get("listId"));
    }
}
