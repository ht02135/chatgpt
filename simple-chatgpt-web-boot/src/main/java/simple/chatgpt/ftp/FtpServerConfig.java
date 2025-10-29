package simple.chatgpt.ftp;

import java.io.File;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.WindowsFakeFileSystem;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FtpServerConfig {

    private static final Logger logger = LogManager.getLogger(FtpServerConfig.class);

    /*
    Connect via FTP client (optional)
	You can test with any FTP client:
	Host: localhost
	Port: 2121
	Username: user
	Password: password
	Files in C:/apps/ftp-drop are directly accessible.
    */
    // ======= CONSTANTS =======
    public static final String FTP_USER = "user";
    public static final String FTP_PASSWORD = "password";
    public static final int FTP_PORT = 2121;
    public static final String FTP_ROOT_FOLDER = "C:/apps/ftp-drop";

    private FakeFtpServer ftpServer;

    @PostConstruct
    public void startFtpServer() {
        logger.debug("Starting local FTP server at localhost:{}", FTP_PORT);

        ftpServer = new FakeFtpServer();
        ftpServer.setServerControlPort(FTP_PORT);

        // FTP user account with root as home directory
        ftpServer.addUserAccount(new UserAccount(FTP_USER, FTP_PASSWORD, "/"));

        // Ensure the real folder exists
        File rootDir = new File(FTP_ROOT_FOLDER);
        if (!rootDir.exists()) {
            boolean created = rootDir.mkdirs();
            logger.debug("Created FTP root folder: {} -> {}", FTP_ROOT_FOLDER, created);
        }

        // Virtual filesystem
        FileSystem fs = new WindowsFakeFileSystem();
        fs.add(new DirectoryEntry("/")); // FTP root

        // Populate virtual root with existing files (empty placeholder)
        File[] existingFiles = rootDir.listFiles();
        if (existingFiles != null) {
            for (File f : existingFiles) {
                if (f.isFile()) {
                    fs.add(new FileEntry("/" + f.getName())); // Empty virtual file
                    logger.debug("Added virtual file to FTP root: {}", f.getName());
                }
            }
        }

        ftpServer.setFileSystem(fs);
        ftpServer.start();

        logger.debug("Local FTP server started successfully. Files dropped here: {}", FTP_ROOT_FOLDER);
    }

    @PreDestroy
    public void stopFtpServer() {
        if (ftpServer != null) {
            ftpServer.stop();
            logger.debug("Local FTP server stopped");
        }
    }

    @Bean
    public FakeFtpServer fakeFtpServer() {
        return ftpServer;
    }
}
