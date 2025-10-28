package simple.chatgpt.service.management.file;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

/*
hung: this class is generic Excel handler used by multiple services
*/

@Service
public class ExcelFileServiceImpl implements ExcelFileService {

    private static final Logger logger = LogManager.getLogger(ExcelFileServiceImpl.class);

    @Override
    public void writeExcel(List<String> headers, List<List<String>> rows, OutputStream os) throws Exception {
        logger.debug("writeExcel START");
        logger.debug("writeExcel headers={}", headers);
        logger.debug("writeExcel rows={}", rows);
        logger.debug("writeExcel os={}", os);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sheet1");

            if (headers != null) {
                Row headerRow = sheet.createRow(0);
                for (int i = 0; i < headers.size(); i++) {
                    headerRow.createCell(i).setCellValue(headers.get(i));
                }
            }

            if (rows != null) {
                for (int i = 0; i < rows.size(); i++) {
                    Row row = sheet.createRow(i + 1);
                    List<String> rowData = rows.get(i);
                    for (int j = 0; j < rowData.size(); j++) {
                        row.createCell(j).setCellValue(rowData.get(j));
                    }
                }
            }

            workbook.write(os);
        }

        logger.debug("writeExcel DONE");
    }

    @Override
    public List<List<String>> readExcel(InputStream is, String fileName) throws Exception {
        logger.debug("readExcel START");
        logger.debug("readExcel is={}", is);
        logger.debug("readExcel fileName={}", fileName);

        List<List<String>> data = new ArrayList<>();

        try (Workbook workbook = fileName != null && fileName.endsWith(".xls") ?
                new HSSFWorkbook(is) : new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = sheet.iterator();

            while (iterator.hasNext()) {
                Row row = iterator.next();
                List<String> cells = new ArrayList<>();
                for (Cell cell : row) {
                    cells.add(cell.toString());
                }
                data.add(cells);
            }
        }

        logger.debug("readExcel DONE");
        return data;
    }
}
