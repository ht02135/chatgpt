package simple.chatgpt.service.management;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import simple.chatgpt.mapper.management.UserManagementListMemberMapper;
import simple.chatgpt.pojo.management.UserManagementListMemberPojo;

@Service
public class UserManagementListMemberServiceImpl implements UserManagementListMemberService {

    private static final Logger logger = LogManager.getLogger(UserManagementListMemberServiceImpl.class);

    private final UserManagementListMemberMapper mapper;

    public UserManagementListMemberServiceImpl(UserManagementListMemberMapper mapper) {
        this.mapper = mapper;
    }

    // ------------------ SEARCH / LIST ------------------
    @Override
    public List<UserManagementListMemberPojo> searchMembers(Map<String, Object> params) {
        logger.debug("searchMembers called with params={}", params);

        int page = params.get("page") != null ? (int) params.get("page") : 0;
        int size = params.get("size") != null ? (int) params.get("size") : 20;
        int offset = page * size;

        String sortField = (String) params.getOrDefault("sortField", "id");
        String sortDirection = ((String) params.getOrDefault("sortDirection", "ASC")).toUpperCase();

        Map<String, Object> sqlParams = new HashMap<>(params);
        sqlParams.put("offset", offset);
        sqlParams.put("limit", size);
        sqlParams.put("sortField", sortField);
        sqlParams.put("sortDirection", sortDirection);

        // Log every param individually
        for (Map.Entry<String, Object> entry : sqlParams.entrySet()) {
            logger.debug("searchMembers param {}={}", entry.getKey(), entry.getValue());
        }

        List<UserManagementListMemberPojo> members;
        try {
            members = mapper.findMembers(sqlParams);
            logger.debug("searchMembers result size={}", members != null ? members.size() : 0);
        } catch (Exception e) {
            logger.error("Error executing searchMembers query with params={}", sqlParams, e);
            throw new RuntimeException("Database error during searchMembers", e);
        }

        return members;
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
            logger.error("Error executing countMembers query with params={}", sqlParams, e);
            throw new RuntimeException("Database error during countMembers", e);
        }

        return count;
    }

    // ------------------ READ ------------------
    @Override
    public UserManagementListMemberPojo getMemberById(Long id) {
        logger.debug("getMemberById called with id={}", id);
        UserManagementListMemberPojo member = mapper.getMemberById(id);
        logger.debug("getMemberById result={}", member);
        return member;
    }

    @Override
    public UserManagementListMemberPojo getMemberByUserName(String userName) {
        logger.debug("getMemberByUserName called with userName={}", userName);
        UserManagementListMemberPojo member = mapper.getMemberByUserName(userName);
        logger.debug("getMemberByUserName result={}", member);
        return member;
    }

    // ------------------ CREATE ------------------
    @Override
    public UserManagementListMemberPojo createMember(UserManagementListMemberPojo member) {
        logger.debug("createMember called with member={}", member);
        mapper.insertMember(member);
        logger.debug("createMember inserted member={}", member);
        return member;
    }

    @Override
    public int batchCreateMembers(List<UserManagementListMemberPojo> members) {
        logger.debug("batchCreateMembers called with members={}", members);
        int count = mapper.batchInsertMembers(members);
        logger.debug("batchCreateMembers inserted count={}", count);
        return count;
    }

    // ------------------ UPDATE ------------------
    @Override
    public UserManagementListMemberPojo updateMemberById(Long id, UserManagementListMemberPojo member) {
        logger.debug("updateMemberById called with id={}", id);
        logger.debug("updateMemberById member={}", member);
        mapper.updateMemberById(member);
        logger.debug("updateMemberById updated member={}", member);
        return member;
    }

    @Override
    public UserManagementListMemberPojo updateMemberByUserName(String userName, UserManagementListMemberPojo member) {
        logger.debug("updateMemberByUserName called with userName={}", userName);
        logger.debug("updateMemberByUserName member={}", member);
        mapper.updateMemberByUserName(member);
        logger.debug("updateMemberByUserName updated member={}", member);
        return member;
    }

    // ------------------ DELETE ------------------
    @Override
    public void deleteMemberById(Long id) {
        logger.debug("deleteMemberById called with id={}", id);
        mapper.deleteMemberById(id);
        logger.debug("deleteMemberById completed for id={}", id);
    }

    @Override
    public void deleteMemberByUserName(String userName) {
        logger.debug("deleteMemberByUserName called with userName={}", userName);
        mapper.deleteMemberByUserName(userName);
        logger.debug("deleteMemberByUserName completed for userName={}", userName);
    }

    @Override
    public void deleteMembersByListId(Long listId) {
        logger.debug("deleteMembersByListId called with listId={}", listId);
        mapper.deleteMembersByListId(listId);
        logger.debug("deleteMembersByListId completed for listId={}", listId);
    }
}
