package simple.chatgpt.ftp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class FtpServerConfig {

    private static final Logger logger = LogManager.getLogger(FtpServerConfig.class);
    private static final String FTP_ROOT_PATH = "C:/apps/ftp-drop";

    public FtpServerConfig() {
        logger.debug("FtpServerConfig constructor called");
        ensureRootExists();
    }

    /*
    hung: ensure the root FTP folder exists
    */
    private void ensureRootExists() {
        File root = new File(FTP_ROOT_PATH);
        if (!root.exists()) {
            boolean created = root.mkdirs();
            logger.debug("ensureRootExists created={}", created);
        }
    }

    /*
    hung: get target file in FTP folder by name
    */
    public File getTargetFile(String fileName) {
        File target = new File(FTP_ROOT_PATH, fileName);
        logger.debug("getTargetFile target={}", target);
        return target;
    }

    /*
    hung: write CSV to FTP folder using OutputStream consumer
    */
    public void writeCsv(String fileName, Consumer<OutputStream> writer) throws IOException {
        File target = getTargetFile(fileName);
        ensureParentExists(target);
        try (FileOutputStream fos = new FileOutputStream(target)) {
            writer.accept(fos);
            logger.debug("writeCsv written to target={}", target);
        }
    }

    /*
    hung: ensure parent directories exist
    */
    private void ensureParentExists(File file) {
        File parent = file.getParentFile();
        if (!parent.exists()) {
            boolean created = parent.mkdirs();
            logger.debug("ensureParentExists created={}", created);
        }
    }
}
