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

        // Scan specific directories in order
        scanPath(rootPath, "src", "    ├── src");

        // Check for pom.xml at root
        Path pomPath = rootPath.resolve("pom.xml");
        if (Files.exists(pomPath)) {
            System.out.println("    └── pom.xml");
        }
    }

    private void scanPath(Path rootPath, String relativePath, String displayPrefix) throws IOException {
        Path targetPath = rootPath.resolve(relativePath);
        if (!Files.exists(targetPath)) return;

        System.out.println(displayPrefix);

        if (relativePath.equals("src")) {
            // Handle src directory structure
            Path mainPath = targetPath.resolve("main");
            Path testPath = targetPath.resolve("test");

            if (Files.exists(mainPath)) {
                System.out.println("    │   ├── main");
                scanMainDirectory(mainPath);
            }

            if (Files.exists(testPath)) {
                System.out.println("    │   └── test");
                printTreeStructure(testPath, "    │       ", false);
            }
        }
    }

    private void scanMainDirectory(Path mainPath) throws IOException {
        Path javaPath = mainPath.resolve("java");
        Path resourcesPath = mainPath.resolve("resources");
        Path webappPath = mainPath.resolve("webapp");

        boolean hasResources = Files.exists(resourcesPath);
        boolean hasWebapp = Files.exists(webappPath);

        if (Files.exists(javaPath)) {
            String prefix = (hasResources || hasWebapp) ? "├──" : "└──";
            System.out.println("    │   │   " + prefix + " java");
            printTreeStructure(javaPath, "    │   │   " + (prefix.equals("├──") ? "│   " : "    "), false);
        }

        if (hasResources) {
            String prefix = hasWebapp ? "├──" : "└──";
            System.out.println("    │   │   " + prefix + " resources");
            printTreeStructure(resourcesPath, "    │   │   " + (prefix.equals("├──") ? "│   " : "    "), false);
        }

        if (hasWebapp) {
            System.out.println("    │   │   └── webapp");
            printTreeStructure(webappPath, "    │   │       ", false);
        }
    }

    private void printTreeStructure(Path path, String prefix, boolean isLast) throws IOException {
        if (!Files.isDirectory(path)) return;

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
            String childPrefix = prefix + (childIsLast ? "└── " : "├── ");
            String nextPrefix = prefix + (childIsLast ? "    " : "│   ");

            if (Files.isDirectory(children.get(i))) {
                System.out.println(childPrefix + children.get(i).getFileName());
                printTreeStructure(children.get(i), nextPrefix, childIsLast);
            } else {
                System.out.println(childPrefix + children.get(i).getFileName());
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