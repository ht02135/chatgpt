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
    public ResponseEntity<Response<PropertyManagementPojo>> create(@Valid @RequestBody PropertyManagementPojo property) {
        logger.debug("create called");
        logger.debug("create property={}", property);

        if (property == null) {
            throw new IllegalArgumentException("Property payload must not be null");
        }

        return createProperty(property);
    }

    @PutMapping("/update")
    public ResponseEntity<Response<PropertyManagementPojo>> update(
            @RequestParam(required = false) Long id,
            @Valid @RequestBody PropertyManagementPojo property) {
        logger.debug("update called");
        logger.debug("update id={}", id);
        logger.debug("update property={}", property);

        if (id == null) {
            throw new IllegalArgumentException("ID must be provided for update");
        }
        if (property == null) {
            throw new IllegalArgumentException("Property payload must not be null");
        }

        return updateProperty(id, null, null, property);
    }

    @GetMapping("/search")
    public ResponseEntity<Response<PagedResult<PropertyManagementPojo>>> search(@RequestParam Map<String, String> params) {
        logger.debug("search called");
        logger.debug("search params={}", params);

        return searchProperties(params);
    }

    @GetMapping("/get")
    public ResponseEntity<Response<PropertyManagementPojo>> get(@RequestParam(required = false) Long id) {
        logger.debug("get called");
        logger.debug("get id={}", id);

        if (id == null) {
            throw new IllegalArgumentException("ID must be provided for get");
        }

        return getProperty(id, null, null);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Response<Void>> delete(@RequestParam(required = false) Long id) {
        logger.debug("delete called");
        logger.debug("delete id={}", id);

        if (id == null) {
            throw new IllegalArgumentException("ID must be provided for delete");
        }

        return deleteProperty(id, null, null);
    }

    // ==============================================================
    // ============ EXISTING METHODS (retained below) ===============
    // ==============================================================

    /*
     * hung: existing methods moved below — URL mappings removed or commented out
     * to avoid collision with 5 core API endpoints above.
     */

    // ------------------ LIST / SEARCH ------------------
    // @GetMapping
    public ResponseEntity<Response<PagedResult<PropertyManagementPojo>>> searchProperties(
            @RequestParam Map<String, String> params
    ) {
        logger.debug("searchProperties START");
        logger.debug("searchProperties raw params={}", params);

        // hung: DONT REMOVE THIS CODE
        int page = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "page", 0), 0);
        int size = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "size", 20), 20);
        int offset = page * size;

        params.put("offset", String.valueOf(offset));
        params.put("limit", String.valueOf(size));
        params.put("sortField", ParamWrapper.unwrap(params, "sortField", "key"));
        params.put("sortDirection", ParamWrapper.unwrap(params, "sortDirection", "asc"));

        PagedResult<PropertyManagementPojo> result = propertyService.searchProperties(params);

        logger.debug("searchProperties result={}", result);
        return ResponseEntity.ok(Response.success("Fetched successfully", result, HttpStatus.OK.value()));
    }

    // ------------------ READ ------------------
    // @GetMapping("/get")
    public ResponseEntity<Response<PropertyManagementPojo>> getProperty(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String propertyName,
            @RequestParam(required = false) String propertyKey
    ) {
        logger.debug("getProperty START");
        logger.debug("getProperty id={}", id);
        logger.debug("getProperty propertyName={}", propertyName);
        logger.debug("getProperty propertyKey={}", propertyKey);

        PropertyManagementPojo property = null;

        if (id != null) {
            property = propertyService.getPropertyById(id);
        } else if (propertyName != null) {
            property = propertyService.getByPropertyName(propertyName);
        } else if (propertyKey != null) {
            property = propertyService.getByPropertyKey(propertyKey);
        }

        if (property == null) {
            logger.debug("getProperty: Property not found");
            return ResponseEntity.ok(Response.error("Property not found", null, HttpStatus.NOT_FOUND.value()));
        }

        logger.debug("getProperty result={}", property);
        return ResponseEntity.ok(Response.success("Fetched successfully", property, HttpStatus.OK.value()));
    }

    // ------------------ CREATE ------------------
    // @PostMapping("/create")
    public ResponseEntity<Response<PropertyManagementPojo>> createProperty(
            @Valid @RequestBody PropertyManagementPojo property
    ) {
        logger.debug("createProperty START");
        logger.debug("createProperty property={}", property);

        PropertyManagementPojo created = propertyService.createProperty(property);

        logger.debug("createProperty result={}", created);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.success("Property created successfully", created, HttpStatus.CREATED.value()));
    }

    // ------------------ UPDATE ------------------
    // @PutMapping("/update")
    public ResponseEntity<Response<PropertyManagementPojo>> updateProperty(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String propertyName,
            @RequestParam(required = false) String propertyKey,
            @Valid @RequestBody PropertyManagementPojo property
    ) {
        logger.debug("updateProperty START");
        logger.debug("updateProperty id={}", id);
        logger.debug("updateProperty propertyName={}", propertyName);
        logger.debug("updateProperty propertyKey={}", propertyKey);
        logger.debug("updateProperty property={}", property);

        PropertyManagementPojo updated = null;

        if (id != null) {
            updated = propertyService.updatePropertyById(id, property);
        } else if (propertyName != null) {
            updated = propertyService.updatePropertyByPropertyName(propertyName, property);
        } else if (propertyKey != null) {
            updated = propertyService.updatePropertyByPropertyKey(propertyKey, property);
        } else {
            logger.debug("updateProperty: No key provided");
            return ResponseEntity.ok(Response.error("At least one key must be provided for update", null, HttpStatus.BAD_REQUEST.value()));
        }

        logger.debug("updateProperty result={}", updated);
        return ResponseEntity.ok(Response.success("Property updated successfully", updated, HttpStatus.OK.value()));
    }

    // ------------------ DELETE ------------------
    // @DeleteMapping("/delete")
    public ResponseEntity<Response<Void>> deleteProperty(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String propertyName,
            @RequestParam(required = false) String propertyKey
    ) {
        logger.debug("deleteProperty START");
        logger.debug("deleteProperty id={}", id);
        logger.debug("deleteProperty propertyName={}", propertyName);
        logger.debug("deleteProperty propertyKey={}", propertyKey);

        if (id != null) {
            propertyService.deletePropertyById(id);
        } else if (propertyName != null) {
            propertyService.deletePropertyByPropertyName(propertyName);
        } else if (propertyKey != null) {
            propertyService.deletePropertyByPropertyKey(propertyKey);
        } else {
            logger.debug("deleteProperty: No key provided");
            return ResponseEntity.ok(Response.error("At least one key must be provided for delete", null, HttpStatus.BAD_REQUEST.value()));
        }

        logger.debug("deleteProperty DONE");
        return ResponseEntity.ok(Response.success("Property deleted successfully", null, HttpStatus.OK.value()));
    }
}
