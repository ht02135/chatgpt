package simple.chatgpt.service.management.file;

import java.io.File;
import java.util.Map;

public interface UserListFileService {

	// ======= 5 CORE METHODS (on top) =======
	public void importListFromCsv(Map<String, Object> params) throws Exception;
	public void exportListToCsv(Map<String, Object> params) throws Exception;
	public void importListFromExcel(Map<String, Object> params) throws Exception;
	public void exportListToExcel(Map<String, Object> params) throws Exception;

	public void exportCsvToFtp(Long listId, File csvFile, File parentDir) throws Exception; 
}
