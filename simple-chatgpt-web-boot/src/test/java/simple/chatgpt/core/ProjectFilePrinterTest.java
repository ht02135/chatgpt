package simple.chatgpt.core;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

public class ProjectFilePrinterTest {

    private final Path projectRoot = Paths.get("C:\\worksplace\\sts\\chatgpt\\simple-chatgpt-web-boot");
    private final Path outputFilePath = Paths.get("C:\\worksplace\\sts\\chatgpt\\simple-chatgpt-web-boot\\src\\main\\resources\\simple-chatgpt-web.txt");

    @Test
    void printProjectFiles() throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(outputFilePath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            // Part 1: Print file structure
            writer.write("└── simple-chatgpt-web");
            writer.newLine();

            Path pomFile = projectRoot.resolve("pom.xml");
            if (Files.exists(pomFile)) {
                writer.write("    ├── pom.xml");
                writer.newLine();
            }

            Path srcDir = projectRoot.resolve("src");
            if (Files.exists(srcDir)) {
                writer.write("    └── src");
                writer.newLine();
                printMainSubdirectories(writer, srcDir, "        ");
            }
            
            // Part 2: Print file contents
            writer.write("===================");
            writer.newLine();
            printFilteredFileContents(writer);
        }
    }
    
    private void printMainSubdirectories(BufferedWriter writer, Path path, String indent) throws IOException {
        try (Stream<Path> stream = Files.list(path)) {
            stream.sorted()
                  .forEach(p -> {
                      try {
                          if (Files.isDirectory(p)) {
                              writer.write(indent + "└── " + p.getFileName());
                              writer.newLine();
                              printDirectoryStructure(writer, p, indent + "    ");
                          } else {
                              if (hasTargetExtension(p)) {
                                  writer.write(indent + "└── " + p.getFileName());
                                  writer.newLine();
                              }
                          }
                      } catch (IOException e) {
                          throw new RuntimeException(e);
                      }
                  });
        }
    }

    private void printDirectoryStructure(BufferedWriter writer, Path path, String indent) throws IOException {
        try (Stream<Path> stream = Files.list(path)) {
            stream.sorted()
                  .forEach(p -> {
                      try {
                          if (Files.isDirectory(p)) {
                              writer.write(indent + "└── " + p.getFileName());
                              writer.newLine();
                              printDirectoryStructure(writer, p, indent + "    ");
                          } else {
                              if (hasTargetExtension(p)) {
                                  writer.write(indent + "└── " + p.getFileName());
                                  writer.newLine();
                              }
                          }
                      } catch (IOException e) {
                          throw new RuntimeException(e);
                      }
                  });
        }
    }
    
    private void printFilteredFileContents(BufferedWriter writer) throws IOException {
        Path mainJavaDir = projectRoot.resolve(Paths.get("src", "main", "java"));
        Path mainResourcesDir = projectRoot.resolve(Paths.get("src", "main", "resources"));
        Path mainWebappDir = projectRoot.resolve(Paths.get("src", "main", "webapp"));
        Path pomFile = projectRoot.resolve("pom.xml");

        // Use an array of paths to process them in a loop
        Path[] pathsToProcess = {pomFile, mainJavaDir, mainResourcesDir, mainWebappDir};

        for (Path path : pathsToProcess) {
            if (Files.exists(path)) {
                if (Files.isDirectory(path)) {
                    try (Stream<Path> stream = Files.walk(path)) {
                        stream.filter(Files::isRegularFile)
                              .filter(this::hasTargetExtension)
                              .sorted()
                              .forEach(p -> {
                                  try {
                                      printFile(writer, p);
                                  } catch (IOException e) {
                                      throw new RuntimeException(e);
                                  }
                              });
                    }
                } else {
                    printFile(writer, path);
                }
            }
        }
    }

    private void printFile(BufferedWriter writer, Path file) throws IOException {
        writer.write("#########################");
        writer.newLine();
        writer.write("file-name=" + projectRoot.relativize(file));
        //writer.newLine();
        //writer.write("content of " + file.getFileName());
        writer.newLine();
        writer.newLine();
        Files.lines(file).forEach(line -> {
            try {
                writer.write(line);
                writer.newLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        writer.newLine();
    }

    private boolean hasTargetExtension(Path file) {
        String name = file.getFileName().toString().toLowerCase();

        // Check for specific exclusion: .js files under src/main/webapp/js
        Path webappJsPath = projectRoot.resolve(Paths.get("src", "main", "webapp", "js"));
        if (name.endsWith(".js") && file.startsWith(webappJsPath)) {
            return false;
        }

        return name.endsWith(".java") || name.endsWith(".properties") ||
               name.endsWith(".xml") || name.endsWith(".sql") ||
               name.endsWith(".js") || name.endsWith(".html") ||
               name.endsWith(".css") || name.endsWith(".jsp");
    }
}