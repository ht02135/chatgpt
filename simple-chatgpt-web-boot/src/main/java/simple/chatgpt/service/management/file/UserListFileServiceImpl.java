package simple.chatgpt.service.management.file;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import simple.chatgpt.config.management.ColumnConfig;
import simple.chatgpt.config.management.loader.DownloadConfigLoader;
import simple.chatgpt.config.management.loader.UploadConfigLoader;
import simple.chatgpt.pojo.management.UserManagementListMemberPojo;
import simple.chatgpt.pojo.management.UserManagementListPojo;
import simple.chatgpt.service.management.UserManagementListMemberService;
import simple.chatgpt.util.PagedResult;
import simple.chatgpt.util.ParamWrapper;

/*
hung: make sure you do not remove any of my comments. 
If you dare remove hung comment, you are in big trouble.
*/

@Service
public class UserListFileServiceImpl implements UserListFileService {

    private static final Logger logger = LogManager.getLogger(UserListFileServiceImpl.class);

    private static final String MEMBER_GRID_ID = "userListMembers";
    
    private final Path storageDir;
    private final List<ColumnConfig> uploadColumns;
    private final List<ColumnConfig> downloadColumns;
    private final UserManagementListMemberService memberService;

    private final CsvFileService csvFileService;
    private final ExcelFileService excelFileService;

    public UserListFileServiceImpl(
            UserManagementListMemberService memberService,
            DownloadConfigLoader downloadConfigLoader,
            UploadConfigLoader uploadConfigLoader,
            CsvFileService csvFileService,
            ExcelFileService excelFileService) {

        logger.debug("UserListFileServiceImpl constructor called");
        logger.debug("UserListFileServiceImpl memberService={}", memberService);
        logger.debug("UserListFileServiceImpl downloadConfigLoader={}", downloadConfigLoader);
        logger.debug("UserListFileServiceImpl uploadConfigLoader={}", uploadConfigLoader);
        logger.debug("UserListFileServiceImpl csvFileService={}", csvFileService);
        logger.debug("UserListFileServiceImpl excelFileService={}", excelFileService);

        this.memberService = memberService;
        this.csvFileService = csvFileService;
        this.excelFileService = excelFileService;
        this.uploadColumns = uploadConfigLoader.getColumns(MEMBER_GRID_ID);
        this.downloadColumns = downloadConfigLoader.getColumns(MEMBER_GRID_ID);

        // ================================
        // FIX: Use WEB-INF/classes/data for storage
        // ================================
        String contextPath = System.getProperty("context.path", "/chatgpt-production"); // default prod
        String profileFolder = contextPath.startsWith("/chatgpt-production") ? "chatgpt-production" : "chatgpt-dev";
        
        String webappClasses = System.getProperty("catalina.base") + "/webapps/" + profileFolder + "/WEB-INF/classes";
        Path storagePath = Paths.get(webappClasses, "data", "management", "user_lists");

        try {
            if (!Files.exists(storagePath)) {
                Files.createDirectories(storagePath);
                logger.debug("Created storageDir at {}", storagePath.toAbsolutePath());
            }
        } catch (Exception e) {
            logger.error("Failed to create storageDir", e);
        }

        this.storageDir = storagePath;
        logger.debug("Using storageDir={}", this.storageDir.toAbsolutePath());
    }

    // ==============================================================
    // ================ CSV ==========================================
    // ==============================================================

    @Override
    public void importListFromCsv(Map<String, Object> params) throws Exception {
        logger.debug("importListFromCsv START");
        logger.debug("importListFromCsv params={}", params);

        InputStream inputStream = ParamWrapper.unwrap(params, "inputStream");
        logger.debug("importListFromCsv inputStream={}", inputStream);

        UserManagementListPojo list = ParamWrapper.unwrap(params, "list");
        logger.debug("importListFromCsv list={}", list);

        String originalFileName = ParamWrapper.unwrap(params, "originalFileName");
        logger.debug("importListFromCsv originalFileName={}", originalFileName);

        Path path = getListFilePath(list.getId(), originalFileName);
        logger.debug("importListFromCsv path={}", path);

        byte[] bytes = inputStream.readAllBytes();
        try (OutputStream os = Files.newOutputStream(path)) {
            os.write(bytes);
        }
        list.setFilePath(path.toString());

        List<List<String>> rows = csvFileService.readCsv(new java.io.ByteArrayInputStream(bytes));
        logger.debug("importListFromCsv total rows read={}", rows.size());

        List<UserManagementListMemberPojo> members = new ArrayList<>();
        Iterator<List<String>> iterator = rows.iterator();
        if (iterator.hasNext()) iterator.next(); // skip header

        while (iterator.hasNext()) {
            List<String> row = iterator.next();
            UserManagementListMemberPojo member = new UserManagementListMemberPojo();
            for (int i = 0; i < uploadColumns.size() && i < row.size(); i++) {
                setFieldValue(member, uploadColumns.get(i).getName(), row.get(i));
            }
            members.add(member);
        }

        logger.debug("importListFromCsv DONE for listId={}", list.getId());
    }

    @Override
    public void exportListToCsv(Map<String, Object> params) throws Exception {
        logger.debug("exportListToCsv START");
        logger.debug("exportListToCsv params={}", params);

        Long listId = ParamWrapper.unwrap(params, "listId");
        logger.debug("exportListToCsv listId={}", listId);

        OutputStream outputStream = ParamWrapper.unwrap(params, "outputStream");
        logger.debug("exportListToCsv outputStream={}", outputStream);

        PagedResult<UserManagementListMemberPojo> result = getMembersByListId(listId);
        List<UserManagementListMemberPojo> members = result.getItems();

        List<String> headers = new ArrayList<>();
        for (ColumnConfig c : downloadColumns) {
            headers.add(c.getDbField());
        }

        List<List<String>> rows = new ArrayList<>();
        for (UserManagementListMemberPojo m : members) {
            List<String> row = new ArrayList<>();
            for (ColumnConfig c : downloadColumns) {
                row.add(getFieldValue(m, c.getName()));
            }
            rows.add(row);
        }

        csvFileService.writeCsv(headers, rows, outputStream);

        logger.debug("exportListToCsv DONE for listId={}", listId);
    }

    // ==============================================================
    // ================ EXCEL ========================================
    // ==============================================================

    @Override
    public void importListFromExcel(Map<String, Object> params) throws Exception {
        logger.debug("importListFromExcel START");
        logger.debug("importListFromExcel params={}", params);

        InputStream inputStream = ParamWrapper.unwrap(params, "inputStream");
        logger.debug("importListFromExcel inputStream={}", inputStream);

        UserManagementListPojo list = ParamWrapper.unwrap(params, "list");
        logger.debug("importListFromExcel list={}", list);

        String originalFileName = ParamWrapper.unwrap(params, "originalFileName");
        logger.debug("importListFromExcel originalFileName={}", originalFileName);

        byte[] bytes = inputStream.readAllBytes();
        logger.debug("importListFromExcel bytes length={}", bytes.length);

        List<List<String>> rows = excelFileService.readExcel(new java.io.ByteArrayInputStream(bytes), originalFileName);
        logger.debug("importListFromExcel total rows read={}", rows.size());

        List<UserManagementListMemberPojo> members = new ArrayList<>();
        Iterator<List<String>> iterator = rows.iterator();
        if (iterator.hasNext()) iterator.next(); // skip header

        while (iterator.hasNext()) {
            List<String> row = iterator.next();
            UserManagementListMemberPojo member = new UserManagementListMemberPojo();
            for (int i = 0; i < uploadColumns.size() && i < row.size(); i++) {
                setFieldValue(member, uploadColumns.get(i).getName(), row.get(i));
            }
            members.add(member);
        }

        Path path = getListFilePath(list.getId(), originalFileName);
        try (OutputStream os = Files.newOutputStream(path)) {
            os.write(bytes);
        }
        list.setFilePath(path.toString());

        logger.debug("importListFromExcel DONE for listId={}", list.getId());
    }

    @Override
    public void exportListToExcel(Map<String, Object> params) throws Exception {
        logger.debug("exportListToExcel START");
        logger.debug("exportListToExcel params={}", params);

        Long listId = ParamWrapper.unwrap(params, "listId");
        logger.debug("exportListToExcel listId={}", listId);

        OutputStream outputStream = ParamWrapper.unwrap(params, "outputStream");
        logger.debug("exportListToExcel outputStream={}", outputStream);

        PagedResult<UserManagementListMemberPojo> result = getMembersByListId(listId);
        List<UserManagementListMemberPojo> members = result.getItems();

        List<String> headers = new ArrayList<>();
        for (ColumnConfig c : downloadColumns) {
            headers.add(c.getDbField());
        }

        List<List<String>> rows = new ArrayList<>();
        for (UserManagementListMemberPojo m : members) {
            List<String> row = new ArrayList<>();
            for (ColumnConfig c : downloadColumns) {
                row.add(getFieldValue(m, c.getName()));
            }
            rows.add(row);
        }

        excelFileService.writeExcel(headers, rows, outputStream);

        logger.debug("exportListToExcel DONE for listId={}", listId);
    }

    // ==============================================================
    // ================ SUPPORT METHODS ==============================
    // ==============================================================

    private Path getListFilePath(Long listId, String originalFileName) {
        logger.debug("getListFilePath START");
        logger.debug("getListFilePath listId={}", listId);
        logger.debug("getListFilePath originalFileName={}", originalFileName);

        String extension = "";
        int dot = originalFileName.lastIndexOf('.');
        if (dot >= 0) extension = originalFileName.substring(dot);
        Path path = storageDir.resolve("list_" + listId + extension);

        logger.debug("getListFilePath DONE");
        return path;
    }

    private String getFieldValue(UserManagementListMemberPojo member, String property) {
        logger.debug("getFieldValue START");
        logger.debug("getFieldValue member={}", member);
        logger.debug("getFieldValue property={}", property);

        try {
            String mName = "get" + property.substring(0, 1).toUpperCase() + property.substring(1);
            Method m = UserManagementListMemberPojo.class.getMethod(mName);
            Object val = m.invoke(member);
            logger.debug("getFieldValue DONE");
            return val != null ? val.toString() : "";
        } catch (Exception e) {
            logger.warn("Failed getFieldValue '{}': {}", property, e.getMessage());
            logger.debug("getFieldValue DONE with exception");
            return "";
        }
    }

    private void setFieldValue(UserManagementListMemberPojo member, String property, String value) {
        logger.debug("setFieldValue START");
        logger.debug("setFieldValue member={}", member);
        logger.debug("setFieldValue property={}", property);
        logger.debug("setFieldValue value={}", value);

        try {
            String mName = "set" + property.substring(0, 1).toUpperCase() + property.substring(1);
            Method m = UserManagementListMemberPojo.class.getMethod(mName, String.class);
            m.invoke(member, value);
        } catch (Exception e) {
            logger.warn("Failed setFieldValue '{}': {}", property, e.getMessage());
        }

        logger.debug("setFieldValue DONE");
    }

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
}
