package simple.chatgpt.service.management.file;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/*
hung: generic Excel file service to handle header/row reading/writing
*/
public interface ExcelFileService {

    void writeExcel(List<String> headers, List<List<String>> rows, OutputStream os) throws Exception;

    List<List<String>> readExcel(InputStream is, String fileName) throws Exception;
}
