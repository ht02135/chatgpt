package simple.chatgpt.ftp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets; // <-- add this

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.WindowsFakeFileSystem;
import org.springframework.stereotype.Component;

/*
hung: do not remove my comment.
this class configures a mock FTP server that maps to real directory.
*/

@Component
public class FtpServerConfig {

    private static final Logger logger = LogManager.getLogger(FtpServerConfig.class);

    private static final int FTP_PORT = 2221;
    private static final String FTP_ROOT_PATH = "C:/apps/ftp-drop";

    private FakeFtpServer ftpServer;

    @PostConstruct
    public void startFtpServer() {
        logger.debug("startFtpServer called");

        ftpServer = new FakeFtpServer();
        ftpServer.setServerControlPort(FTP_PORT);
        logger.debug("startFtpServer FTP port={}", FTP_PORT);

        FileSystem fs = new WindowsFakeFileSystem();

        File ftpRoot = new File(FTP_ROOT_PATH);
        if (!ftpRoot.exists()) {
            boolean created = ftpRoot.mkdirs();
            logger.debug("startFtpServer createdRootDir={}", created);
        }

        fs.add(new DirectoryEntry(ftpRoot.getAbsolutePath().replace("\\", "/")));
        ftpServer.setFileSystem(fs);

        try {
            ftpServer.start();
            logger.debug("startFtpServer Mock FTP server started successfully on port {}", ftpServer.getServerControlPort());
        } catch (Exception e) {
            logger.error("startFtpServer failed", e);
            throw new RuntimeException(e);
        }
    }

    public FakeFtpServer getFtpServer() {
        return ftpServer;
    }

    public String getFtpRootPath() {
        return FTP_ROOT_PATH;
    }

    public int getFtpPort() {
        return FTP_PORT;
    }

    /*
     * hung: add a helper to persist real files to disk and also sync with mock FTP
     */
    public void writeRealFile(File targetFile, byte[] data) throws IOException {
        // write to real filesystem
        try (FileOutputStream fos = new FileOutputStream(targetFile)) {
            fos.write(data);
            logger.debug("writeRealFile wrote {} bytes to {}", data.length, targetFile.getAbsolutePath());
        }

        // sync with mock FTP filesystem (text only)
        FileSystem fs = ftpServer.getFileSystem();
        String ftpPath = targetFile.getAbsolutePath().replace("\\", "/");
        if (!fs.exists(ftpPath)) {
            String content = new String(data, StandardCharsets.UTF_8);
            fs.add(new FileEntry(ftpPath, content));
        } else {
            FileEntry fileEntry = (FileEntry) fs.getEntry(ftpPath);
            String content = new String(data, StandardCharsets.UTF_8);
            fileEntry.setContents(content);
        }
        logger.debug("writeRealFile synced file to mock FTP at {}", ftpPath);
    }
}
