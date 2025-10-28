package simple.chatgpt.service.management.file;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

/*
hung: this class is generic CSV handler used by multiple services
*/
public class CsvFileServiceImpl implements CsvFileService {

    private static final Logger logger = LogManager.getLogger(CsvFileServiceImpl.class);

    @Override
    public void writeCsv(List<String> headers, List<List<String>> rows, OutputStream os) throws Exception {
        logger.debug("writeCsv START");
        logger.debug("writeCsv headers={}", headers);
        logger.debug("writeCsv rows={}", rows);
        logger.debug("writeCsv os={}", os);

        try (CSVWriter writer = new CSVWriter(new java.io.OutputStreamWriter(os))) {
            if (headers != null) {
                writer.writeNext(headers.toArray(new String[0]));
            }
            if (rows != null) {
                for (List<String> row : rows) {
                    writer.writeNext(row.toArray(new String[0]));
                }
            }
        }

        logger.debug("writeCsv DONE");
    }

    @Override
    public List<List<String>> readCsv(InputStream is) throws Exception {
        logger.debug("readCsv START");
        logger.debug("readCsv is={}", is);

        List<List<String>> data = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new java.io.InputStreamReader(is))) {
            String[] line;
            while ((line = reader.readNext()) != null) {
                data.add(Arrays.asList(line));
            }
        }

        logger.debug("readCsv DONE");
        return data;
    }
}
