package simple.chatgpt.service.management;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
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
            logger.debug("Storage directory created at relativePath={} absolutePath={}", storageDir, storageDir.toAbsolutePath());
        } else {
            logger.debug("Storage directory exists at relativePath={} absolutePath={}", storageDir, storageDir.toAbsolutePath());
        }

        this.uploadColumns = UploadConfigLoader.getColumns(MEMBER_GRID_ID);
        this.downloadColumns = DownloadConfigLoader.getColumns(MEMBER_GRID_ID);
    }

    // ------------------ SEARCH / LIST ------------------
    @Override
    public PagedResult<UserManagementListPojo> searchUserLists(Map<String, String> params) {
        logger.debug("searchUserLists called with params={}", params);

        int page = 0;
        int size = 20;
        try {
            page = Integer.parseInt(params.getOrDefault("page", "0"));
        } catch (NumberFormatException e) {
            logger.warn("Invalid page parameter: {}, defaulting to 0", params.get("page"), e);
        }
        try {
            size = Integer.parseInt(params.getOrDefault("size", "20"));
        } catch (NumberFormatException e) {
            logger.warn("Invalid size parameter: {}, defaulting to 20", params.get("size"), e);
        }
        int offset = page * size;

        String sortField = params.getOrDefault("sortField", "id");
        String sortDirection = params.getOrDefault("sortDirection", "ASC").toUpperCase();

        // Copy params into Map<String, Object> for MyBatis
        Map<String, Object> sqlParams = new HashMap<>();
        sqlParams.putAll(params);
        sqlParams.put("offset", offset);
        sqlParams.put("limit", size);
        sqlParams.put("sortField", sortField);
        sqlParams.put("sortDirection", sortDirection);

        List<UserManagementListPojo> items = new ArrayList<>();
        long totalCount = 0;

        try {
            items = listMapper.searchUserLists(sqlParams, offset, size, sortField, sortDirection);
            totalCount = listMapper.countLists(sqlParams);
            logger.debug("searchUserLists items={}", items);
            logger.debug("searchUserLists totalCount={}", totalCount);
        } catch (Exception e) {
            logger.error("Error executing searchUserLists query with params={}", sqlParams, e);
            throw new RuntimeException("Database error during searchUserLists", e);
        }

        return new PagedResult<>(items, totalCount, page, size);
    }

    // ------------------ CRUD ------------------
    @Override
    public void createList(UserManagementListPojo list, List<UserManagementListMemberPojo> members) {
        logger.debug("createList list={}", list);
        logger.debug("createList members={}", members);

        listMapper.insertList(list);
        Long listId = list.getId();
        logger.debug("createList generated listId={}", listId);

        if (members != null && !members.isEmpty()) {
            for (UserManagementListMemberPojo m : members) {
                m.setListId(listId);
                logger.debug("createList member listId set: member={}", m);
            }
            memberMapper.batchInsertMembers(members);
        }
    }

    @Override
    public void deleteList(Long listId) {
        logger.debug("deleteList listId={}", listId);
        memberMapper.deleteMembersByListId(listId);
        listMapper.deleteList(listId);
        logger.debug("deleteList completed for listId={}", listId);
    }

    @Override
    public UserManagementListPojo getListById(Long listId) {
        logger.debug("getListById listId={}", listId);
        UserManagementListPojo list = listMapper.findListById(listId);
        logger.debug("getListById result={}", list);
        return list;
    }

    @Override
    public List<UserManagementListMemberPojo> getMembersByListId(Long listId) {
        logger.debug("getMembersByListId listId={}", listId);
        List<UserManagementListMemberPojo> members = memberMapper.findMembersByListId(listId);
        logger.debug("getMembersByListId members={}", members);
        return members;
    }

    // ------------------ MEMBER SEARCH ------------------
    @Override
    public List<UserManagementListMemberPojo> searchMembers(Map<String, Object> params) {
        logger.debug("searchMembers params={}", params);
        List<UserManagementListMemberPojo> members = memberMapper.findMembers(params);
        logger.debug("searchMembers members={}", members);
        return members;
    }

    @Override
    public long countMembers(Map<String, Object> params) {
        logger.debug("countMembers params={}", params);
        long count = memberMapper.countMembers(params);
        logger.debug("countMembers count={}", count);
        return count;
    }

    // ------------------ FILE STORAGE ------------------
    private Path getListFilePath(Long listId, String originalFileName) {
        String extension = "";
        int dotIndex = originalFileName.lastIndexOf('.');
        if (dotIndex >= 0) extension = originalFileName.substring(dotIndex);

        Path path = storageDir.resolve("list_" + listId + extension);
        logger.debug("getListFilePath relativePath={}", path);
        logger.debug("getListFilePath absolutePath={}", path.toAbsolutePath());
        return path;
    }

    // ------------------ Reflection Helpers ------------------
    private String getFieldValue(UserManagementListMemberPojo member, String property) {
        try {
            String methodName = "get" + property.substring(0, 1).toUpperCase() + property.substring(1);
            Method method = UserManagementListMemberPojo.class.getMethod(methodName);
            Object value = method.invoke(member);
            return value != null ? value.toString() : "";
        } catch (Exception e) {
            logger.warn("Failed to get field '{}': {}", property, e.getMessage());
            return "";
        }
    }

    private void setFieldValue(UserManagementListMemberPojo member, String property, String value) {
        try {
            String methodName = "set" + property.substring(0, 1).toUpperCase() + property.substring(1);
            Method method = UserManagementListMemberPojo.class.getMethod(methodName, String.class);
            method.invoke(member, value);
        } catch (Exception e) {
            logger.warn("Failed to set field '{}': {}", property, e.getMessage());
        }
    }

    // ------------------ CSV Import/Export ------------------
    @Override
    public void importListFromCsv(InputStream inputStream, UserManagementListPojo list, String originalFileName) throws Exception {
        logger.debug("importListFromCsv list={}", list);
        logger.debug("importListFromCsv originalFileName={}", originalFileName);

        byte[] bytes = inputStream.readAllBytes(); // copy input stream

        List<UserManagementListMemberPojo> members = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new InputStreamReader(new java.io.ByteArrayInputStream(bytes)))) {
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

        createList(list, members);

        Path path = getListFilePath(list.getId(), originalFileName);
        try (OutputStream os = Files.newOutputStream(path)) {
            os.write(bytes);
        }
        list.setFilePath(path.toString());
    }

    @Override
    public void exportListToCsv(Long listId, OutputStream outputStream) throws Exception {
        logger.debug("exportListToCsv listId={}", listId);

        List<UserManagementListMemberPojo> members = getMembersByListId(listId);
        try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(outputStream))) {
            String[] header = downloadColumns.stream().map(ColumnConfig::getDbField).toArray(String[]::new);
            writer.writeNext(header);

            for (UserManagementListMemberPojo m : members) {
                String[] row = downloadColumns.stream().map(c -> getFieldValue(m, c.getName())).toArray(String[]::new);
                writer.writeNext(row);
            }
        }
    }

    // ------------------ Excel Import/Export ------------------
    @Override
    public void importListFromExcel(InputStream inputStream, UserManagementListPojo list, String originalFileName) throws Exception {
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

        createList(list, members);

        Path path = getListFilePath(list.getId(), originalFileName);
        try (OutputStream os = Files.newOutputStream(path)) {
            os.write(bytes);
        }
        list.setFilePath(path.toString());
    }

    @Override
    public void exportListToExcel(Long listId, OutputStream outputStream) throws Exception {
        logger.debug("exportListToExcel listId={}", listId);
        List<UserManagementListMemberPojo> members = getMembersByListId(listId);

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
}
