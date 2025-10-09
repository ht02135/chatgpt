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
        logger.debug("searchMembers START");
        logger.debug("searchMembers params={}", params);

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

        logger.debug("searchMembers sqlParams={}", sqlParams);

        List<UserManagementListMemberPojo> members;
        long total = 0;
        try {
            members = mapper.findMembers(sqlParams);
            total = mapper.countMembers(params);
        } catch (Exception e) {
            logger.error("Error executing searchMembers", e);
            throw new RuntimeException("Database error during searchMembers", e);
        }

        logger.debug("searchMembers DONE");
        return new PagedResult<>(members, total, page, size);
    }

    @Override
    public long countMembers(Map<String, Object> params) {
        logger.debug("countMembers START");
        logger.debug("countMembers params={}", params);

        long count;
        try {
            count = mapper.countMembers(params);
        } catch (Exception e) {
            logger.error("Error executing countMembers", e);
            throw new RuntimeException("Database error during countMembers", e);
        }

        logger.debug("countMembers DONE");
        return count;
    }

    // ------------------ READ ------------------
    @Override
    public UserManagementListMemberPojo getMemberById(Map<String, Object> params) {
        logger.debug("getMemberById START");
        logger.debug("getMemberById params={}", params);

        UserManagementListMemberPojo member = mapper.getMemberById(params);

        logger.debug("getMemberById DONE");
        return member;
    }

    @Override
    public UserManagementListMemberPojo getMemberByUserName(Map<String, Object> params) {
        logger.debug("getMemberByUserName START");
        logger.debug("getMemberByUserName params={}", params);

        UserManagementListMemberPojo member = mapper.getMemberByUserName(params);

        logger.debug("getMemberByUserName DONE");
        return member;
    }

    // ------------------ CREATE ------------------
    @Override
    public UserManagementListMemberPojo createMember(Map<String, Object> params) {
        logger.debug("createMember START");
        logger.debug("createMember params={}", params);

        mapper.insertMember(params);

        logger.debug("createMember DONE");
        return (UserManagementListMemberPojo) ParamWrapper.unwrap(params, "member");
    }

    @Override
    public int batchCreateMembers(Map<String, Object> params) {
        logger.debug("batchCreateMembers START");
        logger.debug("batchCreateMembers params={}", params);

        int count = mapper.batchInsertMembers(params);

        logger.debug("batchCreateMembers DONE");
        return count;
    }

    // ------------------ UPDATE ------------------
    @Override
    public UserManagementListMemberPojo updateMemberById(Map<String, Object> params) {
        logger.debug("updateMemberById START");
        logger.debug("updateMemberById params={}", params);

        mapper.updateMemberById(params);

        logger.debug("updateMemberById DONE");
        return (UserManagementListMemberPojo) ParamWrapper.unwrap(params, "member");
    }

    @Override
    public UserManagementListMemberPojo updateMemberByUserName(Map<String, Object> params) {
        logger.debug("updateMemberByUserName START");
        logger.debug("updateMemberByUserName params={}", params);

        mapper.updateMemberByUserName(params);

        logger.debug("updateMemberByUserName DONE");
        return (UserManagementListMemberPojo) ParamWrapper.unwrap(params, "member");
    }

    // ------------------ DELETE ------------------
    @Override
    public void deleteMemberById(Map<String, Object> params) {
        logger.debug("deleteMemberById START");
        logger.debug("deleteMemberById params={}", params);

        mapper.deleteMemberById(params);

        logger.debug("deleteMemberById DONE");
    }

    @Override
    public void deleteMemberByUserName(Map<String, Object> params) {
        logger.debug("deleteMemberByUserName START");
        logger.debug("deleteMemberByUserName params={}", params);

        mapper.deleteMemberByUserName(params);

        logger.debug("deleteMemberByUserName DONE");
    }

    @Override
    public void deleteMembersByListId(Map<String, Object> params) {
        logger.debug("deleteMembersByListId START");
        logger.debug("deleteMembersByListId params={}", params);

        mapper.deleteMembersByListId(params);

        logger.debug("deleteMembersByListId DONE");
    }

}
