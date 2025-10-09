package simple.chatgpt.service.management;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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
import simple.chatgpt.mapper.management.UserManagementListMemberMapper;
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
    private final UserManagementListMemberMapper memberMapper;
    private final Path storageDir;
    private final List<ColumnConfig> uploadColumns;
    private final List<ColumnConfig> downloadColumns;

    public UserManagementListServiceImpl(UserManagementListMapper listMapper,
                                         UserManagementListMemberMapper memberMapper,
                                         DownloadConfigLoader downloadConfigLoader,
                                         UploadConfigLoader uploadConfigLoader) throws Exception {
        logger.debug("UserManagementListServiceImpl START");
        logger.debug("UserManagementListServiceImpl listMapper={}", listMapper);
        logger.debug("UserManagementListServiceImpl memberMapper={}", memberMapper);
        logger.debug("UserManagementListServiceImpl downloadConfigLoader={}", downloadConfigLoader);
        logger.debug("UserManagementListServiceImpl uploadConfigLoader={}", uploadConfigLoader);

        this.listMapper = listMapper;
        this.memberMapper = memberMapper;
        this.downloadConfigLoader = downloadConfigLoader;
        this.uploadConfigLoader = uploadConfigLoader;

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

    // ------------------ SEARCH ------------------
    @Override
    public PagedResult<UserManagementListPojo> searchUserLists(Map<String, Object> params) {
        logger.debug("searchUserLists START");
        logger.debug("searchUserLists params={}", params);

        int page = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "page", 0), 0);
        int size = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "size", 20), 20);
        int offset = page * size;

        if (!params.containsKey("sortField")) params.put("sortField", "id");
        if (!params.containsKey("sortDirection")) params.put("sortDirection", "ASC");

        params.put("offset", offset);
        params.put("limit", size);

        List<UserManagementListPojo> items = listMapper.findLists(params);
        long totalCount = listMapper.countLists(params);

        PagedResult<UserManagementListPojo> result = new PagedResult<>(items, totalCount, page, size);
        logger.debug("searchUserLists return={}", result);
        return result;
    }

    // ------------------ CRUD ------------------
    @Override
    public void createList(Map<String, Object> params) {
        logger.debug("createList START");
        logger.debug("createList params={}", params);

        UserManagementListPojo list = ParamWrapper.unwrap(params, "list");
        List<UserManagementListMemberPojo> members = ParamWrapper.unwrap(params, "members");

        logger.debug("createList list={}", list);
        logger.debug("createList members={}", members);

        listMapper.insertList(params);

        Long listId = list.getId();
        logger.debug("createList generated listId={}", listId);

        if (members != null && !members.isEmpty()) {
            for (UserManagementListMemberPojo m : members) {
                m.setListId(listId);
                logger.debug("createList member listId set: member={}", m);
            }
            params.put("members", members);
            memberMapper.batchInsertMembers(params);
        }

        logger.debug("createList DONE");
    }

    @Override
    public void deleteList(Map<String, Object> params) {
        logger.debug("deleteList START");
        logger.debug("deleteList params={}", params);

        Long listId = ParamWrapper.unwrap(params, "listId");
        logger.debug("deleteList listId={}", listId);

        params.put("listId", listId);
        memberMapper.deleteMembersByListId(params);
        listMapper.deleteList(params);

        logger.debug("deleteList DONE for listId={}", listId);
    }

    @Override
    public void updateList(Map<String, Object> params) {
        logger.debug("updateList START");
        logger.debug("updateList params={}", params);

        UserManagementListPojo list = ParamWrapper.unwrap(params, "list");
        List<UserManagementListMemberPojo> members = ParamWrapper.unwrap(params, "members");

        logger.debug("updateList list={}", list);
        logger.debug("updateList members={}", members);

        listMapper.updateList(params);

        Long listId = list.getId();
        if (members != null && !members.isEmpty()) {
            params.put("listId", listId);
            memberMapper.deleteMembersByListId(params);

            for (UserManagementListMemberPojo m : members) {
                m.setListId(listId);
                logger.debug("updateList member listId set: member={}", m);
            }

            params.put("members", members);
            memberMapper.batchInsertMembers(params);
        }

        logger.debug("updateList DONE for listId={}", listId);
    }

    @Override
    public UserManagementListPojo getListById(Map<String, Object> params) {
        logger.debug("getListById START");
        logger.debug("getListById params={}", params);

        UserManagementListPojo list = listMapper.findListById(params);
        logger.debug("getListById return={}", list);
        return list;
    }

    @Override
    public PagedResult<UserManagementListMemberPojo> getMembersByListId(Map<String, Object> params) {
        logger.debug("getMembersByListId START");
        logger.debug("getMembersByListId params={}", params);

        int page = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "page", 0), 0);
        int size = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "size", 20), 20);
        int offset = page * size;

        params.put("offset", offset);
        params.put("limit", size);

        List<UserManagementListMemberPojo> members = memberMapper.findMembersByListId(params);
        long total = memberMapper.countMembers(params);

        PagedResult<UserManagementListMemberPojo> result = new PagedResult<>(members, total, page, size);
        logger.debug("getMembersByListId return size={} total={}", members.size(), total);
        return result;
    }

    @Override
    public PagedResult<UserManagementListMemberPojo> searchMembers(Map<String, Object> params) {
        logger.debug("searchMembers START");
        logger.debug("searchMembers params={}", params);

        int page = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "page", 0), 0);
        int size = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "size", 20), 20);
        int offset = page * size;

        if (!params.containsKey("sortField")) params.put("sortField", "id");
        if (!params.containsKey("sortDirection")) params.put("sortDirection", "ASC");

        params.put("offset", offset);
        params.put("limit", size);

        List<UserManagementListMemberPojo> members = memberMapper.findMembers(params);
        long total = memberMapper.countMembers(params);

        PagedResult<UserManagementListMemberPojo> result = new PagedResult<>(members, total, page, size);
        logger.debug("searchMembers return size={} total={}", members.size(), total);
        return result;
    }

    @Override
    public long countMembers(Map<String, Object> params) {
        logger.debug("countMembers START");
        logger.debug("countMembers params={}", params);

        long count = memberMapper.countMembers(params);
        logger.debug("countMembers return={}", count);
        return count;
    }

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

        params.put("list", list);
        params.put("members", members);
        createList(params);

        logger.debug("importListFromCsv DONE for listId={}", list.getId());
    }

    @Override
    public void exportListToCsv(Map<String, Object> params) throws Exception {
        logger.debug("exportListToCsv START");
        logger.debug("exportListToCsv params={}", params);

        params.put("page", 0);
        params.put("size", Integer.MAX_VALUE);
        PagedResult<UserManagementListMemberPojo> result = getMembersByListId(params);
        List<UserManagementListMemberPojo> members = result.getItems();

        try (CSVWriter writer = new CSVWriter(new java.io.OutputStreamWriter(ParamWrapper.unwrap(params, "outputStream")))) {
            String[] header = downloadColumns.stream().map(ColumnConfig::getDbField).toArray(String[]::new);
            writer.writeNext(header);

            for (UserManagementListMemberPojo m : members) {
                String[] row = downloadColumns.stream()
                        .map(c -> getFieldValue(m, c.getName()))
                        .toArray(String[]::new);
                writer.writeNext(row);
            }
        }

        logger.debug("exportListToCsv DONE");
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

        params.put("list", list);
        params.put("members", members);
        createList(params);

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

        params.put("page", 0);
        params.put("size", Integer.MAX_VALUE);
        PagedResult<UserManagementListMemberPojo> result = getMembersByListId(params);
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

            workbook.write(ParamWrapper.unwrap(params, "outputStream"));
        }

        logger.debug("exportListToExcel DONE");
    }

    // ------------------ Helpers ------------------
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
}
