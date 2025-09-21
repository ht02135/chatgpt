package simple.chatgpt.service.management;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
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

import simple.chatgpt.mapper.management.UserManagementListMapper;
import simple.chatgpt.mapper.management.UserManagementListMemberMapper;
import simple.chatgpt.pojo.management.UserManagementListMemberPojo;
import simple.chatgpt.pojo.management.UserManagementListPojo;

@Service
public class UserManagementListServiceImpl implements UserManagementListService {

    private static final Logger logger = LogManager.getLogger(UserManagementListServiceImpl.class);

    private final UserManagementListMapper listMapper;
    private final UserManagementListMemberMapper memberMapper;
    private final Path storageDir;

    public UserManagementListServiceImpl(UserManagementListMapper listMapper,
                                         UserManagementListMemberMapper memberMapper) throws IOException {
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

    // ------------------ CSV Import/Export ------------------

    @Override
    public void importListFromCsv(InputStream inputStream, UserManagementListPojo list, String originalFileName) throws Exception {
        logger.debug("importListFromCsv list={}", list);
        logger.debug("importListFromCsv originalFileName={}", originalFileName);

        List<UserManagementListMemberPojo> members = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new InputStreamReader(inputStream))) {
            String[] header = reader.readNext(); // skip header
            String[] row;
            while ((row = reader.readNext()) != null) {
                UserManagementListMemberPojo member = new UserManagementListMemberPojo();
                member.setUserName(row[0]);
                member.setPassword(row[1]);
                member.setFirstName(row[2]);
                member.setLastName(row[3]);
                member.setEmail(row[4]);
                member.setAddressLine1(row[5]);
                member.setAddressLine2(row[6]);
                member.setCity(row[7]);
                member.setState(row[8]);
                member.setPostCode(row[9]);
                member.setCountry(row[10]);
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
            String[] header = {"user_name","password","first_name","last_name","email",
                    "address_line_1","address_line_2","city","state","post_code","country"};
            writer.writeNext(header);

            for (UserManagementListMemberPojo m : members) {
                writer.writeNext(new String[]{
                        m.getUserName(), m.getPassword(), m.getFirstName(), m.getLastName(),
                        m.getEmail(), m.getAddressLine1(), m.getAddressLine2(),
                        m.getCity(), m.getState(), m.getPostCode(), m.getCountry()
                });
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
                member.setUserName(row.getCell(0).getStringCellValue());
                member.setPassword(row.getCell(1).getStringCellValue());
                member.setFirstName(row.getCell(2).getStringCellValue());
                member.setLastName(row.getCell(3).getStringCellValue());
                member.setEmail(row.getCell(4).getStringCellValue());
                member.setAddressLine1(row.getCell(5).getStringCellValue());
                member.setAddressLine2(row.getCell(6).getStringCellValue());
                member.setCity(row.getCell(7).getStringCellValue());
                member.setState(row.getCell(8).getStringCellValue());
                member.setPostCode(row.getCell(9).getStringCellValue());
                member.setCountry(row.getCell(10).getStringCellValue());
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

            Row header = sheet.createRow(0);
            String[] columns = {"user_name","password","first_name","last_name","email",
                    "address_line_1","address_line_2","city","state","post_code","country"};
            for (int i = 0; i < columns.length; i++) {
                header.createCell(i).setCellValue(columns[i]);
            }

            int rowIdx = 1;
            for (UserManagementListMemberPojo m : members) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(m.getUserName());
                row.createCell(1).setCellValue(m.getPassword());
                row.createCell(2).setCellValue(m.getFirstName());
                row.createCell(3).setCellValue(m.getLastName());
                row.createCell(4).setCellValue(m.getEmail());
                row.createCell(5).setCellValue(m.getAddressLine1());
                row.createCell(6).setCellValue(m.getAddressLine2());
                row.createCell(7).setCellValue(m.getCity());
                row.createCell(8).setCellValue(m.getState());
                row.createCell(9).setCellValue(m.getPostCode());
                row.createCell(10).setCellValue(m.getCountry());
                logger.debug("exportListToExcel member written={}", m);
            }

            workbook.write(outputStream);
        }
    }
}
