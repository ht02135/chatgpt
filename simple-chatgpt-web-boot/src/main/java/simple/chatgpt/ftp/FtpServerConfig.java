package simple.chatgpt.ftp;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;
import org.springframework.stereotype.Component;

@Component
public class FtpServerConfig {

    private static final Logger logger = LogManager.getLogger(FtpServerConfig.class);

    private FakeFtpServer ftpServer;

    @PostConstruct
    public void startFtpServer() {
        logger.debug("Starting Mock FTP server");

        ftpServer = new FakeFtpServer();
        ftpServer.setServerControlPort(2221); // your FTP port

        FileSystem fs = new UnixFakeFileSystem();
        fs.add(new DirectoryEntry("/")); // start with empty root
        ftpServer.setFileSystem(fs);

        try {
            ftpServer.start();
            logger.debug("Mock FTP server started successfully on port {}", ftpServer.getServerControlPort());
        } catch (Exception e) {
            logger.error("Failed to start Mock FTP server", e);
            throw new RuntimeException(e);
        }
    }

    public FakeFtpServer getFtpServer() {
        return ftpServer;
    }
}
