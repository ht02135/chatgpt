package simple.chatgpt.service.management;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
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

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import simple.chatgpt.config.management.ColumnConfig;
import simple.chatgpt.config.management.loader.DownloadConfigLoader;
import simple.chatgpt.config.management.loader.UploadConfigLoader;
import simple.chatgpt.pojo.management.UserManagementListMemberPojo;
import simple.chatgpt.pojo.management.UserManagementListPojo;
import simple.chatgpt.util.PagedResult;
import simple.chatgpt.util.ParamWrapper;

/*
hung: make sure you do not remove any of my comments. 
If you dare remove hung comment, you are in big trouble.
*/

public class UserListFileServiceImpl implements UserListFileService {

    private static final Logger logger = LogManager.getLogger(UserListFileServiceImpl.class);

    private final Path storageDir;
    private final List<ColumnConfig> uploadColumns;
    private final List<ColumnConfig> downloadColumns;
    private final UserManagementListMemberService memberService;

    public UserListFileServiceImpl(
            UserManagementListMemberService memberService,
            DownloadConfigLoader downloadConfigLoader,
            UploadConfigLoader uploadConfigLoader,
            Path storageDir,
            String memberGridId) {

        logger.debug("UserListFileServiceImpl constructor called");
        logger.debug("UserListFileServiceImpl memberService={}", memberService);
        logger.debug("UserListFileServiceImpl downloadConfigLoader={}", downloadConfigLoader);
        logger.debug("UserListFileServiceImpl uploadConfigLoader={}", uploadConfigLoader);
        logger.debug("UserListFileServiceImpl storageDir={}", storageDir);
        logger.debug("UserListFileServiceImpl memberGridId={}", memberGridId);

        this.memberService = memberService;
        this.storageDir = storageDir;
        this.uploadColumns = uploadConfigLoader.getColumns(memberGridId);
        this.downloadColumns = downloadConfigLoader.getColumns(memberGridId);
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
        logger.debug("importListFromCsv inputStream={}", inputStream);

        UserManagementListPojo list = ParamWrapper.unwrap(params, "list");
        logger.debug("importListFromCsv list={}", list);

        String originalFileName = ParamWrapper.unwrap(params, "originalFileName");
        logger.debug("importListFromCsv originalFileName={}", originalFileName);

        Path path = getListFilePath(list.getId(), originalFileName);
        logger.debug("importListFromCsv path={}", path);

        list.setFilePath(path.toString());
        byte[] bytes = inputStream.readAllBytes();
        try (OutputStream os = Files.newOutputStream(path)) {
            os.write(bytes);
        }

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
        logger.debug("importListFromExcel inputStream={}", inputStream);

        UserManagementListPojo list = ParamWrapper.unwrap(params, "list");
        logger.debug("importListFromExcel list={}", list);

        String originalFileName = ParamWrapper.unwrap(params, "originalFileName");
        logger.debug("importListFromExcel originalFileName={}", originalFileName);

        byte[] bytes = inputStream.readAllBytes();
        logger.debug("importListFromExcel bytes length={}", bytes.length);

        List<UserManagementListMemberPojo> members = new ArrayList<>();

        try (Workbook workbook = originalFileName.endsWith(".xls") ?
                new HSSFWorkbook(new java.io.ByteArrayInputStream(bytes)) :
                new XSSFWorkbook(new java.io.ByteArrayInputStream(bytes))) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            if (rows.hasNext()) rows.next(); // skip header row

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
