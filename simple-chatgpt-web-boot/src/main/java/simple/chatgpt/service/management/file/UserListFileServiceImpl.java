package simple.chatgpt.service.management.file;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.springframework.stereotype.Service;

import simple.chatgpt.config.management.ColumnConfig;
import simple.chatgpt.config.management.loader.DownloadConfigLoader;
import simple.chatgpt.config.management.loader.UploadConfigLoader;
import simple.chatgpt.ftp.FtpServerConfig;
import simple.chatgpt.pojo.management.UserManagementListMemberPojo;
import simple.chatgpt.pojo.management.UserManagementListPojo;
import simple.chatgpt.service.management.UserManagementListMemberService;
import simple.chatgpt.service.management.UserManagementListService;
import simple.chatgpt.util.ParamWrapper;

/*
hung: make sure you do not remove any of my comments. 
If you dare remove hung comment, you are in big trouble.
*/

@Service
public class UserListFileServiceImpl implements UserListFileService {

    private static final Logger logger = LogManager.getLogger(UserListFileServiceImpl.class);
    private static final String MEMBER_GRID_ID = "userListMembers";

    private final List<ColumnConfig> uploadColumns;
    private final List<ColumnConfig> downloadColumns;
    private final UserManagementListService listService;
    private final UserManagementListMemberService memberService;
    private final CsvFileService csvFileService;
    private final ExcelFileService excelFileService;
    private final FtpServerConfig ftpServerConfig;

    public UserListFileServiceImpl(
            UserManagementListService listService,
            UserManagementListMemberService memberService,
            DownloadConfigLoader downloadConfigLoader,
            UploadConfigLoader uploadConfigLoader,
            CsvFileService csvFileService,
            ExcelFileService excelFileService,
            FtpServerConfig ftpServerConfig) {

        logger.debug("UserListFileServiceImpl constructor called");
        logger.debug("listService={}", listService);
        logger.debug("memberService={}", memberService);
        logger.debug("downloadConfigLoader={}", downloadConfigLoader);
        logger.debug("uploadConfigLoader={}", uploadConfigLoader);
        logger.debug("csvFileService={}", csvFileService);
        logger.debug("excelFileService={}", excelFileService);
        logger.debug("ftpServerConfig={}", ftpServerConfig);

        this.listService = listService;
        this.memberService = memberService;
        this.csvFileService = csvFileService;
        this.excelFileService = excelFileService;
        this.ftpServerConfig = ftpServerConfig;

        this.uploadColumns = uploadConfigLoader.getColumns(MEMBER_GRID_ID);
        this.downloadColumns = downloadConfigLoader.getColumns(MEMBER_GRID_ID);
    }

    // ==============================================================
    // ================ CSV ==========================================
    // ==============================================================

    /*
    hung : dont remove this comment
    importList only import list into table 
    */
    @Override
    public void importListFromCsv(Map<String, Object> params) throws Exception {
        logger.debug("importListFromCsv START");
        logger.debug("importListFromCsv params={}", params);

        InputStream inputStream = ParamWrapper.unwrap(params, "inputStream");
        UserManagementListPojo list = ParamWrapper.unwrap(params, "list");

        list.setFilePath(list.getOriginalFileName());
        logger.debug("importListFromCsv list={}", list);

        // Persist list using proper service
        UserManagementListPojo createdList = listService.create(list);
        logger.debug("importListFromCsv createdList={}", createdList);

        // Read CSV rows
        List<List<String>> rows = csvFileService.readCsv(inputStream);
        logger.debug("importListFromCsv total rows read={}", rows.size());

        Iterator<List<String>> iterator = rows.iterator();
        if (iterator.hasNext()) iterator.next(); // skip header

        while (iterator.hasNext()) {
            List<String> row = iterator.next();
            UserManagementListMemberPojo member = new UserManagementListMemberPojo();
            member.setListId(createdList.getId());
            for (int i = 0; i < uploadColumns.size() && i < row.size(); i++) {
                setFieldValue(member, uploadColumns.get(i).getName(), row.get(i));
            }
            UserManagementListMemberPojo createdMember = memberService.create(member);
            logger.debug("importListFromCsv createdMember={}", createdMember);
        }

        logger.debug("importListFromCsv DONE");
    }

    /*
    hung : dont remove this comment
    exportList only export list table to output file
    */
    @Override
    public void exportListToCsv(Map<String, Object> params) throws Exception {
        logger.debug("exportListToCsv START");
        logger.debug("exportListToCsv params={}", params);

        OutputStream outputStream = ParamWrapper.unwrap(params, "outputStream");
        Long listId = ParamWrapper.unwrap(params, "listId");
        logger.debug("exportListToCsv listId={}", listId);

        List<UserManagementListMemberPojo> members = memberService.getMembersByListId(listId);
        logger.debug("exportListToCsv members={}", members);
        
        List<String> headers = new ArrayList<>();
        for (ColumnConfig c : downloadColumns) headers.add(c.getDbField());
        logger.debug("exportListToCsv headers={}", headers);

        List<List<String>> rows = new ArrayList<>();
        for (UserManagementListMemberPojo m : members) {
            List<String> row = new ArrayList<>();
            for (ColumnConfig c : downloadColumns) row.add(getFieldValue(m, c.getName()));
            rows.add(row);
        }
        logger.debug("exportListToCsv rows={}", rows);

        csvFileService.writeCsv(headers, rows, outputStream);
        logger.debug("exportListToCsv DONE listId={}", listId);
    }

    // ==============================================================
    // ================ EXCEL ========================================
    // ==============================================================

    /*
    hung : dont remove this comment
    importList only import list into table 
    */
    @Override
    public void importListFromExcel(Map<String, Object> params) throws Exception {
        logger.debug("importListFromExcel START");
        logger.debug("importListFromExcel params={}", params);
        InputStream inputStream = ParamWrapper.unwrap(params, "inputStream");
        UserManagementListPojo list = ParamWrapper.unwrap(params, "list");

		list.setFilePath(list.getOriginalFileName());
        logger.debug("importListFromExcel list={}", list);

        // Persist list using proper service
        UserManagementListPojo createdList = listService.create(list);
        logger.debug("importListFromExcel createdList={}", createdList);

        // Read Excel rows
        List<List<String>> rows = excelFileService.readExcel(inputStream, "import.xlsx");
        logger.debug("importListFromExcel total rows read={}", rows.size());

        Iterator<List<String>> iterator = rows.iterator();
        if (iterator.hasNext()) iterator.next(); // skip header

        while (iterator.hasNext()) {
            List<String> row = iterator.next();
            UserManagementListMemberPojo member = new UserManagementListMemberPojo();
            member.setListId(createdList.getId());
            for (int i = 0; i < uploadColumns.size() && i < row.size(); i++) {
                setFieldValue(member, uploadColumns.get(i).getName(), row.get(i));
            }
            UserManagementListMemberPojo createdMember = memberService.create(member);
            logger.debug("importListFromExcel createdMember={}", createdMember);
        }

        logger.debug("importListFromExcel DONE");
    }

    /*
    hung : dont remove this comment
    exportList only export list table to output file
    */
    @Override
    public void exportListToExcel(Map<String, Object> params) throws Exception {
        logger.debug("exportListToExcel START");
        logger.debug("exportListToExcel params={}", params);
        
        OutputStream outputStream = ParamWrapper.unwrap(params, "outputStream");
        Long listId = ParamWrapper.unwrap(params, "listId");
        logger.debug("exportListToExcel listId={}", listId);

        List<UserManagementListMemberPojo> members = memberService.getMembersByListId(listId);
        logger.debug("exportListToExcel members={}", members);
        
        List<String> headers = new ArrayList<>();
        for (ColumnConfig c : downloadColumns) headers.add(c.getDbField());
        logger.debug("exportListToExcel headers={}", headers);

        List<List<String>> rows = new ArrayList<>();
        for (UserManagementListMemberPojo m : members) {
            List<String> row = new ArrayList<>();
            for (ColumnConfig c : downloadColumns) row.add(getFieldValue(m, c.getName()));
            rows.add(row);
        }
        logger.debug("exportListToExcel rows={}", rows);

        excelFileService.writeExcel(headers, rows, outputStream);
        logger.debug("exportListToExcel DONE listId={}", listId);
    }

    // ==============================================================
    // ================ FTP HELPERS =================================
    // ==============================================================

    /*
    hung : dont remove this comment
    used by batch job to generate csv in ftp location
    */
    @Override
    public void exportCsvToFtp(Long listId, File csvFile) throws Exception {
        logger.debug("exportCsvToFtp START");
        logger.debug("exportCsvToFtp listId={}", listId);
        logger.debug("exportCsvToFtp csvFile={}", csvFile);

        FileSystem fs = ftpServerConfig.getFtpServer().getFileSystem();
        String ftpRootPath = ftpServerConfig.getFtpRootPath();
        logger.debug("exportCsvToFtp ftpRootPath={}", ftpRootPath);

        // Ensure FTP root exists
        if (!fs.exists(ftpRootPath)) {
            fs.add(new DirectoryEntry(ftpRootPath.replace("\\", "/")));
        }

        // Build FTP file path
        String ftpFilePath = ftpRootPath.replace("\\", "/") + "/" + csvFile.getName();
        logger.debug("exportCsvToFtp ftpFilePath={}", ftpFilePath);

        if (!fs.exists(ftpFilePath)) {
            fs.add(new org.mockftpserver.fake.filesystem.FileEntry(ftpFilePath));
        }

        // Export CSV into memory
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        exportListToCsv(Map.of("listId", listId, "outputStream", baos));

        // Write CSV bytes into fake FTP file
        org.mockftpserver.fake.filesystem.FileEntry fileEntry =
                (org.mockftpserver.fake.filesystem.FileEntry) fs.getEntry(ftpFilePath);
        fileEntry.setContents(baos.toByteArray());

        // ========== Write real file to disk ==========
        try (java.io.FileOutputStream fos = new java.io.FileOutputStream(csvFile)) {
            baos.writeTo(fos);
        }

        // ========== Update list.filePath in DB ==========
        UserManagementListPojo list = listService.get(listId);
        list.setFilePath(csvFile.getAbsolutePath());
        listService.update(listId, list);
        logger.debug("exportCsvToFtp updated list={}", list);

        logger.debug("exportCsvToFtp DONE");
    }


    // ==============================================================
    // ================ HELPER METHODS ==============================
    // ==============================================================

    private String getFieldValue(UserManagementListMemberPojo member, String property) {
        logger.debug("getFieldValue START");
        try {
            String getter = "get" + property.substring(0, 1).toUpperCase() + property.substring(1);
            Method m = UserManagementListMemberPojo.class.getMethod(getter);
            Object val = m.invoke(member);
            return val != null ? val.toString() : "";
        } catch (Exception e) {
            logger.warn("Failed getFieldValue '{}': {}", property, e.getMessage());
            return "";
        } finally {
            logger.debug("getFieldValue DONE");
        }
    }

    private void setFieldValue(UserManagementListMemberPojo member, String property, String value) {
        logger.debug("setFieldValue START");
        try {
            String setter = "set" + property.substring(0, 1).toUpperCase() + property.substring(1);
            Method m = UserManagementListMemberPojo.class.getMethod(setter, String.class);
            m.invoke(member, value);
        } catch (Exception e) {
            logger.warn("Failed setFieldValue '{}': {}", property, e.getMessage());
        } finally {
            logger.debug("setFieldValue DONE");
        }
    }
}
