package simple.chatgpt.service.management.file;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/*
hung: generic CSV file service to handle header/row reading/writing
*/
public interface CsvFileService {

    void writeCsv(List<String> headers, List<List<String>> rows, OutputStream os) throws Exception;

    List<List<String>> readCsv(InputStream is) throws Exception;
}
