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
import simple.chatgpt.util.Response;

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
    }

    // 🔎 LIST / SEARCH
    @GetMapping
    public ResponseEntity<Response<PagedResult<PropertyManagementPojo>>> searchProperties(
            @RequestParam Map<String, String> params
    ) {
        logger.debug("searchProperties called with params={}", params);

        int page = Integer.parseInt(params.getOrDefault("page", "0"));
        int size = Integer.parseInt(params.getOrDefault("size", "20"));
        int offset = page * size;

        params.put("offset", String.valueOf(offset));
        params.put("limit", String.valueOf(size));
        params.put("sortField", params.getOrDefault("sortField", "key"));
        params.put("sortDirection", params.getOrDefault("sortDirection", "asc"));

        PagedResult<PropertyManagementPojo> result = propertyService.searchProperties(params);
        logger.debug("searchProperties result={}", result);

        return ResponseEntity.ok(Response.success("Fetched successfully", result, HttpStatus.OK.value()));
    }

    // 📖 READ (Flexible key)
    @GetMapping("/get")
    public ResponseEntity<Response<PropertyManagementPojo>> getProperty(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String propertyName,
            @RequestParam(required = false) String propertyKey
    ) {
        logger.debug("getProperty called with id={}, propertyName={}, propertyKey={}", id, propertyName, propertyKey);

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

    // ➕ CREATE
    @PostMapping("/create")
    public ResponseEntity<Response<PropertyManagementPojo>> createProperty(
            @Valid @RequestBody PropertyManagementPojo property
    ) {
        logger.debug("#############");
        logger.debug("createProperty called with property={}", property);

        PropertyManagementPojo created = propertyService.createProperty(property);

        logger.debug("createProperty result={}", created);
        logger.debug("#############");

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Response.success("Property created successfully", created, HttpStatus.CREATED.value()));
    }

    // ✏️ UPDATE (Flexible key)
    @PutMapping("/update")
    public ResponseEntity<Response<PropertyManagementPojo>> updateProperty(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String propertyName,
            @RequestParam(required = false) String propertyKey,
            @Valid @RequestBody PropertyManagementPojo property
    ) {
        logger.debug("updateProperty called with id={}, propertyName={}, propertyKey={}, property={}", id, propertyName, propertyKey, property);

        PropertyManagementPojo updated = null;
        logger.debug("#############");
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
        logger.debug("#############");

        return ResponseEntity.ok(Response.success("Property updated successfully", updated, HttpStatus.OK.value()));
    }

    // 🗑 DELETE (Flexible key)
    @DeleteMapping("/delete")
    public ResponseEntity<Response<Void>> deleteProperty(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String propertyName,
            @RequestParam(required = false) String propertyKey
    ) {
        logger.debug("deleteProperty called with id={}, propertyName={}, propertyKey={}", id, propertyName, propertyKey);

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

        logger.debug("deleteProperty: success");
        return ResponseEntity.ok(Response.success("Property deleted successfully", null, HttpStatus.OK.value()));
    }
}
