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
import simple.chatgpt.util.SafeConverter;

@Service
public class UserManagementListMemberServiceImpl implements UserManagementListMemberService {

    private static final Logger logger = LogManager.getLogger(UserManagementListMemberServiceImpl.class);

    private final UserManagementListMemberMapper memberMapper;

    public UserManagementListMemberServiceImpl(UserManagementListMemberMapper memberMapper) {
        logger.debug("UserManagementListMemberServiceImpl constructor called");
        this.memberMapper = memberMapper;
    }

    // ==============================================================
    // ================ 5 CORE METHODS (on top) =====================
    // ==============================================================

    @Override
    public UserManagementListMemberPojo create(UserManagementListMemberPojo member) {
        logger.debug("create called");
        logger.debug("create member={}", member);
        memberMapper.create(member);
        return member;
    }

    @Override
    public UserManagementListMemberPojo update(Long id, UserManagementListMemberPojo member) {
        logger.debug("update called");
        logger.debug("update id={}", id);
        logger.debug("update member={}", member);
        memberMapper.update(id, member);
        return member;
    }

    @Override
    public PagedResult<UserManagementListMemberPojo> search(Map<String, String> params) {
        logger.debug("search called");
        logger.debug("search params={}", params);

        if (!params.containsKey("page")) params.put("page", "0");
        if (!params.containsKey("size")) params.put("size", "20");
        int page = SafeConverter.toIntOrDefault(params.get("page"), 0);
        int size = SafeConverter.toIntOrDefault(params.get("size"), 20);
        int offset = page * size;

        if (!params.containsKey("offset")) params.put("offset", String.valueOf(offset));
        if (!params.containsKey("limit")) params.put("limit", String.valueOf(size));
        if (!params.containsKey("sortField")) params.put("sortField", "id");
        if (!params.containsKey("sortDirection")) params.put("sortDirection", "ASC");
        params.put("sortDirection", params.get("sortDirection").toUpperCase());

        // Hung : mapper expect Map<String, Object> for offset and limit
    	Map<String, Object> mapperParams = new HashMap<>(params);
        mapperParams.put("offset", SafeConverter.toIntOrDefault(params.get("offset"), 0));
        mapperParams.put("limit", SafeConverter.toIntOrDefault(params.get("limit"), 10));
        
        List<UserManagementListMemberPojo> items = memberMapper.search(mapperParams);
        long totalCount = items.size();
        PagedResult<UserManagementListMemberPojo> result = new PagedResult<>(items, totalCount, page, size);
        logger.debug("search return={}", result);
        return result;
    }

    @Override
    public UserManagementListMemberPojo get(Long id) {
        logger.debug("get called");
        logger.debug("get id={}", id);
        UserManagementListMemberPojo member = memberMapper.get(id);
        logger.debug("get return={}", member);
        return member;
    }

    @Override
    public void delete(Long id) {
        logger.debug("delete called");
        logger.debug("delete id={}", id);
        memberMapper.delete(id);
    }

    // ======= OTHER METHODS =======
    
    @Override
    public List<UserManagementListMemberPojo> getMembersByParams(Map<String, Object> params)
    {
        logger.debug("getMembersByParams called");

        List<UserManagementListMemberPojo> memebers = memberMapper.search(params);
        return memebers;
    }
    
    // mapper uses #{params.listId}
    @Override
    public List<UserManagementListMemberPojo> getMembersByListId(Long listId) {
        logger.debug("getMembersByListId called");

        // Reuse search mapper with empty params to get everything
        Map<String, Object> params = new HashMap<>();
        params.put("listId", listId); 
        List<UserManagementListMemberPojo> memebers = getMembersByParams(params);
        
        return memebers;
    }
    
    @Override
    public List<UserManagementListMemberPojo> getAll() {
        logger.debug("getAll called");

        // Reuse search mapper with empty params to get everything
        Map<String, Object> params = new HashMap<>();
        // No offset/limit => all rows
        List<UserManagementListMemberPojo> memebers = getMembersByParams(params);
        
        return memebers;
    }
    
}
