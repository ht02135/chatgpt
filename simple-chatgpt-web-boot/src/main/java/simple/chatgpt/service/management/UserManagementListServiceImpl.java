package simple.chatgpt.service.management;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import simple.chatgpt.config.management.ColumnConfig;
import simple.chatgpt.config.management.loader.DownloadConfigLoader;
import simple.chatgpt.config.management.loader.UploadConfigLoader;
import simple.chatgpt.mapper.management.UserManagementListMapper;
import simple.chatgpt.pojo.management.UserManagementListMemberPojo;
import simple.chatgpt.pojo.management.UserManagementListPojo;
import simple.chatgpt.service.management.file.UserListFileServiceImpl;
import simple.chatgpt.util.PagedResult;
import simple.chatgpt.util.SafeConverter;


@Service
public class UserManagementListServiceImpl implements UserManagementListService {

    private static final Logger logger = LogManager.getLogger(UserManagementListServiceImpl.class);
    private static final String MEMBER_GRID_ID = "userListMembers";

    private final DownloadConfigLoader downloadConfigLoader;
    private final UploadConfigLoader uploadConfigLoader;
    private final UserManagementListMapper listMapper;
    private final UserManagementListMemberService memberService;
    private final Path storageDir;
    private final List<ColumnConfig> uploadColumns;
    private final List<ColumnConfig> downloadColumns;

    /*
    hung: inject UserListFileServiceImpl for file handling
    */
    private final UserListFileServiceImpl userListFileService;

    @Autowired
    public UserManagementListServiceImpl(UserManagementListMapper listMapper,
                                         UserManagementListMemberService memberService,
                                         DownloadConfigLoader downloadConfigLoader,
                                         UploadConfigLoader uploadConfigLoader,
                                         UserListFileServiceImpl userListFileService) throws Exception {

        logger.debug("UserManagementListServiceImpl constructor called");
        logger.debug("UserManagementListServiceImpl listMapper={}", listMapper);
        logger.debug("UserManagementListServiceImpl memberService={}", memberService);
        logger.debug("UserManagementListServiceImpl downloadConfigLoader={}", downloadConfigLoader);
        logger.debug("UserManagementListServiceImpl uploadConfigLoader={}", uploadConfigLoader);
        logger.debug("UserManagementListServiceImpl userListFileService={}", userListFileService);

        this.listMapper = listMapper;
        this.memberService = memberService;
        this.downloadConfigLoader = downloadConfigLoader;
        this.uploadConfigLoader = uploadConfigLoader;
        this.userListFileService = userListFileService;

        // ================================
        // FIX: Use deployed WAR folder dynamically
        // ================================
        String webappsDir = System.getProperty("catalina.base") + "/webapps";
        String warName = System.getProperty("war.name", "chatgpt-production");
        String webappRoot = webappsDir + "/" + warName;

        storageDir = Paths.get(webappRoot, "data/management/user_lists");

        if (!Files.exists(storageDir)) {
            Files.createDirectories(storageDir);
            logger.debug("Storage directory created at relativePath={}", storageDir);
            logger.debug("Storage directory created at absolutePath={}", storageDir.toAbsolutePath());
        } else {
            logger.debug("Storage directory exists at relativePath={}", storageDir);
            logger.debug("Storage directory exists at absolutePath={}", storageDir.toAbsolutePath());
        }

        this.uploadColumns = this.uploadConfigLoader.getColumns(MEMBER_GRID_ID);
        this.downloadColumns = this.downloadConfigLoader.getColumns(MEMBER_GRID_ID);
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
    // ================ FILE IMPORT/EXPORT ==========================
    // ==============================================================

    @Override
    public void importListFromCsv(Map<String, Object> params) throws Exception {
        logger.debug("importListFromCsv START");
        logger.debug("importListFromCsv params={}", params);
        userListFileService.importListFromCsv(params);
        logger.debug("importListFromCsv rerouted to userListFileService DONE");
    }

    @Override
    public void exportListToCsv(Map<String, Object> params) throws Exception {
        logger.debug("exportListToCsv START");
        logger.debug("exportListToCsv params={}", params);
        userListFileService.exportListToCsv(params);
        logger.debug("exportListToCsv rerouted to userListFileService DONE");
    }

    @Override
    public void importListFromExcel(Map<String, Object> params) throws Exception {
        logger.debug("importListFromExcel START");
        logger.debug("importListFromExcel params={}", params);
        userListFileService.importListFromExcel(params);
        logger.debug("importListFromExcel rerouted to userListFileService DONE");
    }

    @Override
    public void exportListToExcel(Map<String, Object> params) throws Exception {
        logger.debug("exportListToExcel START");
        logger.debug("exportListToExcel params={}", params);
        userListFileService.exportListToExcel(params);
        logger.debug("exportListToExcel rerouted to userListFileService DONE");
    }

    // ==============================================================
    // ================ SUPPORT METHODS ==============================
    // ==============================================================

    private PagedResult<UserManagementListMemberPojo> getMembersByListId(Long listId) {
        logger.debug("getMembersByListId START");
        logger.debug("getMembersByListId listId={}", listId);

        int page = 0;
        int size = Integer.MAX_VALUE;
        int offset = page * size;

        Map<String, Object> mapperParams = new HashMap<>();
        mapperParams.put("listId", listId);
        mapperParams.put("offset", offset);
        mapperParams.put("limit", size);
        List<UserManagementListMemberPojo> members = memberService.getMembersByParams(mapperParams);
        long total = members.size();

        logger.debug("getMembersByListId result size={} total={}", members.size(), total);
        return new PagedResult<>(members, total, page, size);
    }

    @Override
    public List<UserManagementListPojo> getAll() {
        logger.debug("getAll called");
        Map<String, Object> params = new HashMap<>();
        List<UserManagementListPojo> userLists = listMapper.search(params);
        return userLists;
    }
}
