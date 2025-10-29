package simple.chatgpt.controller.management;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
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

import simple.chatgpt.pojo.management.UserManagementListPojo;
import simple.chatgpt.service.management.PropertyManagementService;
import simple.chatgpt.service.management.UserManagementListService;
import simple.chatgpt.service.management.file.UserListFileService;
import simple.chatgpt.util.PagedResult;
import simple.chatgpt.util.PropertyKey;
import simple.chatgpt.util.Response;
import simple.chatgpt.util.SafeConverter;

@RestController
@RequestMapping(value = "/management/userlists", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserManagementListController {

	private static final Logger logger = LogManager.getLogger(UserManagementListController.class);

	private final UserManagementListService userManagementListService;
	private final PropertyManagementService propertyService;
	private final UserListFileService userListFileService;

	public UserManagementListController(UserManagementListService userManagementListService,
	                                   UserListFileService userListFileService,
	                                   PropertyManagementService propertyService) {
	    this.userManagementListService = userManagementListService;
	    this.userListFileService = userListFileService; // initialize new service
	    this.propertyService = propertyService;
	    logger.debug(
	        "UserManagementListController constructor called, userManagementListService={}, userListFileService={}, propertyService={}",
	        userManagementListService, userListFileService, propertyService);
	}

    // ==============================================================
    // ================ 5 CORE METHODS (on top) =====================
    // ==============================================================

    @PostMapping("/create")
    public ResponseEntity<Response<UserManagementListPojo>> create(
            @RequestParam(required = false) UserManagementListPojo list) {
        logger.debug("create called");
        logger.debug("create list={}", list);

        if (list == null) {
            logger.debug("create: missing list payload");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error("Missing list payload", null, HttpStatus.BAD_REQUEST.value()));
        }

        UserManagementListPojo saved = userManagementListService.create(list);
        return ResponseEntity.ok(Response.success("Created successfully", saved, HttpStatus.OK.value()));
    }

    @PutMapping("/update")
    public ResponseEntity<Response<UserManagementListPojo>> update(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) UserManagementListPojo list) {
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

        // hung: by design, update of user list not implemented
        return ResponseEntity.ok(Response.success("Update not implemented yet", null, HttpStatus.BAD_REQUEST.value()));
    }

    @GetMapping("/search")
    public ResponseEntity<Response<PagedResult<UserManagementListPojo>>> search(
            @RequestParam Map<String, String> params) {
        logger.debug("search called");
        logger.debug("search params={}", params);

        if (!params.containsKey("page")) params.put("page", "0");
        if (!params.containsKey("size")) params.put("size", "20");
        int page = SafeConverter.toIntOrDefault(params.get("page"), 0);
        int size = SafeConverter.toIntOrDefault(params.get("size"), 20);
        int offset = page * size;

        if (!params.containsKey("offset")) params.put("offset", String.valueOf(offset));
        if (!params.containsKey("limit")) params.put("limit", String.valueOf(size));

        if (!params.containsKey("sortField")) params.put("sortField", "id");
        if (!params.containsKey("sortDirection")) params.put("sortDirection", "ASC");
        params.put("sortDirection", params.get("sortDirection").toUpperCase());

        PagedResult<UserManagementListPojo> result = userManagementListService.search(params);
        return ResponseEntity.ok(Response.success("Fetched successfully", result, HttpStatus.OK.value()));
    }

    @GetMapping("/get")
    public ResponseEntity<Response<UserManagementListPojo>> get(
            @RequestParam(required = false) Long id) {
        logger.debug("get called");
        logger.debug("get listId={}", id);

        if (id == null) {
            logger.debug("get: missing id");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error("Missing id parameter", null, HttpStatus.BAD_REQUEST.value()));
        }

        UserManagementListPojo list = userManagementListService.get(id);
        return ResponseEntity.ok(Response.success("Fetched successfully", list, HttpStatus.OK.value()));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Response<Void>> delete(@RequestParam(required = false) Long id) {
        logger.debug("delete called");
        logger.debug("delete id={}", id);

        if (id == null) {
            logger.debug("delete: missing id");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error("Missing id parameter", null, HttpStatus.BAD_REQUEST.value()));
        }

        userManagementListService.delete(id);
        return ResponseEntity.ok(Response.success("Deleted successfully", null, HttpStatus.OK.value()));
    }

    // ======= OTHER METHODS =======

	// ==============================================================
	// ============ EXISTING METHODS (retained below) ===============
	// ==============================================================

	// ------------------ IMPORT LIST ------------------
	@PostMapping("/import")
	public ResponseEntity<Response<UserManagementListPojo>> importList(
		@RequestPart("list") UserManagementListPojo list,
		@RequestPart("file") MultipartFile file) 
	{
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
				userListFileService.importListFromCsv(params);
			} else if (filename.endsWith(".xlsx") || filename.endsWith(".xls")) {
				userListFileService.importListFromExcel(params);
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
	public void exportListToCsv(
		@RequestParam Long listId, 
		HttpServletResponse response) 
	{
		logger.debug("exportListToCsv START");
		logger.debug("exportListToCsv listId={}", listId);

		response.setContentType("text/csv");
		response.setHeader("Content-Disposition", "attachment; filename=\"list_" + listId + ".csv\"");

		try (var os = response.getOutputStream()) {
			Map<String, Object> params = new HashMap<>();
			params.put("listId", listId);
			params.put("outputStream", os);

			userListFileService.exportListToCsv(params);
		} catch (Exception e) {
			logger.error("exportListToCsv failed", e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}

		logger.debug("exportListToCsv DONE");
	}

	@GetMapping("/export/excel")
	public void exportListToExcel(
		@RequestParam Long listId, 
		HttpServletResponse response) 
	{
		logger.debug("exportListToExcel START");
		logger.debug("exportListToExcel listId={}", listId);

		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.setHeader("Content-Disposition", "attachment; filename=\"list_" + listId + ".xlsx\"");

		try (var os = response.getOutputStream()) {
			Map<String, Object> params = new HashMap<>();
			params.put("listId", listId);
			params.put("outputStream", os);
			userListFileService.exportListToExcel(params);

		} catch (Exception e) {
			logger.error("exportListToExcel failed", e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}

		logger.debug("exportListToExcel DONE");
	}

	// ------------------ DOWNLOAD SAMPLE CSV ------------------
	@GetMapping("/download/sample")
	public void downloadSampleCsv(
		HttpServletRequest request, 
		HttpServletResponse response) 
	{
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
}
