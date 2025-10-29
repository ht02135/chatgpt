package simple.chatgpt.service.management;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import simple.chatgpt.config.management.loader.DownloadConfigLoader;
import simple.chatgpt.config.management.loader.UploadConfigLoader;
import simple.chatgpt.mapper.management.UserManagementListMapper;
import simple.chatgpt.pojo.management.UserManagementListPojo;
import simple.chatgpt.util.PagedResult;
import simple.chatgpt.util.SafeConverter;

@Service
public class UserManagementListServiceImpl implements UserManagementListService {

    private static final Logger logger = LogManager.getLogger(UserManagementListServiceImpl.class);
    private static final String MEMBER_GRID_ID = "userListMembers";

    private final DownloadConfigLoader downloadConfigLoader;
    private final UploadConfigLoader uploadConfigLoader;
    private final UserManagementListMapper listMapper;

    @Autowired
    public UserManagementListServiceImpl(UserManagementListMapper listMapper,
                                         DownloadConfigLoader downloadConfigLoader,
                                         UploadConfigLoader uploadConfigLoader) {

        logger.debug("UserManagementListServiceImpl constructor called");
        logger.debug("listMapper={}", listMapper);
        logger.debug("downloadConfigLoader={}", downloadConfigLoader);
        logger.debug("uploadConfigLoader={}", uploadConfigLoader);

        this.listMapper = listMapper;
        this.downloadConfigLoader = downloadConfigLoader;
        this.uploadConfigLoader = uploadConfigLoader;

        logger.debug("UserManagementListServiceImpl DONE");
    }

    // ==============================================================
    // ================ 5 CORE METHODS ==============================
    // ==============================================================

    @Override
    public UserManagementListPojo create(UserManagementListPojo list) {
        logger.debug("create called");
        logger.debug("create list={}", list);
        listMapper.create(list);
        return list;
    }

    @Override
    public UserManagementListPojo update(Long id, UserManagementListPojo list) {
        logger.debug("update called");
        logger.debug("update id={}", id);
        logger.debug("update list={}", list);
        listMapper.update(id, list);
        return list;
    }

    @Override
    public PagedResult<UserManagementListPojo> search(Map<String, String> params) {
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

        Map<String, Object> mapperParams = new HashMap<>(params);
        mapperParams.put("offset", SafeConverter.toIntOrDefault(params.get("offset"), 0));
        mapperParams.put("limit", SafeConverter.toIntOrDefault(params.get("limit"), 10));

        List<UserManagementListPojo> items = listMapper.search(mapperParams);
        long totalCount = items.size();

        PagedResult<UserManagementListPojo> result = new PagedResult<>(items, totalCount, page, size);
        logger.debug("search result={}", result);
        return result;
    }

    @Override
    public UserManagementListPojo get(Long id) {
        logger.debug("get called");
        logger.debug("get id={}", id);
        return listMapper.get(id);
    }

    @Override
    public void delete(Long id) {
        logger.debug("delete called");
        logger.debug("delete id={}", id);
        listMapper.delete(id);
    }

    // ==============================================================
    // ================ SUPPORT METHODS ==============================
    // ==============================================================

    @Override
    public List<UserManagementListPojo> getAll() {
        logger.debug("getAll called");
        Map<String, Object> params = new HashMap<>();
        List<UserManagementListPojo> userLists = listMapper.search(params);
        return userLists;
    }
}
