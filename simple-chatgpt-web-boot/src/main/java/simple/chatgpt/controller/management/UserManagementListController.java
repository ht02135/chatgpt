package simple.chatgpt.controller.management;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import simple.chatgpt.pojo.management.UserManagementListMemberPojo;
import simple.chatgpt.pojo.management.UserManagementListPojo;
import simple.chatgpt.service.management.PropertyManagementService;
import simple.chatgpt.service.management.UserManagementListService;
import simple.chatgpt.util.PagedResult;
import simple.chatgpt.util.ParamWrapper;
import simple.chatgpt.util.PropertyKey;
import simple.chatgpt.util.Response;
import simple.chatgpt.util.SafeConverter;

@RestController
@RequestMapping(value = "/management/userlists", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserManagementListController {

	private static final Logger logger = LogManager.getLogger(UserManagementListController.class);

	private final UserManagementListService userManagementListService;
	private final PropertyManagementService propertyService;

	public UserManagementListController(UserManagementListService userManagementListService,
			PropertyManagementService propertyService) {
		this.userManagementListService = userManagementListService;
		this.propertyService = propertyService;
		logger.debug(
				"UserManagementListController constructor called, userManagementListService={}, propertyService={}",
				userManagementListService, propertyService);
	}

	// ==============================================================
	// ================ 5 CORE METHODS (on top) =====================
	// ==============================================================

	@PostMapping("/create")
	public ResponseEntity<Response<UserManagementListPojo>> create(
		@RequestParam(required = false) UserManagementListPojo list) 
	{
		logger.debug("create called");
		logger.debug("create list={}", list);

		if (list == null) {
			logger.debug("create: missing list payload");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Response.error("Missing list payload", null, HttpStatus.BAD_REQUEST.value()));
		}

		// Use existing createList if applicable
		return createList(list, null);
	}

	@PutMapping("/update")
	public ResponseEntity<Response<UserManagementListPojo>> update(
		@RequestParam(required = false) Long id,
		@RequestParam(required = false) UserManagementListPojo list) 
	{
		logger.debug("update called");
		logger.debug("update id={}", id);
		logger.debug("update list={}", list);

		if (id == null) {
			logger.debug("update: missing listId");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Response.error("Missing id parameter", null, HttpStatus.BAD_REQUEST.value()));
		}

		if (list == null) {
			logger.debug("update: missing list payload");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Response.error("Missing list payload", null, HttpStatus.BAD_REQUEST.value()));
		}

		// hung: indeed, by design i didnt allow update user list
		return ResponseEntity.ok(Response.success("Update not implemented yet", null, HttpStatus.BAD_REQUEST.value()));
	}

	@GetMapping("/search")
	public ResponseEntity<Response<PagedResult<UserManagementListPojo>>> search(
			@RequestParam Map<String, Object> params) {
		logger.debug("search called");
		logger.debug("search params={}", params);

		// Use existing searchUserLists
		return searchUserLists(params);
	}

	@GetMapping("/get")
	public ResponseEntity<Response<UserManagementListPojo>> get(
		@RequestParam(required = false) Long id) 
	{
		logger.debug("get called");
		logger.debug("get listId={}", id);

		if (id == null) {
			logger.debug("get: missing id");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Response.error("Missing id parameter", null, HttpStatus.BAD_REQUEST.value()));
		}

		// Use existing getListById
		return getListById(id);
	}

	@DeleteMapping("/delete")
	public ResponseEntity<Response<Void>> delete(@RequestParam(required = false) Long listId) {
		logger.debug("delete called");
		logger.debug("delete listId={}", listId);

		if (listId == null) {
			logger.debug("delete: missing id");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Response.error("Missing id parameter", null, HttpStatus.BAD_REQUEST.value()));
		}

		// Use existing deleteList
		return deleteList(listId);
	}

	// ==============================================================
	// ============ EXISTING METHODS (retained below) ===============
	// ==============================================================
	// All your original methods (searchUserLists, createList, getListById,
	// deleteList, etc.)
	// remain unchanged below this point.

	// ------------------ LIST SEARCH ------------------
	@GetMapping
	public ResponseEntity<Response<PagedResult<UserManagementListPojo>>> searchUserLists(
			@RequestParam Map<String, Object> params) {
		logger.debug("searchUserLists START");
		logger.debug("searchUserLists raw params={}", params);

		int page = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "page", 0), 0);
		int size = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "size", 20), 20);
		int offset = page * size;

		String sortField = ParamWrapper.unwrap(params, "sortField", "id");
		String sortDirection = ParamWrapper.unwrap(params, "sortDirection", "ASC").toUpperCase();

		params.put("page", page);
		params.put("size", size);
		params.put("offset", offset);
		params.put("limit", size);
		params.put("sortField", sortField);
		params.put("sortDirection", sortDirection);

		for (Map.Entry<String, Object> entry : params.entrySet()) {
			logger.debug("searchUserLists param {}={}", entry.getKey(), entry.getValue());
		}

		PagedResult<UserManagementListPojo> lists = userManagementListService.searchUserLists(params);
		logger.debug("searchUserLists return={}", lists);
		return ResponseEntity.ok(Response.success("Fetched successfully", lists, HttpStatus.OK.value()));
	}

	// ------------------ CREATE LIST WITH MEMBERS ------------------

	public ResponseEntity<Response<UserManagementListPojo>> createList(@RequestPart("list") UserManagementListPojo list,
			@RequestPart(value = "members", required = false) UserManagementListMemberPojo[] members) {
		logger.debug("createList START");
		logger.debug("createList list={}", list);

		if (members != null) {
			logger.debug("createList members count={}", members.length);
			for (UserManagementListMemberPojo m : members) {
				logger.debug("createList member={}", m);
			}
		} else {
			logger.debug("createList members=null");
		}

		try {
			Map<String, Object> params = new HashMap<>();
			params.put("list", list);
			params.put("members", members != null ? Arrays.asList(members) : null);
			logger.debug("createList params={}", params);
			userManagementListService.createList(params);
		} catch (Exception e) {
			logger.error("createList failed", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Response.error("Create failed: " + e.getMessage(), null, 500));
		}

		logger.debug("createList return={}", list);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(Response.success("List created successfully", list, HttpStatus.CREATED.value()));
	}

	// ------------------ GET LIST BY ID ------------------

	public ResponseEntity<Response<UserManagementListPojo>> getListById(@RequestParam Long listId) {
		logger.debug("getListById START");
		logger.debug("getListById listId={}", listId);

		Map<String, Object> params = new HashMap<>();
		params.put("listId", listId);

		UserManagementListPojo list = userManagementListService.getListById(params);
		if (list == null) {
			logger.debug("getListById List not found");
			return ResponseEntity.ok(Response.error("List not found", null, HttpStatus.NOT_FOUND.value()));
		}

		logger.debug("getListById return={}", list);
		return ResponseEntity.ok(Response.success("List fetched successfully", list, HttpStatus.OK.value()));
	}

	// ------------------ GET MEMBERS BY LIST ------------------
	@GetMapping("/members")
	public ResponseEntity<Response<PagedResult<UserManagementListMemberPojo>>> getMembersByListId(
			@RequestParam Long listId) {
		logger.debug("getMembersByListId START");
		logger.debug("getMembersByListId listId={}", listId);

		Map<String, Object> params = new HashMap<>();
		params.put("listId", listId);

		PagedResult<UserManagementListMemberPojo> members = userManagementListService.getMembersByListId(params);

		logger.debug("getMembersByListId return={}", members);
		return ResponseEntity.ok(Response.success("Members fetched successfully", members, HttpStatus.OK.value()));
	}

	// ------------------ DELETE LIST ------------------

	public ResponseEntity<Response<Void>> deleteList(@RequestParam Long listId) {
		logger.debug("deleteList START");
		logger.debug("deleteList listId={}", listId);

		Map<String, Object> params = new HashMap<>();
		params.put("listId", listId);

		userManagementListService.deleteList(params);

		logger.debug("deleteList DONE");
		return ResponseEntity.ok(Response.success("List deleted successfully", null, HttpStatus.OK.value()));
	}

	// ------------------ IMPORT LIST ------------------
	@PostMapping("/import")
	public ResponseEntity<Response<UserManagementListPojo>> importList(@RequestPart("list") UserManagementListPojo list,
			@RequestPart("file") MultipartFile file) {
		logger.debug("importList START");
		logger.debug("importList list={}", list);
		logger.debug("importList fileName={}", file.getOriginalFilename());

		try (var is = file.getInputStream()) {
			String filename = file.getOriginalFilename().toLowerCase();
			Map<String, Object> params = new HashMap<>();
			params.put("list", list);
			params.put("inputStream", is);
			params.put("originalFileName", file.getOriginalFilename());

			if (filename.endsWith(".csv")) {
				userManagementListService.importListFromCsv(params);
			} else if (filename.endsWith(".xlsx") || filename.endsWith(".xls")) {
				userManagementListService.importListFromExcel(params);
			} else {
				return ResponseEntity.badRequest()
						.body(Response.error("Unsupported file type", null, HttpStatus.BAD_REQUEST.value()));
			}
		} catch (Exception e) {
			logger.error("importList failed", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
					Response.error("Import failed: " + e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
		}

		logger.debug("importList return={}", list);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(Response.success("List imported successfully", list, HttpStatus.CREATED.value()));
	}

	// ------------------ EXPORT LIST ------------------
	@GetMapping("/export/csv")
	public void exportListToCsv(@RequestParam Long listId, HttpServletResponse response) {
		logger.debug("exportListToCsv START");
		logger.debug("exportListToCsv listId={}", listId);

		response.setContentType("text/csv");
		response.setHeader("Content-Disposition", "attachment; filename=\"list_" + listId + ".csv\"");

		try (var os = response.getOutputStream()) {
			Map<String, Object> params = new HashMap<>();
			params.put("listId", listId);
			params.put("outputStream", os);

			userManagementListService.exportListToCsv(params);
		} catch (Exception e) {
			logger.error("exportListToCsv failed", e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}

		logger.debug("exportListToCsv DONE");
	}

	@GetMapping("/export/excel")
	public void exportListToExcel(@RequestParam Long listId, HttpServletResponse response) {
		logger.debug("exportListToExcel START");
		logger.debug("exportListToExcel listId={}", listId);

		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.setHeader("Content-Disposition", "attachment; filename=\"list_" + listId + ".xlsx\"");

		try (var os = response.getOutputStream()) {
			Map<String, Object> params = new HashMap<>();
			params.put("listId", listId);
			params.put("outputStream", os);
			userManagementListService.exportListToExcel(params);

		} catch (Exception e) {
			logger.error("exportListToExcel failed", e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}

		logger.debug("exportListToExcel DONE");
	}

	// ------------------ DOWNLOAD SAMPLE CSV ------------------
	@GetMapping("/download/sample")
	public void downloadSampleCsv(HttpServletRequest request, HttpServletResponse response) {
		logger.debug("downloadSampleCsv START");

		String sampleCSVRelativePath = "/management/data/user_lists/test_user_lists_1.csv";

		try {
			sampleCSVRelativePath = propertyService.getString(PropertyKey.SAMPLE_CSV_RELATIVE_PATH);
		} catch (Exception e) {
			logger.error("downloadSampleCsv failed to get property, using default", e);
		}

		try {
			String absolutePath = request.getServletContext().getRealPath(sampleCSVRelativePath);

			File file = new File(absolutePath);
			if (!file.exists()) {
				logger.error("downloadSampleCsv file not found at {}", absolutePath);
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				return;
			}

			response.setContentType("text/csv");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");

			try (InputStream is = new FileInputStream(file); OutputStream os = response.getOutputStream()) {
				is.transferTo(os);
				os.flush();
			}
		} catch (Exception e) {
			logger.error("downloadSampleCsv failed", e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}

		logger.debug("downloadSampleCsv DONE");
	}

	// ------------------ SEARCH MEMBERS ------------------
	@GetMapping("/members/search")
	public ResponseEntity<Response<PagedResult<UserManagementListMemberPojo>>> searchMembers(
			@RequestParam Map<String, Object> params) {
		logger.debug("searchMembers START");
		logger.debug("searchMembers raw params={}", params);

		int page = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "page", 0), 0);
		int size = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "size", 20), 20);
		int offset = page * size;

		String sortField = ParamWrapper.unwrap(params, "sortField", "id");
		String sortDirection = ParamWrapper.unwrap(params, "sortDirection", "ASC").toUpperCase();

		Map<String, Object> serviceParams = new HashMap<>(params);
		serviceParams.put("page", page);
		serviceParams.put("size", size);
		serviceParams.put("offset", offset);
		serviceParams.put("limit", size);
		serviceParams.put("sortField", sortField);
		serviceParams.put("sortDirection", sortDirection);

		PagedResult<UserManagementListMemberPojo> members = userManagementListService.searchMembers(serviceParams);

		logger.debug("searchMembers return={}", members);
		return ResponseEntity.ok(Response.success("Members fetched successfully", members, HttpStatus.OK.value()));
	}

	@GetMapping("/members/count")
	public ResponseEntity<Response<Long>> countMembers(@RequestParam Map<String, Object> params) {
		logger.debug("countMembers START");
		logger.debug("countMembers raw params={}", params);

		Map<String, Object> serviceParams = new HashMap<>(params);
		serviceParams.put("listId", ParamWrapper.unwrap(params, "listId"));

		long count = userManagementListService.countMembers(serviceParams);

		logger.debug("countMembers return={}", count);
		return ResponseEntity.ok(Response.success("Count fetched successfully", count, HttpStatus.OK.value()));
	}
}
