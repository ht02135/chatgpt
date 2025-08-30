import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

class FilePrinterTest {

    @Test
    void printFiles() throws Exception {
        String projectPath = "C:\\worksplace\\sts\\chatgpt\\simple-chatgpt-web";
        Path rootPath = Paths.get(projectPath);

        if (!Files.exists(rootPath)) {
            System.err.println("Path not found: " + projectPath);
            return;
        }

        System.out.println("└── simple-chatgpt-web");

        // Scan specific directories
        scanDirectory(rootPath.resolve("src/main/java"), "    ├── src/main/java");
        scanDirectory(rootPath.resolve("src/main/resources"), "    ├── src/main/resources");
        scanDirectory(rootPath.resolve("src/main/webapp"), "    ├── src/main/webapp");
        scanDirectory(rootPath.resolve("test"), "    ├── test");

        // Check for pom.xml
        Path pomPath = rootPath.resolve("pom.xml");
        if (Files.exists(pomPath)) {
            System.out.println("    └── pom.xml");
        }
    }

    private void scanDirectory(Path dirPath, String prefix) throws IOException {
        if (!Files.exists(dirPath)) return;

        System.out.println(prefix);
        printTreeStructure(dirPath, prefix.replaceAll("[├└]── .*", "    │   "), false);
    }

    private void printTreeStructure(Path path, String prefix, boolean isLast) throws IOException {
        if (Files.isDirectory(path)) {
            System.out.println(prefix + (isLast ? "└── " : "├── ") + path.getFileName());

            List<Path> children = Files.list(path)
                    .filter(p -> Files.isDirectory(p) || hasTargetExtension(p))
                    .sorted((a, b) -> {
                        if (Files.isDirectory(a) && !Files.isDirectory(b)) return -1;
                        if (!Files.isDirectory(a) && Files.isDirectory(b)) return 1;
                        return a.getFileName().toString().compareToIgnoreCase(b.getFileName().toString());
                    })
                    .toList();

            for (int i = 0; i < children.size(); i++) {
                boolean childIsLast = (i == children.size() - 1);
                String childPrefix = prefix + (isLast ? "    " : "│   ");

                if (Files.isDirectory(children.get(i))) {
                    printTreeStructure(children.get(i), childPrefix, childIsLast);
                } else {
                    System.out.println(childPrefix + (childIsLast ? "└── " : "├── ") + children.get(i).getFileName());
                }
            }
        }
    }

    private boolean hasTargetExtension(Path file) {
        String name = file.getFileName().toString().toLowerCase();
        return name.endsWith(".java") || name.endsWith(".properties") ||
                name.endsWith(".xml") || name.endsWith(".sql") ||
                name.endsWith(".js") || name.endsWith(".html") || name.endsWith(".jsp");
    }
}