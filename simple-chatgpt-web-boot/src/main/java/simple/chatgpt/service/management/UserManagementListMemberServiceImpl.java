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
import simple.chatgpt.util.ParamWrapper;
import simple.chatgpt.util.SafeConverter;

@Service
public class UserManagementListMemberServiceImpl implements UserManagementListMemberService {

    private static final Logger logger = LogManager.getLogger(UserManagementListMemberServiceImpl.class);

    private final UserManagementListMemberMapper mapper;

    public UserManagementListMemberServiceImpl(UserManagementListMemberMapper mapper) {
        logger.debug("UserManagementListMemberServiceImpl constructor called");
        this.mapper = mapper;
    }

    // ------------------ SEARCH / LIST ------------------
    @Override
    public PagedResult<UserManagementListMemberPojo> searchMembers(Map<String, Object> params) {
        logger.debug("searchMembers called");
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            logger.debug("searchMembers param {}={}", entry.getKey(), entry.getValue());
        }

        // hung: DONT REMOVE THIS CODE
        int page = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "page", 0), 0); 
        int size = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "size", 20), 20);
        int offset = page * size;
        
        String sortField = ParamWrapper.unwrap(params, "sortField", "id");
        String sortDirection = ParamWrapper.unwrap(params, "sortDirection", "ASC").toUpperCase();

        Map<String, Object> sqlParams = new HashMap<>(params);
        sqlParams.put("offset", offset);
        sqlParams.put("limit", size);
        sqlParams.put("sortField", sortField);
        sqlParams.put("sortDirection", sortDirection);

        for (Map.Entry<String, Object> entry : sqlParams.entrySet()) {
            logger.debug("searchMembers sqlParam {}={}", entry.getKey(), entry.getValue());
        }

        List<UserManagementListMemberPojo> members;
        long total = 0;
        try {
            logger.debug("searchMembers sqlParams={}", sqlParams);
            members = mapper.findMembers(sqlParams);
            logger.debug("searchMembers params={}", params);
            total = mapper.countMembers(params);
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
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            logger.debug("countMembers param {}={}", entry.getKey(), entry.getValue());
        }

        long count;
        try {
            count = mapper.countMembers(params);
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
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            logger.debug("getMemberById param {}={}", entry.getKey(), entry.getValue());
        }

        UserManagementListMemberPojo member = mapper.getMemberById(params);
        logger.debug("getMemberById result={}", member);
        return member;
    }

    @Override
    public UserManagementListMemberPojo getMemberByUserName(Map<String, Object> params) {
        logger.debug("getMemberByUserName called");
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            logger.debug("getMemberByUserName param {}={}", entry.getKey(), entry.getValue());
        }

        UserManagementListMemberPojo member = mapper.getMemberByUserName(params);
        logger.debug("getMemberByUserName result={}", member);
        return member;
    }

    // ------------------ CREATE ------------------
    @Override
    public UserManagementListMemberPojo createMember(Map<String, Object> params) {
        logger.debug("createMember called");
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            logger.debug("createMember param {}={}", entry.getKey(), entry.getValue());
        }

        logger.debug("createMember #############");
        logger.debug("createMember params={}", params);
        logger.debug("createMember #############");
        mapper.insertMember(params);
        logger.debug("createMember #############");
        logger.debug("createMember DONE!!!");
        logger.debug("createMember #############");
        
        return (UserManagementListMemberPojo) ParamWrapper.unwrap(params, "member");
    }

    @Override
    public int batchCreateMembers(Map<String, Object> params) {
        logger.debug("batchCreateMembers called");
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            logger.debug("batchCreateMembers param {}={}", entry.getKey(), entry.getValue());
        }

        logger.debug("batchCreateMembers params={}", params);
        int count = mapper.batchInsertMembers(params);
        logger.debug("batchCreateMembers inserted count={}", count);
        return count;
    }

    // ------------------ UPDATE ------------------
    @Override
    public UserManagementListMemberPojo updateMemberById(Map<String, Object> params) {
        logger.debug("updateMemberById called");
        logger.debug("updateMemberById #############");
        logger.debug("updateMemberById updated params={}", params);
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            logger.debug("updateMemberById param {}={}", entry.getKey(), entry.getValue());
        }
        logger.debug("updateMemberById #############");

        mapper.updateMemberById(params);
        return (UserManagementListMemberPojo) ParamWrapper.unwrap(params, "member");
    }

    @Override
    public UserManagementListMemberPojo updateMemberByUserName(Map<String, Object> params) {
        logger.debug("updateMemberByUserName called");
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            logger.debug("updateMemberByUserName param {}={}", entry.getKey(), entry.getValue());
        }

        mapper.updateMemberByUserName(params);
        return (UserManagementListMemberPojo) ParamWrapper.unwrap(params, "member");
    }

    // ------------------ DELETE ------------------
    @Override
    public void deleteMemberById(Map<String, Object> params) {
        logger.debug("deleteMemberById called");
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            logger.debug("deleteMemberById param {}={}", entry.getKey(), entry.getValue());
        }

        mapper.deleteMemberById(params);
    }

    @Override
    public void deleteMemberByUserName(Map<String, Object> params) {
        logger.debug("deleteMemberByUserName called");
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            logger.debug("deleteMemberByUserName param {}={}", entry.getKey(), entry.getValue());
        }

        mapper.deleteMemberByUserName(params);
    }

    @Override
    public void deleteMembersByListId(Map<String, Object> params) {
        logger.debug("deleteMembersByListId called");
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            logger.debug("deleteMembersByListId param {}={}", entry.getKey(), entry.getValue());
        }

        mapper.deleteMembersByListId(params);
    }
    
}
