package simple.chatgpt.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

public class JsConstantsCollectorTest {

    private static final Logger logger = LogManager.getLogger(JsConstantsCollectorTest.class);

    private static final String WEBAPP_PATH = "C:\\worksplace\\sts\\chatgpt\\simple-chatgpt-web-boot\\src\\main\\webapp";
    private static final String OUTPUT_FILE = "C:\\worksplace\\sts\\chatgpt\\simple-chatgpt-web-boot\\src\\main\\resources\\constants.txt";

    // Only include these JSP files
    private static final Set<String> TARGET_JSP_FILES = Set.of(
            "index.jsp",
            "dashboard.jsp",
            "login.jsp",
            "logout.jsp",
            "register.jsp"
    );

    @Test
    public void collectConstAndContextPathLines() throws IOException {
        logger.debug("collectConstAndContextPathLines called");

        File webappDir = new File(WEBAPP_PATH);
        logger.debug("WEBAPP_PATH={}", WEBAPP_PATH);

        if (!webappDir.exists() || !webappDir.isDirectory()) {
            logger.error("Invalid webapp directory: {}", WEBAPP_PATH);
            throw new IllegalArgumentException("Invalid webapp directory path");
        }

        // Find all .js files + selected .jsp files
        var targetFiles = Files.walk(webappDir.toPath())
                .filter(p -> {
                    File f = p.toFile();
                    String name = f.getName().toLowerCase(Locale.ROOT);
                    return f.isFile() &&
                            (name.endsWith(".js") ||
                                    (name.endsWith(".jsp") && TARGET_JSP_FILES.contains(name)));
                })
                .sorted()
                .collect(Collectors.toList());

        logger.debug("Total target files found={}", targetFiles.size());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_FILE))) {
            for (var path : targetFiles) {
                File file = path.toFile();
                String fullPath = file.getAbsolutePath();
                String fileName = file.getName();

                logger.debug("Processing file={}", fileName);
                logger.debug("Full path={}", fullPath);

                // Filter lines that contain both "const" and "CONTEXT_PATH"
                List<String> constLines = Files.readAllLines(file.toPath()).stream()
                        .filter(line -> line.contains("const") && line.contains("CONTEXT_PATH"))
                        .collect(Collectors.toList());

                if (!constLines.isEmpty()) {
                    writer.write("// " + fullPath + System.lineSeparator());
                    writer.write("// " + fileName + System.lineSeparator());
                    for (String constLine : constLines) {
                        writer.write(constLine.trim() + System.lineSeparator());
                    }
                    writer.write(System.lineSeparator());
                }
            }
        }

        logger.info("constants.txt generated successfully at {}", OUTPUT_FILE);
    }
}
