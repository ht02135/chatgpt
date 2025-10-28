package simple.chatgpt.service.management;

import java.util.Map;

public interface ObsoleteUserListFileService {

	// ======= 5 CORE METHODS (on top) =======
	public void importListFromCsv(Map<String, Object> params) throws Exception;
	public void exportListToCsv(Map<String, Object> params) throws Exception;
	public void importListFromExcel(Map<String, Object> params) throws Exception;
	public void exportListToExcel(Map<String, Object> params) throws Exception;

}
