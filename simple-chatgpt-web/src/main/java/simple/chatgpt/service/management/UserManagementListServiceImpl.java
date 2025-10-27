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

import simple.chatgpt.config.ColumnConfig;
import simple.chatgpt.config.management.loader.DownloadConfigLoader;
import simple.chatgpt.config.management.loader.UploadConfigLoader;
import simple.chatgpt.mapper.management.UserManagementListMapper;
import simple.chatgpt.mapper.management.UserManagementListMemberMapper;
import simple.chatgpt.pojo.management.UserManagementListMemberPojo;
import simple.chatgpt.pojo.management.UserManagementListPojo;
import simple.chatgpt.util.PagedResult;

@Service
public class UserManagementListServiceImpl implements UserManagementListService {

    private static final Logger logger = LogManager.getLogger(UserManagementListServiceImpl.class);
    private static final String MEMBER_GRID_ID = "userListMembers";

    private final DownloadConfigLoader downloadConfigLoader; // constructor-injected
    private final UploadConfigLoader uploadConfigLoader; // constructor-injected
    private final UserManagementListMapper listMapper;
    private final UserManagementListMemberMapper memberMapper;
    private final Path storageDir;
    private final List<ColumnConfig> uploadColumns;
    private final List<ColumnConfig> downloadColumns;

    public UserManagementListServiceImpl(UserManagementListMapper listMapper,
                                         UserManagementListMemberMapper memberMapper,
                                         DownloadConfigLoader downloadConfigLoader,
                                         UploadConfigLoader uploadConfigLoader) throws Exception {
        this.listMapper = listMapper;
        this.memberMapper = memberMapper;
        this.downloadConfigLoader = downloadConfigLoader; // injected here
        this.uploadConfigLoader = uploadConfigLoader; // injected here

        String webappRoot = System.getProperty("catalina.base") + "/webapps/chatgpt";
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
        this.downloadColumns = this.downloadConfigLoader.getColumns(MEMBER_GRID_ID); // NPE fixed
    }

    // ------------------ SEARCH ------------------
    @Override
    public PagedResult<UserManagementListPojo> searchUserLists(Map<String, Object> params) {
        logger.debug("searchUserLists called with params={}", params);

        int page = 0, size = 20;
        try { page = getInt(params.getOrDefault("page", "0")); }
        catch (Exception e) { logger.warn("Invalid page param {}, defaulting to 0", params.get("page"), e); }
        try { size = getInt(params.getOrDefault("size", "20")); }
        catch (Exception e) { logger.warn("Invalid size param {}, defaulting to 20", params.get("size"), e); }

        int offset = page * size;
        String sortField = (String) params.getOrDefault("sortField", "id");
        String sortDirection = ((String) params.getOrDefault("sortDirection", "ASC")).toUpperCase();

        Map<String, Object> sqlParams = new HashMap<>(params);
        sqlParams.put("offset", offset);
        sqlParams.put("limit", size);
        sqlParams.put("sortField", sortField);
        sqlParams.put("sortDirection", sortDirection);

        List<UserManagementListPojo> items = new ArrayList<>();
        long totalCount = 0;
        try {
            items = listMapper.findLists(sqlParams);
            totalCount = listMapper.countLists(sqlParams);
            logger.debug("searchUserLists items={}", items);
            logger.debug("searchUserLists totalCount={}", totalCount);
        } catch (Exception e) {
            logger.error("Error executing searchUserLists with params={}", sqlParams, e);
            throw new RuntimeException(e);
        }

        return new PagedResult<>(items, totalCount, page, size);
    }

    // ------------------ CRUD ------------------
    @Override
    public void createList(Map<String, Object> params) {
        logger.debug("createList called with params={}", params);

        UserManagementListPojo list = (UserManagementListPojo) params.get("list");
        List<UserManagementListMemberPojo> members = (List<UserManagementListMemberPojo>) params.get("members");

        logger.debug("createList list={}", list);
        logger.debug("createList members={}", members);

        Map<String, Object> listParam = new HashMap<>();
        listParam.put("list", list);
        logger.debug("createList -> listMapper.insertList(listParam): listParam={}", listParam);
        listMapper.insertList(listParam);

        Long listId = list.getId();
        logger.debug("createList #############");
        logger.debug("createList generated listId={}", listId);
        logger.debug("createList #############");

        if (members != null && !members.isEmpty()) {
            for (UserManagementListMemberPojo m : members) {
                m.setListId(listId);
                logger.debug("createList member listId set: member={}", m);
            }
            Map<String, Object> memberParam = new HashMap<>();
            memberParam.put("members", members);
            logger.debug("createList -> memberMapper.batchInsertMembers(memberParam): members={}", members);
            memberMapper.batchInsertMembers(memberParam);
        }
        
        logger.debug("createList #############");
        logger.debug("createList DONE!!!");
        logger.debug("createList #############");
    }

    @Override
    public void deleteList(Map<String, Object> params) {
        logger.debug("deleteList called with params={}", params);

        Long listId = (Long) params.get("listId");
        logger.debug("deleteList listId={}", listId);

        // Delete members first
        Map<String, Object> memberParam = new HashMap<>();
        memberParam.put("listId", listId);
        memberMapper.deleteMembersByListId(memberParam);
        logger.debug("deleteList deleted members for listId={}", listId);

        // Delete list
        Map<String, Object> listParam = new HashMap<>();
        listParam.put("listId", listId);
        listMapper.deleteList(listParam);
        logger.debug("deleteList deleted list for listId={}", listId);
    }

    @Override
    public void updateList(Map<String, Object> params) {
        logger.debug("updateList called with params={}", params);

        UserManagementListPojo list = (UserManagementListPojo) params.get("list");
        List<UserManagementListMemberPojo> members = (List<UserManagementListMemberPojo>) params.get("members");

        logger.debug("updateList list={}", list);
        logger.debug("updateList members={}", members);

        Map<String, Object> listParam = new HashMap<>();
        listParam.put("listId", list.getId());
        listParam.put("userListName", list.getUserListName());
        listParam.put("filePath", list.getFilePath());
        listParam.put("originalFileName", list.getOriginalFileName());
        listParam.put("description", list.getDescription());

        listMapper.updateList(listParam);
        logger.debug("updateList list updated for listId={}", list.getId());

        Long listId = list.getId();
        if (members != null && !members.isEmpty()) {
            Map<String, Object> deleteParam = new HashMap<>();
            deleteParam.put("listId", listId);
            memberMapper.deleteMembersByListId(deleteParam);
            logger.debug("updateList existing members deleted for listId={}", listId);

            for (UserManagementListMemberPojo m : members) {
                m.setListId(listId);
                logger.debug("updateList member listId set: member={}", m);
            }
            Map<String, Object> insertParam = new HashMap<>();
            insertParam.put("members", members);
            memberMapper.batchInsertMembers(insertParam);
            logger.debug("updateList new members inserted for listId={}", listId);
        }
    }

    @Override
    public UserManagementListPojo getListById(Map<String, Object> params) {
        logger.debug("getListById called with params={}", params);
        Long listId = (Long) params.get("listId");
        logger.debug("getListById listId={}", listId);

        Map<String, Object> sqlParams = new HashMap<>();
        sqlParams.put("listId", listId);

        logger.debug("getListById #############");
        logger.debug("getListById sqlParams={}", sqlParams);
        logger.debug("getListById #############");
        UserManagementListPojo list = listMapper.findListById(sqlParams);
        logger.debug("getListById #############");
        logger.debug("getListById result={}", list);
        logger.debug("getListById DONE!!!");
        logger.debug("getListById #############");
        return list;
    }

    // ------------------ MEMBER SEARCH/COUNT ------------------
    @Override
    public PagedResult<UserManagementListMemberPojo> getMembersByListId(Map<String, Object> params) {
        logger.debug("getMembersByListId called with params={}", params);
        Long listId = (Long) params.get("listId");
        logger.debug("getMembersByListId listId={}", listId);

        int page = 0, size = 20;
        try { page = getInt(params.getOrDefault("page", "0")); }
        catch (Exception e) { logger.warn("Invalid page param {}, defaulting to 0", params.get("page"), e); }
        try { size = getInt(params.getOrDefault("size", "20")); }
        catch (Exception e) { logger.warn("Invalid size param {}, defaulting to 20", params.get("size"), e); }

        int offset = page * size;

        Map<String, Object> sqlParams = new HashMap<>();
        sqlParams.put("listId", listId);
        sqlParams.put("offset", offset);
        sqlParams.put("limit", size);

        for (Map.Entry<String, Object> entry : sqlParams.entrySet()) {
            logger.debug("getMembersByListId param {}={}", entry.getKey(), entry.getValue());
        }

        List<UserManagementListMemberPojo> members = memberMapper.findMembersByListId(sqlParams);
        long total = memberMapper.countMembers(sqlParams);

        logger.debug("getMembersByListId result size={} total={}", members.size(), total);
        return new PagedResult<>(members, total, page, size);
    }

    @Override
    public PagedResult<UserManagementListMemberPojo> searchMembers(Map<String, Object> params) {
        logger.debug("searchMembers called with params={}", params);

        int page = 0, size = 20;
        try { page = getInt(params.getOrDefault("page", "0")); }
        catch (Exception e) { logger.warn("Invalid page param {}, defaulting to 0", params.get("page"), e); }
        try { size = getInt(params.getOrDefault("size", "20")); }
        catch (Exception e) { logger.warn("Invalid size param {}, defaulting to 20", params.get("size"), e); }

        int offset = page * size;
        String sortField = (String) params.getOrDefault("sortField", "id");
        String sortDirection = ((String) params.getOrDefault("sortDirection", "ASC")).toUpperCase();

        Map<String, Object> sqlParams = new HashMap<>(params);
        sqlParams.put("offset", offset);
        sqlParams.put("limit", size);
        sqlParams.put("sortField", sortField);
        sqlParams.put("sortDirection", sortDirection);

        for (Map.Entry<String, Object> entry : sqlParams.entrySet()) {
            logger.debug("searchMembers param {}={}", entry.getKey(), entry.getValue());
        }

        List<UserManagementListMemberPojo> members = memberMapper.findMembers(sqlParams);
        long total = memberMapper.countMembers(sqlParams); // fixed: use same filtered params

        logger.debug("searchMembers result size={} total={}", members.size(), total);
        return new PagedResult<>(members, total, page, size);
    }

    @Override
    public long countMembers(Map<String, Object> params) {
        logger.debug("countMembers called with params={}", params);

        Map<String, Object> sqlParams = new HashMap<>(params);
        for (Map.Entry<String, Object> entry : sqlParams.entrySet()) {
            logger.debug("countMembers param {}={}", entry.getKey(), entry.getValue());
        }

        long count = memberMapper.countMembers(sqlParams);
        logger.debug("countMembers result={}", count);
        return count;
    }

    // ------------------ CSV/Excel ------------------
    @Override
    public void importListFromCsv(Map<String, Object> params) throws Exception {
        logger.debug("importListFromCsv called with params={}", params);

        InputStream inputStream = (InputStream) params.get("inputStream");
        
        UserManagementListPojo list = (UserManagementListPojo) params.get("list");
        String originalFileName = (String) params.get("originalFileName");
        logger.debug("importListFromCsv list={}", list);
        logger.debug("importListFromCsv originalFileName={}", originalFileName);
        
        Path path = getListFilePath(list.getId(), originalFileName);
        list.setFilePath(path.toString());
        logger.debug("importListFromCsv path={}", path);
        logger.debug("importListFromCsv list={}", list);

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

        Map<String, Object> createParams = new HashMap<>();
        createParams.put("list", list);
        createParams.put("members", members);
        logger.debug("importListFromCsv #############");
        logger.debug("importListFromCsv createParams={}", createParams);
        logger.debug("importListFromCsv #############");
        createList(createParams);

        logger.debug("importListFromCsv #############");
        logger.debug("importListFromCsv DONE!!!");
        logger.debug("importListFromCsv #############");
    }

    @Override
    public void exportListToCsv(Map<String, Object> params) throws Exception {
        logger.debug("exportListToCsv called with params={}", params);

        Long listId = (Long) params.get("listId");
        OutputStream outputStream = (OutputStream) params.get("outputStream");

        logger.debug("exportListToCsv listId={}", listId);
        logger.debug("exportListToCsv outputStream={}", outputStream);

        Map<String, Object> pagingParams = new HashMap<>(params);
        pagingParams.put("page", 0);
        pagingParams.put("size", Integer.MAX_VALUE);

        PagedResult<UserManagementListMemberPojo> result = getMembersByListId(pagingParams);
        List<UserManagementListMemberPojo> members = result.getItems();
        logger.debug("exportListToCsv members count={}", members.size());

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
    }

    @Override
    public void importListFromExcel(Map<String, Object> params) throws Exception {
        logger.debug("importListFromExcel called with params={}", params);

        InputStream inputStream = (InputStream) params.get("inputStream");
        UserManagementListPojo list = (UserManagementListPojo) params.get("list");
        String originalFileName = (String) params.get("originalFileName");

        logger.debug("importListFromExcel list={}", list);
        logger.debug("importListFromExcel originalFileName={}", originalFileName);

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

        Map<String, Object> createParams = new HashMap<>();
        createParams.put("list", list);
        createParams.put("members", members);
        createList(createParams);

        Path path = getListFilePath(list.getId(), originalFileName);
        try (OutputStream os = Files.newOutputStream(path)) {
            os.write(bytes);
        }
        list.setFilePath(path.toString());
    }

    @Override
    public void exportListToExcel(Map<String, Object> params) throws Exception {
        logger.debug("exportListToExcel called with params={}", params);

        Long listId = (Long) params.get("listId");
        OutputStream outputStream = (OutputStream) params.get("outputStream");

        logger.debug("exportListToExcel listId={}", listId);
        logger.debug("exportListToExcel outputStream={}", outputStream);

        Map<String, Object> pagingParams = new HashMap<>(params);
        pagingParams.put("page", 0);
        pagingParams.put("size", Integer.MAX_VALUE);

        PagedResult<UserManagementListMemberPojo> result = getMembersByListId(pagingParams);
        List<UserManagementListMemberPojo> members = result.getItems();
        logger.debug("exportListToExcel members count={}", members.size());

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
    }

    // ------------------ Helpers ------------------
    private Path getListFilePath(Long listId, String originalFileName) {
        String extension = "";
        int dot = originalFileName.lastIndexOf('.');
        if (dot >= 0) extension = originalFileName.substring(dot);
        Path path = storageDir.resolve("list_" + listId + extension);
        logger.debug("getListFilePath relativePath={}", path);
        logger.debug("getListFilePath absolutePath={}", path.toAbsolutePath());
        return path;
    }

    private String getFieldValue(UserManagementListMemberPojo member, String property) {
        try {
            String mName = "get" + property.substring(0, 1).toUpperCase() + property.substring(1);
            Method m = UserManagementListMemberPojo.class.getMethod(mName);
            Object val = m.invoke(member);
            return val != null ? val.toString() : "";
        } catch (Exception e) {
            logger.warn("Failed getFieldValue '{}': {}", property, e.getMessage());
            return "";
        }
    }

    private void setFieldValue(UserManagementListMemberPojo member, String property, String value) {
        try {
            String mName = "set" + property.substring(0, 1).toUpperCase() + property.substring(1);
            Method m = UserManagementListMemberPojo.class.getMethod(mName, String.class);
            m.invoke(member, value);
        } catch (Exception e) {
            logger.warn("Failed setFieldValue '{}': {}", property, e.getMessage());
        }
    }
    
    private int getInt(Object object) {
        if (object == null) {
            return 0; // Treat null as 0
        }

        if (object instanceof Number) {
            return ((Number) object).intValue();
        } else if (object instanceof String) {
            String s = (String) object;
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                // Log the warning using the assumed logger
                logger.warn("Invalid integer value: '{}', defaulting to 0", s, e);
                return 0;
            }
        }

        // Handle any other unexpected Object type by trying to parse its string representation
        // or by simply returning 0. Given the original logic, returning 0 is safer.
        return 0;
    }
}
