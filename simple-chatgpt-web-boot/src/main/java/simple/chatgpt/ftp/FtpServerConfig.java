package simple.chatgpt.ftp;

import java.io.File;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
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

    // ==============================================================
    // ================ CONSTANTS ===================================
    // ==============================================================
    private static final int FTP_PORT = 2221;
    private static final String FTP_ROOT_PATH = "C:/apps/ftp-drop";

    // ==============================================================
    // ================ FIELDS ======================================
    // ==============================================================
    private FakeFtpServer ftpServer;

    // ==============================================================
    // ================ INIT ========================================
    // ==============================================================
    @PostConstruct
    public void startFtpServer() {
        logger.debug("startFtpServer called");

        ftpServer = new FakeFtpServer();
        ftpServer.setServerControlPort(FTP_PORT);
        logger.debug("startFtpServer FTP port={}", FTP_PORT);

        FileSystem fs = new WindowsFakeFileSystem();

        File ftpRoot = new File(FTP_ROOT_PATH);
        logger.debug("startFtpServer ftpRoot={}", ftpRoot);

        if (!ftpRoot.exists()) {
            boolean created = ftpRoot.mkdirs();
            logger.debug("startFtpServer createdRootDir={}", created);
        }

        fs.add(new DirectoryEntry(ftpRoot.getAbsolutePath().replace("\\", "/")));
        ftpServer.setFileSystem(fs);

        try {
            ftpServer.start();
            logger.debug("startFtpServer Mock FTP server started successfully on port {}", ftpServer.getServerControlPort());
            logger.debug("startFtpServer Mock FTP root mapped to {}", ftpRoot.getAbsolutePath());
        } catch (Exception e) {
            logger.error("startFtpServer failed", e);
            throw new RuntimeException(e);
        }
    }

    // ==============================================================
    // ================ ACCESSOR ====================================
    // ==============================================================
    public FakeFtpServer getFtpServer() {
        logger.debug("getFtpServer called");
        return ftpServer;
    }

    public String getFtpRootPath() {
        logger.debug("getFtpRootPath called");
        return FTP_ROOT_PATH;
    }

    public int getFtpPort() {
        logger.debug("getFtpPort called");
        return FTP_PORT;
    }
}
