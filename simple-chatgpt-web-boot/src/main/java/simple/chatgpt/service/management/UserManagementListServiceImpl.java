package simple.chatgpt.service.management;

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
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import simple.chatgpt.config.management.ColumnConfig;
import simple.chatgpt.config.management.loader.DownloadConfigLoader;
import simple.chatgpt.config.management.loader.UploadConfigLoader;
import simple.chatgpt.mapper.management.UserManagementListMapper;
import simple.chatgpt.pojo.management.UserManagementListMemberPojo;
import simple.chatgpt.pojo.management.UserManagementListPojo;
import simple.chatgpt.util.PagedResult;
import simple.chatgpt.util.ParamWrapper;
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

    public UserManagementListServiceImpl(UserManagementListMapper listMapper,
                                         UserManagementListMemberService memberService,
                                         DownloadConfigLoader downloadConfigLoader,
                                         UploadConfigLoader uploadConfigLoader) throws Exception {
        logger.debug("UserManagementListServiceImpl START");
        logger.debug("UserManagementListServiceImpl listMapper={}", listMapper);
        logger.debug("UserManagementListServiceImpl memberService={}", memberService);
        logger.debug("UserManagementListServiceImpl downloadConfigLoader={}", downloadConfigLoader);
        logger.debug("UserManagementListServiceImpl uploadConfigLoader={}", uploadConfigLoader);

        this.listMapper = listMapper;
        this.memberService = memberService;
        this.downloadConfigLoader = downloadConfigLoader;
        this.uploadConfigLoader = uploadConfigLoader;

        // ================================
        // FIX: Use deployed WAR folder dynamically
        // ================================
        String webappsDir = System.getProperty("catalina.base") + "/webapps";
        String warName = System.getProperty("war.name", "chatgpt-production"); // provide default
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
    // ================ 5 CORE METHODS (on top) =====================
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

        // force uppercase for sortDirection
        params.put("sortDirection", params.get("sortDirection").toUpperCase());

        logger.debug("search final params={}", params);

        // Hung : mapper expect Map<String, Object> for offset and limit
    	Map<String, Object> mapperParams = new HashMap<>(params);
        mapperParams.put("offset", SafeConverter.toIntOrDefault(params.get("offset"), 0));
        mapperParams.put("limit", SafeConverter.toIntOrDefault(params.get("limit"), 10));
        
        List<UserManagementListPojo> items = listMapper.search(mapperParams);
        long totalCount = items.size(); // ideally from count query

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
    // ================ OTHER METHODS ===============================
    // ==============================================================
    
    // ------------------ CSV/Excel ------------------
    @Override
    public void importListFromCsv(Map<String, Object> params) throws Exception {
        logger.debug("importListFromCsv START");
        logger.debug("importListFromCsv params={}", params);

        InputStream inputStream = ParamWrapper.unwrap(params, "inputStream");
        UserManagementListPojo list = ParamWrapper.unwrap(params, "list");
        String originalFileName = ParamWrapper.unwrap(params, "originalFileName");

        Path path = getListFilePath(list.getId(), originalFileName);
        list.setFilePath(path.toString());
        byte[] bytes = inputStream.readAllBytes();
        try (OutputStream os = Files.newOutputStream(path)) {
            os.write(bytes);
        }

        List<UserManagementListMemberPojo> members = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new java.io.InputStreamReader(new java.io.ByteArrayInputStream(bytes)))) {
            reader.readNext();
            String[] row;
            while ((row = reader.readNext()) != null) {
                UserManagementListMemberPojo member = new UserManagementListMemberPojo();
                for (int i = 0; i < uploadColumns.size() && i < row.length; i++) {
                    setFieldValue(member, uploadColumns.get(i).getName(), row[i]);
                }
                members.add(member);
            }
        }

        create(list);
        logger.debug("importListFromCsv DONE for listId={}", list.getId());
    }

    @Override
    public void exportListToCsv(Map<String, Object> params) throws Exception {
        logger.debug("exportListToCsv START");
        logger.debug("exportListToCsv params={}", params);

        Long listId = ParamWrapper.unwrap(params, "listId");
        OutputStream outputStream = ParamWrapper.unwrap(params, "outputStream");

        PagedResult<UserManagementListMemberPojo> result = getMembersByListId(listId);
        List<UserManagementListMemberPojo> members = result.getItems();

        try (CSVWriter writer = new CSVWriter(new java.io.OutputStreamWriter(outputStream))) {
            String[] header = downloadColumns.stream().map(ColumnConfig::getDbField).toArray(String[]::new);
            writer.writeNext(header);

            for (UserManagementListMemberPojo m : members) {
                String[] row = downloadColumns.stream()
                        .map(c -> getFieldValue(m, c.getName()))
                        .toArray(String[]::new);
                writer.writeNext(row);
            }
        }

        logger.debug("exportListToCsv DONE for listId={}", listId);
    }

    @Override
    public void importListFromExcel(Map<String, Object> params) throws Exception {
        logger.debug("importListFromExcel START");
        logger.debug("importListFromExcel params={}", params);

        InputStream inputStream = ParamWrapper.unwrap(params, "inputStream");
        UserManagementListPojo list = ParamWrapper.unwrap(params, "list");
        String originalFileName = ParamWrapper.unwrap(params, "originalFileName");

        byte[] bytes = inputStream.readAllBytes();
        List<UserManagementListMemberPojo> members = new ArrayList<>();

        try (Workbook workbook = originalFileName.endsWith(".xls") ?
                new HSSFWorkbook(new java.io.ByteArrayInputStream(bytes)) :
                new XSSFWorkbook(new java.io.ByteArrayInputStream(bytes))) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            if (rows.hasNext()) rows.next();

            while (rows.hasNext()) {
                Row row = rows.next();
                UserManagementListMemberPojo member = new UserManagementListMemberPojo();
                for (int i = 0; i < uploadColumns.size(); i++) {
                    if (row.getCell(i) != null)
                        setFieldValue(member, uploadColumns.get(i).getName(), row.getCell(i).toString());
                }
                members.add(member);
            }
        }

        create(list);
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
        OutputStream outputStream = ParamWrapper.unwrap(params, "outputStream");

        PagedResult<UserManagementListMemberPojo> result = getMembersByListId(listId);
        List<UserManagementListMemberPojo> members = result.getItems();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Users");

            Row header = sheet.createRow(0);
            for (int i = 0; i < downloadColumns.size(); i++) {
                header.createCell(i).setCellValue(downloadColumns.get(i).getDbField());
            }

            int rowIdx = 1;
            for (UserManagementListMemberPojo m : members) {
                Row row = sheet.createRow(rowIdx++);
                for (int i = 0; i < downloadColumns.size(); i++) {
                    row.createCell(i).setCellValue(getFieldValue(m, downloadColumns.get(i).getName()));
                }
            }

            workbook.write(outputStream);
        }

        logger.debug("exportListToExcel DONE for listId={}", listId);
    }

    // ------------------ Helpers ------------------
    
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
    
    @Override
    public List<UserManagementListPojo> getAll() {
        logger.debug("getAll called");

        // Reuse search mapper with empty params to get everything
        Map<String, Object> params = new HashMap<>();
        // No offset/limit => all rows
        List<UserManagementListPojo> userLists = listMapper.search(params);
        
        return userLists;
    }
    
}
