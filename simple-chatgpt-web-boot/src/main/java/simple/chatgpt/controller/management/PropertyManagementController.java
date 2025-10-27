package simple.chatgpt.controller.management;

import java.util.Map;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import simple.chatgpt.pojo.management.PropertyManagementPojo;
import simple.chatgpt.service.management.PropertyManagementService;
import simple.chatgpt.util.PagedResult;
import simple.chatgpt.util.ParamWrapper;
import simple.chatgpt.util.Response;
import simple.chatgpt.util.SafeConverter;

/*
1>Entity/DTO validation (@RequestBody) → just @Valid on the 
method parameter.
2>Parameter validation (@RequestParam, @PathVariable, etc.) 
→ requires @Validated on the controller (or service) class.

So unless you’re going to validate query params like @NotBlank
or  @NotNull String propertyKey, you don’t need @Validated on 
your controller right now.
*/

@RestController
@RequestMapping(value = "/management/properties", produces = MediaType.APPLICATION_JSON_VALUE)
public class PropertyManagementController {

	private static final Logger logger = LogManager.getLogger(PropertyManagementController.class);

	private final PropertyManagementService propertyService;

	public PropertyManagementController(PropertyManagementService propertyService) {
		this.propertyService = propertyService;
		logger.debug("PropertyManagementController constructor called");
		logger.debug("PropertyManagementController propertyService={}", propertyService);
	}

    // ==============================================================
    // ================ 5 CORE METHODS (on top) =====================
    // ==============================================================

    @PostMapping("/create")
    public ResponseEntity<Response<PropertyManagementPojo>> create(
            @Valid @RequestBody PropertyManagementPojo property) {
        logger.debug("create called");
        logger.debug("create property={}", property);

        if (property == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error("Missing property payload", null, HttpStatus.BAD_REQUEST.value()));
        }

        PropertyManagementPojo saved = propertyService.create(property);
        return ResponseEntity.ok(Response.success("Created successfully", saved, HttpStatus.OK.value()));
    }

    @PutMapping("/update")
    public ResponseEntity<Response<PropertyManagementPojo>> update(
            @RequestParam(required = false) Long id,
            @Valid @RequestBody PropertyManagementPojo property) {
        logger.debug("update called");
        logger.debug("update id={}", id);
        logger.debug("update property={}", property);

        if (id == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error("Missing id parameter", null, HttpStatus.BAD_REQUEST.value()));
        }
        if (property == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error("Missing property payload", null, HttpStatus.BAD_REQUEST.value()));
        }

        PropertyManagementPojo updated = propertyService.update(id, property);
        return ResponseEntity.ok(Response.success("Updated successfully", updated, HttpStatus.OK.value()));
    }

    @GetMapping("/search")
    public ResponseEntity<Response<PagedResult<PropertyManagementPojo>>> search(
            @RequestParam Map<String, String> params) {
        logger.debug("search called");
        logger.debug("search params={}", params);

        // Add defaults for paging & sorting, only params used in XML
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

        PagedResult<PropertyManagementPojo> result = propertyService.search(params);
        return ResponseEntity.ok(Response.success("Fetched successfully", result, HttpStatus.OK.value()));
    }

    @GetMapping("/get")
    public ResponseEntity<Response<PropertyManagementPojo>> get(
            @RequestParam(required = false) Long id) {
        logger.debug("get called");
        logger.debug("get id={}", id);

        if (id == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error("Missing id parameter", null, HttpStatus.BAD_REQUEST.value()));
        }

        PropertyManagementPojo property = propertyService.get(id);
        return ResponseEntity.ok(Response.success("Fetched successfully", property, HttpStatus.OK.value()));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Response<Void>> delete(@RequestParam(required = false) Long id) {
        logger.debug("delete called");
        logger.debug("delete id={}", id);

        if (id == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error("Missing id parameter", null, HttpStatus.BAD_REQUEST.value()));
        }

        propertyService.delete(id);
        return ResponseEntity.ok(Response.success("Deleted successfully", null, HttpStatus.OK.value()));
    }

    // ======= OTHER METHODS =======

}
