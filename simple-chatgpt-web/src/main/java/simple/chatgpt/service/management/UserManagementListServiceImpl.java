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
import simple.chatgpt.download.management.loader.DownloadConfigLoader;
import simple.chatgpt.mapper.management.UserManagementListMapper;
import simple.chatgpt.mapper.management.UserManagementListMemberMapper;
import simple.chatgpt.pojo.management.UserManagementListMemberPojo;
import simple.chatgpt.pojo.management.UserManagementListPojo;
import simple.chatgpt.upload.management.loader.UploadConfigLoader;
import simple.chatgpt.util.PagedResult;

@Service
public class UserManagementListServiceImpl implements UserManagementListService {

    private static final Logger logger = LogManager.getLogger(UserManagementListServiceImpl.class);
    private static final String MEMBER_GRID_ID = "userListMembers";

    private final UserManagementListMapper listMapper;
    private final UserManagementListMemberMapper memberMapper;
    private final Path storageDir;
    private final List<ColumnConfig> uploadColumns;
    private final List<ColumnConfig> downloadColumns;

    public UserManagementListServiceImpl(UserManagementListMapper listMapper,
                                         UserManagementListMemberMapper memberMapper) throws Exception {
        this.listMapper = listMapper;
        this.memberMapper = memberMapper;

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

        this.uploadColumns = UploadConfigLoader.getColumns(MEMBER_GRID_ID);
        this.downloadColumns = DownloadConfigLoader.getColumns(MEMBER_GRID_ID);
    }

    // ------------------ SEARCH ------------------
    @Override
    public PagedResult<UserManagementListPojo> searchUserLists(Map<String, Object> params) {
        logger.debug("searchUserLists called");
        Map<String, Object> innerParams = getParamsMap(params);

        for (Map.Entry<String, Object> entry : innerParams.entrySet()) {
            logger.debug("searchUserLists param {}={}", entry.getKey(), entry.getValue());
        }

        int page = 0, size = 20;
        try { page = Integer.parseInt(innerParams.getOrDefault("page", "0").toString()); }
        catch (Exception e) { logger.warn("Invalid page param, defaulting to 0", e); }
        try { size = Integer.parseInt(innerParams.getOrDefault("size", "20").toString()); }
        catch (Exception e) { logger.warn("Invalid size param, defaulting to 20", e); }

        int offset = page * size;
        innerParams.put("offset", offset);
        innerParams.put("limit", size);
        innerParams.put("sortField", innerParams.getOrDefault("sortField", "id"));
        innerParams.put("sortDirection", ((String) innerParams.getOrDefault("sortDirection", "ASC")).toUpperCase());

        Map<String, Object> wrapperParam = new HashMap<>();
        wrapperParam.put("params", innerParams);

        List<UserManagementListPojo> items;
        long totalCount;
        try {
            items = listMapper.findLists(wrapperParam);
            totalCount = listMapper.countLists(wrapperParam);
            logger.debug("searchUserLists items={}", items);
            logger.debug("searchUserLists totalCount={}", totalCount);
        } catch (Exception e) {
            logger.error("Error executing searchUserLists", e);
            throw new RuntimeException(e);
        }

        return new PagedResult<>(items, totalCount, page, size);
    }

    // ------------------ CRUD ------------------
    @Override
    public void createList(Map<String, Object> params) {
        logger.debug("createList called");
        Map<String, Object> innerParams = getParamsMap(params);

        UserManagementListPojo list = (UserManagementListPojo) innerParams.get("list");
        List<UserManagementListMemberPojo> members = (List<UserManagementListMemberPojo>) innerParams.get("members");

        logger.debug("createList list={}", list);
        logger.debug("createList members={}", members);

        Map<String, Object> listParam = new HashMap<>();
        listParam.put("list", list);

        Map<String, Object> wrapperParam = new HashMap<>();
        wrapperParam.put("params", listParam);

        logger.debug("createList wrapperParam={}", wrapperParam);

        listMapper.insertList(wrapperParam);
        Long listId = list.getId();
        logger.debug("createList generated listId={}", listId);

        if (members != null && !members.isEmpty()) {
            for (UserManagementListMemberPojo m : members) {
                m.setListId(listId);
                logger.debug("createList member listId set: member={}", m);
            }

            Map<String, Object> memberParam = new HashMap<>();
            memberParam.put("member", members);
            Map<String, Object> memberWrapper = new HashMap<>();
            memberWrapper.put("params", memberParam);

            logger.debug("createList memberWrapper={}", memberWrapper);

            memberMapper.batchInsertMembers(memberWrapper);
        }
    }

    @Override
    public void deleteList(Map<String, Object> params) {
        logger.debug("deleteList called");
        Map<String, Object> innerParams = getParamsMap(params);

        Long listId = (Long) innerParams.get("listId");
        logger.debug("deleteList listId={}", listId);

        Map<String, Object> memberParam = new HashMap<>();
        memberParam.put("listId", listId);
        Map<String, Object> memberWrapper = new HashMap<>();
        memberWrapper.put("params", memberParam);

        memberMapper.deleteMembersByListId(memberWrapper);
        logger.debug("deleteList deleted members for listId={}", listId);

        Map<String, Object> listParam = new HashMap<>();
        listParam.put("listId", listId);
        Map<String, Object> listWrapper = new HashMap<>();
        listWrapper.put("params", listParam);

        listMapper.deleteList(listWrapper);
        logger.debug("deleteList deleted list for listId={}", listId);
    }    
    
    @Override
    public void updateList(Map<String, Object> params) {
        logger.debug("updateList called");
        Map<String, Object> innerParams = getParamsMap(params);

        UserManagementListPojo list = (UserManagementListPojo) innerParams.get("list");
        List<UserManagementListMemberPojo> members = (List<UserManagementListMemberPojo>) innerParams.get("members");

        logger.debug("updateList list={}", list);
        logger.debug("updateList members={}", members);

        Map<String, Object> listParam = new HashMap<>();
        listParam.put("listId", list.getId());
        listParam.put("userListName", list.getUserListName());
        listParam.put("filePath", list.getFilePath());
        listParam.put("originalFileName", list.getOriginalFileName());
        listParam.put("description", list.getDescription());

        Map<String, Object> wrapperParam = new HashMap<>();
        wrapperParam.put("params", listParam);

        listMapper.updateList(wrapperParam);
        logger.debug("updateList list updated for listId={}", list.getId());

        Long listId = list.getId();
        if (members != null && !members.isEmpty()) {
            for (UserManagementListMemberPojo m : members) {
                m.setListId(listId);
                logger.debug("updateList member listId set: member={}", m);
            }

            Map<String, Object> memberParam = new HashMap<>();
            memberParam.put("member", members);
            Map<String, Object> memberWrapper = new HashMap<>();
            memberWrapper.put("params", memberParam);

            logger.debug("updateList memberWrapper={}", memberWrapper);

            memberMapper.deleteMembersByListId(wrapperParam); // delete old members
            memberMapper.batchInsertMembers(memberWrapper);
            logger.debug("updateList new members inserted for listId={}", listId);
        }
    }

    @Override
    public UserManagementListPojo getListById(Map<String, Object> params) {
        logger.debug("getListById called");
        Map<String, Object> innerParams = getParamsMap(params);

        Long listId = (Long) innerParams.get("listId");
        logger.debug("getListById listId={}", listId);

        Map<String, Object> listParam = new HashMap<>();
        listParam.put("listId", listId);
        Map<String, Object> wrapperParam = new HashMap<>();
        wrapperParam.put("params", listParam);

        UserManagementListPojo list = listMapper.findListById(wrapperParam);
        logger.debug("getListById result={}", list);
        return list;
    }

    @Override
    public PagedResult<UserManagementListMemberPojo> getMembersByListId(Map<String, Object> params) {
        logger.debug("getMembersByListId called");
        Map<String, Object> innerParams = getParamsMap(params);

        for (Map.Entry<String, Object> entry : innerParams.entrySet()) {
            logger.debug("getMembersByListId param {}={}", entry.getKey(), entry.getValue());
        }

        int page = 0;
        int size = 20;
        try {
            if (innerParams.get("page") != null) page = Integer.parseInt(innerParams.get("page").toString());
            if (innerParams.get("size") != null) size = Integer.parseInt(innerParams.get("size").toString());
        } catch (NumberFormatException e) {
            logger.warn("Invalid page or size format, using defaults page=0 size=20", e);
        }

        int offset = page * size;
        innerParams.put("offset", offset);
        innerParams.put("limit", size);

        Map<String, Object> wrapperParam = new HashMap<>();
        wrapperParam.put("params", innerParams);

        List<UserManagementListMemberPojo> members;
        long total = 0;
        try {
            members = memberMapper.findMembersByListId(wrapperParam);
            total = memberMapper.countMembers(wrapperParam);
            logger.debug("getMembersByListId result size={}", members != null ? members.size() : 0);
            logger.debug("getMembersByListId total count={}", total);
        } catch (Exception e) {
            logger.error("Error executing getMembersByListId", e);
            throw new RuntimeException("Database error during getMembersByListId", e);
        }

        return new PagedResult<>(members, total, page, size);
    }

    // ------------------ MEMBER SEARCH/COUNT ------------------
    @Override
    public PagedResult<UserManagementListMemberPojo> searchMembers(Map<String, Object> params) {
        logger.debug("searchMembers called");
        Map<String, Object> innerParams = getParamsMap(params);

        for (Map.Entry<String, Object> entry : innerParams.entrySet()) {
            logger.debug("searchMembers param {}={}", entry.getKey(), entry.getValue());
        }

        int page = 0, size = 20;
        try {
            if (innerParams.get("page") != null) page = Integer.parseInt(innerParams.get("page").toString());
            if (innerParams.get("size") != null) size = Integer.parseInt(innerParams.get("size").toString());
        } catch (NumberFormatException e) {
            logger.warn("Invalid page or size format, using defaults page=0 size=20", e);
        }

        int offset = page * size;
        innerParams.put("offset", offset);
        innerParams.put("limit", size);
        innerParams.put("sortField", innerParams.getOrDefault("sortField", "id"));
        innerParams.put("sortDirection", ((String) innerParams.getOrDefault("sortDirection", "ASC")).toUpperCase());

        Map<String, Object> wrapperParam = new HashMap<>();
        wrapperParam.put("params", innerParams);

        List<UserManagementListMemberPojo> members;
        long total = 0;
        try {
            members = memberMapper.findMembers(wrapperParam);
            total = memberMapper.countMembers(wrapperParam);
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
        Map<String, Object> innerParams = getParamsMap(params);

        Map<String, Object> wrapperParam = new HashMap<>();
        wrapperParam.put("params", innerParams);

        long count = memberMapper.countMembers(wrapperParam);
        logger.debug("countMembers result={}", count);
        return count;
    }

    // ------------------ CSV/Excel ------------------
    @Override
    public void importListFromCsv(Map<String, Object> params) throws Exception {
        logger.debug("importListFromCsv called");
        Map<String, Object> innerParams = getParamsMap(params);

        InputStream inputStream = (InputStream) innerParams.get("inputStream");
        UserManagementListPojo list = (UserManagementListPojo) innerParams.get("list");
        String originalFileName = (String) innerParams.get("originalFileName");

        logger.debug("importListFromCsv list={}", list);
        logger.debug("importListFromCsv originalFileName={}", originalFileName);

        byte[] bytes = inputStream.readAllBytes();
        List<UserManagementListMemberPojo> members = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new java.io.InputStreamReader(new java.io.ByteArrayInputStream(bytes)))) {
            reader.readNext(); // skip header
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

        Map<String, Object> wrapperParam = new HashMap<>();
        wrapperParam.put("params", createParams);

        logger.debug("importListFromCsv wrapperParam={}", wrapperParam);

        createList(wrapperParam);

        Path path = getListFilePath(list.getId(), originalFileName);
        try (OutputStream os = Files.newOutputStream(path)) {
            os.write(bytes);
        }
        list.setFilePath(path.toString());
        logger.debug("importListFromCsv file saved at path={}", path);
    }

    @Override
    public void exportListToCsv(Map<String, Object> params) throws Exception {
        logger.debug("exportListToCsv called");
        Map<String, Object> innerParams = getParamsMap(params);

        Long listId = (Long) innerParams.get("listId");
        OutputStream outputStream = (OutputStream) innerParams.get("outputStream");

        logger.debug("exportListToCsv listId={}", listId);
        logger.debug("exportListToCsv outputStream={}", outputStream);

        Map<String, Object> pagingParams = new HashMap<>();
        pagingParams.put("listId", listId);
        pagingParams.put("page", 0);
        pagingParams.put("size", Integer.MAX_VALUE);

        Map<String, Object> wrapperParam = new HashMap<>();
        wrapperParam.put("params", pagingParams);

        PagedResult<UserManagementListMemberPojo> result = getMembersByListId(wrapperParam);
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
        logger.debug("importListFromExcel called");
        Map<String, Object> innerParams = getParamsMap(params);

        InputStream inputStream = (InputStream) innerParams.get("inputStream");
        UserManagementListPojo list = (UserManagementListPojo) innerParams.get("list");
        String originalFileName = (String) innerParams.get("originalFileName");

        logger.debug("importListFromExcel list={}", list);
        logger.debug("importListFromExcel originalFileName={}", originalFileName);

        byte[] bytes = inputStream.readAllBytes();
        List<UserManagementListMemberPojo> members = new ArrayList<>();

        try (Workbook workbook = originalFileName.endsWith(".xls") ?
                new HSSFWorkbook(new java.io.ByteArrayInputStream(bytes)) :
                new XSSFWorkbook(new java.io.ByteArrayInputStream(bytes))) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            if (rows.hasNext()) rows.next(); // skip header

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

        Map<String, Object> wrapperParam = new HashMap<>();
        wrapperParam.put("params", createParams);

        logger.debug("importListFromExcel wrapperParam={}", wrapperParam);

        createList(wrapperParam);

        Path path = getListFilePath(list.getId(), originalFileName);
        try (OutputStream os = Files.newOutputStream(path)) {
            os.write(bytes);
        }
        list.setFilePath(path.toString());
        logger.debug("importListFromExcel file saved at path={}", path);
    }

    @Override
    public void exportListToExcel(Map<String, Object> params) throws Exception {
        logger.debug("exportListToExcel called");
        Map<String, Object> innerParams = getParamsMap(params);

        Long listId = (Long) innerParams.get("listId");
        OutputStream outputStream = (OutputStream) innerParams.get("outputStream");

        logger.debug("exportListToExcel listId={}", listId);
        logger.debug("exportListToExcel outputStream={}", outputStream);

        Map<String, Object> pagingParams = new HashMap<>();
        pagingParams.put("listId", listId);
        pagingParams.put("page", 0);
        pagingParams.put("size", Integer.MAX_VALUE);

        Map<String, Object> wrapperParam = new HashMap<>();
        wrapperParam.put("params", pagingParams);

        PagedResult<UserManagementListMemberPojo> result = getMembersByListId(wrapperParam);
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
            String methodName = "get" + property.substring(0, 1).toUpperCase() + property.substring(1);
            Method method = UserManagementListMemberPojo.class.getMethod(methodName);
            Object value = method.invoke(member);
            return value != null ? value.toString() : "";
        } catch (Exception e) {
            logger.warn("Failed getFieldValue '{}': {}", property, e.getMessage());
            return "";
        }
    }

    private void setFieldValue(UserManagementListMemberPojo member, String property, String value) {
        try {
            String methodName = "set" + property.substring(0, 1).toUpperCase() + property.substring(1);
            Method method = UserManagementListMemberPojo.class.getMethod(methodName, String.class);
            method.invoke(member, value);
        } catch (Exception e) {
            logger.warn("Failed setFieldValue '{}': {}", property, e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getParamsMap(Map<String, Object> params) {
        if (params == null) {
            params = new HashMap<>();
        }
        Map<String, Object> innerParams = (Map<String, Object>) params.get("params");
        if (innerParams == null) {
            innerParams = new HashMap<>();
            params.put("params", innerParams);
        }
        return innerParams;
    }
}