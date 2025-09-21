package simple.chatgpt.service.management;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import simple.chatgpt.mapper.management.UserManagementListMapper;
import simple.chatgpt.mapper.management.UserManagementListMemberMapper;
import simple.chatgpt.pojo.management.UserManagementListMemberPojo;
import simple.chatgpt.pojo.management.UserManagementListPojo;

public class UserManagementListService {

    private final SqlSessionFactory sqlSessionFactory;

    public UserManagementListService(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    // ------------------ Core CRUD ------------------

    public void createList(UserManagementListPojo list, List<UserManagementListMemberPojo> members) {
        try (SqlSession session = sqlSessionFactory.openSession(false)) {
            UserManagementListMapper listMapper = session.getMapper(UserManagementListMapper.class);
            UserManagementListMemberMapper memberMapper = session.getMapper(UserManagementListMemberMapper.class);

            listMapper.insertList(list);
            Long listId = list.getId();

            if (members != null && !members.isEmpty()) {
                for (UserManagementListMemberPojo m : members) {
                    m.setListId(listId);
                }
                memberMapper.batchInsertMembers(members);
            }

            session.commit();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create list", e);
        }
    }

    public void deleteList(Long listId) {
        try (SqlSession session = sqlSessionFactory.openSession(false)) {
            UserManagementListMapper listMapper = session.getMapper(UserManagementListMapper.class);
            UserManagementListMemberMapper memberMapper = session.getMapper(UserManagementListMemberMapper.class);

            memberMapper.deleteMembersByListId(listId);
            listMapper.deleteList(listId);

            session.commit();
        }
    }

    public UserManagementListPojo getListById(Long listId) {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            return session.getMapper(UserManagementListMapper.class).findListById(listId);
        }
    }

    public List<UserManagementListMemberPojo> getMembersByListId(Long listId) {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            return session.getMapper(UserManagementListMemberMapper.class).findMembersByListId(listId);
        }
    }

    public void updateList(UserManagementListPojo list) {
        try (SqlSession session = sqlSessionFactory.openSession(false)) {
            session.getMapper(UserManagementListMapper.class).updateList(list);
            session.commit();
        }
    }

    // ------------------ CSV Import/Export ------------------

    public void importListFromCsv(InputStream inputStream, UserManagementListPojo list) throws Exception {
        List<UserManagementListMemberPojo> members = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new InputStreamReader(inputStream))) {
            String[] header = reader.readNext();
            String[] row;
            while ((row = reader.readNext()) != null) {
                UserManagementListMemberPojo m = mapRowToMember(row);
                members.add(m);
            }
        }
        createList(list, members);
    }

    public void exportListToCsv(Long listId, OutputStream outputStream) throws Exception {
        List<UserManagementListMemberPojo> members = getMembersByListId(listId);
        try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(outputStream))) {
            String[] header = {"user_name","password","first_name","last_name","email",
                               "address_line_1","address_line_2","city","state","post_code","country"};
            writer.writeNext(header);

            for (UserManagementListMemberPojo m : members) {
                writer.writeNext(memberToRow(m));
            }
        }
    }

    // ------------------ Excel Import/Export ------------------

    public void importListFromExcel(InputStream inputStream, UserManagementListPojo list) throws Exception {
        List<UserManagementListMemberPojo> members = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            if (rows.hasNext()) rows.next(); // skip header

            while (rows.hasNext()) {
                Row row = rows.next();
                UserManagementListMemberPojo m = new UserManagementListMemberPojo();
                m.setUserName(row.getCell(0).getStringCellValue());
                m.setPassword(row.getCell(1).getStringCellValue());
                m.setFirstName(row.getCell(2).getStringCellValue());
                m.setLastName(row.getCell(3).getStringCellValue());
                m.setEmail(row.getCell(4).getStringCellValue());
                m.setAddressLine1(row.getCell(5).getStringCellValue());
                m.setAddressLine2(row.getCell(6).getStringCellValue());
                m.setCity(row.getCell(7).getStringCellValue());
                m.setState(row.getCell(8).getStringCellValue());
                m.setPostCode(row.getCell(9).getStringCellValue());
                m.setCountry(row.getCell(10).getStringCellValue());
                members.add(m);
            }
        }

        createList(list, members);
    }

    public void exportListToExcel(Long listId, OutputStream outputStream) throws Exception {
        List<UserManagementListMemberPojo> members = getMembersByListId(listId);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Users");
            String[] columns = {"user_name","password","first_name","last_name","email",
                                "address_line_1","address_line_2","city","state","post_code","country"};

            Row header = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) header.createCell(i).setCellValue(columns[i]);

            int rowIdx = 1;
            for (UserManagementListMemberPojo m : members) {
                Row row = sheet.createRow(rowIdx++);
                for (int i = 0; i < 11; i++) row.createCell(i).setCellValue(memberToRow(m)[i]);
            }

            workbook.write(outputStream);
        }
    }

    // ------------------ Helper Methods ------------------

    private UserManagementListMemberPojo mapRowToMember(String[] row) {
        UserManagementListMemberPojo m = new UserManagementListMemberPojo();
        m.setUserName(row[0]);
        m.setPassword(row[1]);
        m.setFirstName(row[2]);
        m.setLastName(row[3]);
        m.setEmail(row[4]);
        m.setAddressLine1(row[5]);
        m.setAddressLine2(row[6]);
        m.setCity(row[7]);
        m.setState(row[8]);
        m.setPostCode(row[9]);
        m.setCountry(row[10]);
        return m;
    }

    private String[] memberToRow(UserManagementListMemberPojo m) {
        return new String[]{
            m.getUserName(),
            m.getPassword(),
            m.getFirstName(),
            m.getLastName(),
            m.getEmail(),
            m.getAddressLine1(),
            m.getAddressLine2(),
            m.getCity(),
            m.getState(),
            m.getPostCode(),
            m.getCountry()
        };
    }
}
