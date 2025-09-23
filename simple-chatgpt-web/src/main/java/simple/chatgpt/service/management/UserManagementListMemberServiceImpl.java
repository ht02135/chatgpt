package simple.chatgpt.service.management;

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

    // 🔎 SEARCH / LIST
    @Override
    public List<UserManagementListMemberPojo> searchMembers(Map<String, Object> params) {
        logger.debug("searchMembers called with params={}", params);
        return mapper.findMembers(params);
    }

    @Override
    public long countMembers(Map<String, Object> params) {
        logger.debug("countMembers called with params={}", params);
        return mapper.countMembers(params);
    }

    // 📖 READ
    @Override
    public UserManagementListMemberPojo getMemberById(Long id) {
        logger.debug("getMemberById called with id={}", id);
        return mapper.getMemberById(id);
    }

    @Override
    public UserManagementListMemberPojo getMemberByUserName(String userName) {
        logger.debug("getMemberByUserName called with userName={}", userName);
        return mapper.getMemberByUserName(userName);
    }

    // ➕ CREATE
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

    // ✏️ UPDATE
    @Override
    public UserManagementListMemberPojo updateMemberById(Long id, UserManagementListMemberPojo member) {
        logger.debug("updateMemberById called with id={}, member={}", id, member);
        mapper.updateMemberById(member);
        logger.debug("updateMemberById updated member={}", member);
        return member;
    }

    @Override
    public UserManagementListMemberPojo updateMemberByUserName(String userName, UserManagementListMemberPojo member) {
        logger.debug("updateMemberByUserName called with userName={}, member={}", userName, member);
        mapper.updateMemberByUserName(member);
        logger.debug("updateMemberByUserName updated member={}", member);
        return member;
    }

    // 🗑 DELETE
    @Override
    public void deleteMemberById(Long id) {
        logger.debug("deleteMemberById called with id={}", id);
        mapper.deleteMemberById(id);
    }

    @Override
    public void deleteMemberByUserName(String userName) {
        logger.debug("deleteMemberByUserName called with userName={}", userName);
        mapper.deleteMemberByUserName(userName);
    }

    @Override
    public void deleteMembersByListId(Long listId) {
        logger.debug("deleteMembersByListId called with listId={}", listId);
        mapper.deleteMembersByListId(listId);
    }
}
