package simple.chatgpt.controller.management;

import java.util.Map;

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

@RestController
@RequestMapping(value = "/management/properties", produces = MediaType.APPLICATION_JSON_VALUE)
public class PropertyManagementController {

    private final PropertyManagementService propertyService;

    public PropertyManagementController(PropertyManagementService propertyService) {
        this.propertyService = propertyService;
    }

    // 🔎 LIST / SEARCH
    @GetMapping
    public ResponseEntity<Response<PagedResult<PropertyManagementPojo>>> searchProperties(
            @RequestParam Map<String, String> params
    ) {
        int page = Integer.parseInt(params.getOrDefault("page", "0"));
        int size = Integer.parseInt(params.getOrDefault("size", "20"));
        int offset = page * size;

        params.put("offset", String.valueOf(offset));
        params.put("limit", String.valueOf(size));
        params.put("sortField", params.getOrDefault("sortField", "id"));
        params.put("sortDirection", params.getOrDefault("sortDirection", "asc"));

        PagedResult<PropertyManagementPojo> result = propertyService.searchProperties(params);
        return ResponseEntity.ok(Response.success("Fetched successfully", result, HttpStatus.OK.value()));
    }

    // 📖 READ (Flexible key)
    @GetMapping("/get")
    public ResponseEntity<Response<PropertyManagementPojo>> getProperty(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String propertyName,
            @RequestParam(required = false) String propertyKey
    ) {
        PropertyManagementPojo property = null;

        if (id != null) {
            property = propertyService.getPropertyById(id);
        } else if (propertyName != null) {
            property = propertyService.getByPropertyName(propertyName);
        } else if (propertyKey != null) {
            property = propertyService.getByPropertyKey(propertyKey);
        }

        if (property == null) {
            return ResponseEntity.ok(Response.error("Property not found", null, HttpStatus.NOT_FOUND.value()));
        }

        return ResponseEntity.ok(Response.success("Fetched successfully", property, HttpStatus.OK.value()));
    }

    // ➕ CREATE
    @PostMapping("/create")
    public ResponseEntity<Response<PropertyManagementPojo>> createProperty(@RequestBody PropertyManagementPojo property) {
        PropertyManagementPojo created = propertyService.createProperty(property);
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
            @RequestBody PropertyManagementPojo property
    ) {
        PropertyManagementPojo updated = null;

        if (id != null) {
            updated = propertyService.updatePropertyById(id, property);
        } else if (propertyName != null) {
            updated = propertyService.updatePropertyByPropertyName(propertyName, property);
        } else if (propertyKey != null) {
            updated = propertyService.updatePropertyByPropertyKey(propertyKey, property);
        } else {
            return ResponseEntity.ok(Response.error(
                    "At least one key must be provided for update", null, HttpStatus.BAD_REQUEST.value()));
        }

        return ResponseEntity.ok(Response.success("Property updated successfully", updated, HttpStatus.OK.value()));
    }

    // 🗑 DELETE (Flexible key)
    @DeleteMapping("/delete")
    public ResponseEntity<Response<Void>> deleteProperty(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String propertyName,
            @RequestParam(required = false) String propertyKey
    ) {
        if (id != null) {
            propertyService.deletePropertyById(id);
        } else if (propertyName != null) {
            propertyService.deletePropertyByPropertyName(propertyName);
        } else if (propertyKey != null) {
            propertyService.deletePropertyByPropertyKey(propertyKey);
        } else {
            return ResponseEntity.ok(Response.error(
                    "At least one key must be provided for delete", null, HttpStatus.BAD_REQUEST.value()));
        }

        return ResponseEntity.ok(Response.success("Property deleted successfully", null, HttpStatus.OK.value()));
    }
}
