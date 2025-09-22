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
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import simple.chatgpt.config.ColumnConfig;
import simple.chatgpt.config.GridConfig;
import simple.chatgpt.config.management.ManagementConfigLoader;
import simple.chatgpt.mapper.management.UserManagementListMapper;
import simple.chatgpt.mapper.management.UserManagementListMemberMapper;
import simple.chatgpt.pojo.management.UserManagementListMemberPojo;
import simple.chatgpt.pojo.management.UserManagementListPojo;

@Service
public class UserManagementListServiceImpl implements UserManagementListService {

    private static final Logger logger = LogManager.getLogger(UserManagementListServiceImpl.class);
    private static final String MEMBER_GRID_ID = "user-list-members";

    private final UserManagementListMapper listMapper;
    private final UserManagementListMemberMapper memberMapper;
    private final Path storageDir;
    private final List<ColumnConfig> memberColumns;

    public UserManagementListServiceImpl(UserManagementListMapper listMapper,
                                         UserManagementListMemberMapper memberMapper) throws Exception {
        this.listMapper = listMapper;
        this.memberMapper = memberMapper;

        String webappRoot = System.getProperty("catalina.base") + "/webapps/chatgpt";
        storageDir = Paths.get(webappRoot, "data/management/user_lists");

        if (!Files.exists(storageDir)) {
            Files.createDirectories(storageDir);
            logger.debug("Storage directory created at relativePath={} absolutePath={}", 
                         storageDir, storageDir.toAbsolutePath());
        } else {
            logger.debug("Storage directory exists at relativePath={} absolutePath={}", 
                         storageDir, storageDir.toAbsolutePath());
        }

        ManagementConfigLoader configLoader = new ManagementConfigLoader();
        GridConfig memberGrid = configLoader.loadGrids().stream()
                .filter(g -> MEMBER_GRID_ID.equals(g.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Grid config not found: " + MEMBER_GRID_ID));
        memberColumns = memberGrid.getColumns();
    }

    // ------------------ Core CRUD ------------------

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

    // ------------------ File Storage ------------------

    private Path getListFilePath(Long listId, String originalFileName) {
        String extension = "";
        int dotIndex = originalFileName.lastIndexOf('.');
        if (dotIndex >= 0) extension = originalFileName.substring(dotIndex);

        Path path = storageDir.resolve("list_" + listId + extension);
        logger.debug("getListFilePath relativePath={}", path);
        logger.debug("getListFilePath absolutePath={}", path.toAbsolutePath());
        return path;
    }

    // ------------------ Reflection Helper ------------------

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

        List<UserManagementListMemberPojo> members = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new InputStreamReader(inputStream))) {
            reader.readNext(); // skip header
            String[] row;
            while ((row = reader.readNext()) != null) {
                UserManagementListMemberPojo member = new UserManagementListMemberPojo();
                for (int i = 0; i < memberColumns.size() && i < row.length; i++) {
                    setFieldValue(member, memberColumns.get(i).getName(), row[i]);
                }
                members.add(member);
                logger.debug("importListFromCsv member parsed={}", member);
            }
        }

        createList(list, members);

        Path path = getListFilePath(list.getId(), originalFileName);
        try (OutputStream os = Files.newOutputStream(path)) {
            if (inputStream.markSupported()) inputStream.reset();
            inputStream.transferTo(os);
        }
        list.setFilePath(path.toString());
    }

    @Override
    public void exportListToCsv(Long listId, OutputStream outputStream) throws Exception {
        logger.debug("exportListToCsv listId={}", listId);
        List<UserManagementListMemberPojo> members = getMembersByListId(listId);

        try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(outputStream))) {
            String[] header = memberColumns.stream().map(ColumnConfig::getDbField).toArray(String[]::new);
            writer.writeNext(header);

            for (UserManagementListMemberPojo m : members) {
                String[] row = memberColumns.stream().map(c -> getFieldValue(m, c.getName())).toArray(String[]::new);
                writer.writeNext(row);
                logger.debug("exportListToCsv member written={}", m);
            }
        }
    }

    // ------------------ Excel Import/Export ------------------

    @Override
    public void importListFromExcel(InputStream inputStream, UserManagementListPojo list, String originalFileName) throws Exception {
        logger.debug("importListFromExcel list={}", list);
        logger.debug("importListFromExcel originalFileName={}", originalFileName);

        List<UserManagementListMemberPojo> members = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            if (rows.hasNext()) rows.next(); // skip header
            while (rows.hasNext()) {
                Row row = rows.next();
                UserManagementListMemberPojo member = new UserManagementListMemberPojo();
                for (int i = 0; i < memberColumns.size(); i++) {
                    setFieldValue(member, memberColumns.get(i).getName(), row.getCell(i).getStringCellValue());
                }
                members.add(member);
                logger.debug("importListFromExcel member parsed={}", member);
            }
        }

        createList(list, members);

        Path path = getListFilePath(list.getId(), originalFileName);
        try (OutputStream os = Files.newOutputStream(path)) {
            if (inputStream.markSupported()) inputStream.reset();
            inputStream.transferTo(os);
        }
        list.setFilePath(path.toString());
    }

    @Override
    public void exportListToExcel(Long listId, OutputStream outputStream) throws Exception {
        logger.debug("exportListToExcel listId={}", listId);
        List<UserManagementListMemberPojo> members = getMembersByListId(listId);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Users");

            // Header
            Row header = sheet.createRow(0);
            for (int i = 0; i < memberColumns.size(); i++) {
                header.createCell(i).setCellValue(memberColumns.get(i).getDbField());
            }

            // Rows
            int rowIdx = 1;
            for (UserManagementListMemberPojo m : members) {
                Row row = sheet.createRow(rowIdx++);
                for (int i = 0; i < memberColumns.size(); i++) {
                    row.createCell(i).setCellValue(getFieldValue(m, memberColumns.get(i).getName()));
                }
                logger.debug("exportListToExcel member written={}", m);
            }

            workbook.write(outputStream);
        }
    }
}
