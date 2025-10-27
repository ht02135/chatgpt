package simple.chatgpt.controller.mybatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import simple.chatgpt.config.mybatis.loader.ConfigLoader;
import simple.chatgpt.pojo.mybatis.MyBatisProperty;
import simple.chatgpt.pojo.mybatis.MyBatisUserUser;
import simple.chatgpt.service.mybatis.MyBatisUserService;
import simple.chatgpt.service.mybatis.PropertyService;
import simple.chatgpt.util.PropertyKey;
import simple.chatgpt.util.Response;

@RestController
@RequestMapping("/mybatis/data")
public class UnifiedController {

    private static final Logger logger = LogManager.getLogger(UnifiedController.class);

    private final MyBatisUserService myBatisUserService;
    private final PropertyService propertyService;
    private final ConfigLoader configLoader = new ConfigLoader();

    @Autowired
    public UnifiedController(MyBatisUserService myBatisUserService,
                             PropertyService propertyService) {
        this.myBatisUserService = myBatisUserService;
        this.propertyService = propertyService;
    }

    //----------------------------------------
    // 1) Save / Add
    @PostMapping(value = "/add", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> save(@RequestParam String type, @RequestBody Map<String, Object> payload) {
        logger.debug("save: type={}, payload={}", type, payload);

        switch (type.toLowerCase()) {
            case "user":
                MyBatisUserUser user = new ObjectMapper().convertValue(payload, MyBatisUserUser.class);
                boolean isUpdate = user.getId() > 0;
                MyBatisUserUser savedUser = myBatisUserService.save(user);
                if (isUpdate) {
                    return ResponseEntity.ok(Response.success("User updated successfully", savedUser, HttpStatus.OK.value()));
                } else {
                    return ResponseEntity.status(HttpStatus.CREATED).body(
                            Response.success("User created successfully", savedUser, HttpStatus.CREATED.value()));
                }

            case "property":
            	// property definitely not support /add by design
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                        .body(Response.error("Add not implemented for property", null, HttpStatus.NOT_IMPLEMENTED.value()));

            case "config":
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                        .body(Response.error("Add not implemented for config", null, HttpStatus.NOT_IMPLEMENTED.value()));

            default:
                return ResponseEntity.badRequest().body(Response.error("Unknown type", null, HttpStatus.BAD_REQUEST.value()));
        }
    }

    //----------------------------------------
    // 2) Update
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateById(@RequestParam String type, @PathVariable int id, @RequestBody Map<String, Object> payload) {
        logger.debug("updateById: type={}, id={}, payload={}", type, id, payload);

        switch (type.toLowerCase()) {
            case "user":
                MyBatisUserUser user = new ObjectMapper().convertValue(payload, MyBatisUserUser.class);
                user.setId(id);
                MyBatisUserUser existingUser = myBatisUserService.get(id);
                if (existingUser == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Response.error("User not found", null, HttpStatus.NOT_FOUND.value()));
                }
                MyBatisUserUser updatedUser = myBatisUserService.save(user);
                return ResponseEntity.ok(Response.success("User updated successfully", updatedUser, HttpStatus.OK.value()));

            case "property":
            	// property definitely not support update by /{id} by design
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                        .body(Response.error("Update by ID not implemented for property", null, HttpStatus.NOT_IMPLEMENTED.value()));

            case "config":
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                        .body(Response.error("Update by ID not implemented for config", null, HttpStatus.NOT_IMPLEMENTED.value()));

            default:
                return ResponseEntity.badRequest().body(Response.error("Unknown type", null, HttpStatus.BAD_REQUEST.value()));
        }
    }

    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@RequestParam String type, @RequestBody Map<String, String> payload) {
        logger.debug("update: type={}, payload={}", type, payload);

        switch (type.toLowerCase()) {
            case "property":
                try {
                    String key = payload.get("key");
                    String value = payload.get("value");
                    PropertyKey propertyKey = PropertyKey.fromKey(key);
                    propertyService.updateProperty(propertyKey, value);
                    return ResponseEntity.ok(Response.success("Property updated successfully", null, HttpStatus.OK.value()));
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Response.error("Invalid property key", null, HttpStatus.BAD_REQUEST.value()));
                }

            case "user":
            	// user definitely not support update by key by design
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                        .body(Response.error("Update (POST /update) not implemented for user", null, HttpStatus.NOT_IMPLEMENTED.value()));

            case "config":
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                        .body(Response.error("Update (POST /update) not implemented for config", null, HttpStatus.NOT_IMPLEMENTED.value()));

            default:
                return ResponseEntity.badRequest().body(Response.error("Unknown type", null, HttpStatus.BAD_REQUEST.value()));
        }
    }

    //----------------------------------------
    // 3) Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@RequestParam String type, @PathVariable int id) {
        logger.debug("delete: type={}, id={}", type, id);

        switch (type.toLowerCase()) {
            case "user":
                myBatisUserService.delete(id);
                return ResponseEntity.ok(Response.success("User deleted successfully", null, HttpStatus.OK.value()));

            case "property":
            	// user definitely not support delete by design
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                        .body(Response.error("Delete not implemented for property", null, HttpStatus.NOT_IMPLEMENTED.value()));

            case "config":
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                        .body(Response.error("Delete not implemented for config", null, HttpStatus.NOT_IMPLEMENTED.value()));

            default:
                return ResponseEntity.badRequest().body(Response.error("Unknown type", null, HttpStatus.BAD_REQUEST.value()));
        }
    }

    //----------------------------------------
    // 4) Get by ID or Key
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@RequestParam String type, @PathVariable int id) {
        logger.debug("getById: type={}, id={}", type, id);

        switch (type.toLowerCase()) {
            case "user":
                MyBatisUserUser user = myBatisUserService.get(id);
                if (user == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Response.error("User not found", null, HttpStatus.NOT_FOUND.value()));
                }
                return ResponseEntity.ok(Response.success("User retrieved successfully", user, HttpStatus.OK.value()));

            case "property":
            	// property definitely not support get by /{id} by design
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                        .body(Response.error("Get by ID not implemented for property (use /by-key)", null, HttpStatus.NOT_IMPLEMENTED.value()));

            case "config":
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                        .body(Response.error("Get by ID not implemented for config", null, HttpStatus.NOT_IMPLEMENTED.value()));

            default:
                return ResponseEntity.badRequest().body(Response.error("Unknown type", null, HttpStatus.BAD_REQUEST.value()));
        }
    }

    @GetMapping(value = "/by-key/{key}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getByKey(@RequestParam String type, @PathVariable String key) {
        logger.debug("getByKey: type={}, key={}", type, key);

        switch (type.toLowerCase()) {
            case "property":
                MyBatisProperty property = propertyService.getAllProperties().stream()
                        .filter(p -> p.getKey().equals(key))
                        .findFirst()
                        .orElse(null);
                if (property == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Response.error("Property not found", null, HttpStatus.NOT_FOUND.value()));
                }
                return ResponseEntity.ok(Response.success("Property retrieved", property, HttpStatus.OK.value()));

            case "user":
            	// user definitely not support get by /by-key/{key} by design
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                        .body(Response.error("Get by key not implemented for user", null, HttpStatus.NOT_IMPLEMENTED.value()));

            case "config":
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                        .body(Response.error("Get by key not implemented for config", null, HttpStatus.NOT_IMPLEMENTED.value()));

            default:
                return ResponseEntity.badRequest().body(Response.error("Unknown type", null, HttpStatus.BAD_REQUEST.value()));
        }
    }

    //----------------------------------------
    // 5) Get All
    @GetMapping("/all")
    public ResponseEntity<?> getAll(@RequestParam String type,
            						@RequestParam(value = "page", required = false, defaultValue = "1") int page,
            						@RequestParam(value = "size", required = false, defaultValue = "10") int size,
            						@RequestParam(value = "sort", required = false, defaultValue = "key") String sort,
            						@RequestParam(value = "order", required = false, defaultValue = "ASC") String order,
                                    @RequestParam(value = "key", required = false) String key,
                                    @RequestParam(value = "type", required = false) String propertyType) {
        logger.debug("getAll: type={}", type);

        switch (type.toLowerCase()) {
            case "user":
                List<MyBatisUserUser> users = myBatisUserService.getAll();
                return ResponseEntity.ok(Response.success("Users retrieved successfully", users, HttpStatus.OK.value()));
            
            case "property":
            	// this has key and type specifically for property
                List<MyBatisProperty> properties = propertyService.getProperties(key, propertyType, page, size, sort, order);
                int total = propertyService.countProperties(key, propertyType);
                int maxPage = (int) Math.ceil((double) total / size);
                Map<String, Object> data = new HashMap<>();
                data.put("properties", properties);
                data.put("total", total);
                data.put("page", page);
                data.put("size", size);
                data.put("maxPage", maxPage);
                return ResponseEntity.ok(Response.success("Properties retrieved successfully", data, HttpStatus.OK.value()));

            case "config":
                try {
                    Map<String, Object> configMap = Map.of(
                            "grids", configLoader.loadGrids(),
                            "forms", configLoader.loadForms(),
                            "regex", configLoader.loadRegexes(),
                            "actions", configLoader.loadActionGroups(),
                            "validators", configLoader.loadValidators()
                    );
                    return ResponseEntity.ok(Response.success("Config loaded", configMap, HttpStatus.OK.value()));
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(Response.error("Failed to load config", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
                }
                
            default:
                return ResponseEntity.badRequest().body(Response.error("Unknown type", null, HttpStatus.BAD_REQUEST.value()));
        }
    }

    //----------------------------------------
    // 6) Get Paged
    @GetMapping(value = "/paged", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getPagedFiltered(@RequestParam String type,
                                              @RequestParam(required = false, defaultValue = "1") int page,
                                              @RequestParam(required = false, defaultValue = "10") int size,
                                              @RequestParam(required = false, defaultValue = "id") String sortField,
                                              @RequestParam(required = false, defaultValue = "ASC") String sortOrder,
                                              @RequestParam(required = false) String firstName,
                                              @RequestParam(required = false) String lastName,
                                              @RequestParam(required = false) String email,
                                              @RequestParam(required = false) String addressLine1,
                                              @RequestParam(required = false) String addressLine2,
                                              @RequestParam(required = false) String city,
                                              @RequestParam(required = false) String state,
                                              @RequestParam(required = false) String country) {
        logger.debug("getPagedFiltered: type={}", type);

        switch (type.toLowerCase()) {
            case "user":
            	// this has firstname blabla specifically for user
                List<MyBatisUserUser> users = myBatisUserService.getUsersPagedFiltered(
                        page, size, sortField, sortOrder,
                        firstName, lastName, email,
                        addressLine1, addressLine2, city, state, country);
                int total = myBatisUserService.getTotalUserCountFiltered(firstName, lastName, email, addressLine1, addressLine2, city, state, country);
                Map<String, Object> data = new HashMap<>();
                data.put("users", users);
                data.put("total", total);
                return ResponseEntity.ok(Response.success("Paged users fetched", data, HttpStatus.OK.value()));

            case "property":
                List<MyBatisProperty> properties = propertyService.getAllProperties();
                return ResponseEntity.ok(Response.success("Users retrieved successfully", properties, HttpStatus.OK.value()));

            case "config":
                try {
                    Map<String, Object> configMap = Map.of(
                            "grids", configLoader.loadGrids(),
                            "forms", configLoader.loadForms(),
                            "regex", configLoader.loadRegexes(),
                            "actions", configLoader.loadActionGroups(),
                            "validators", configLoader.loadValidators()
                    );
                    return ResponseEntity.ok(Response.success("Config loaded", configMap, HttpStatus.OK.value()));
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(Response.error("Failed to load config", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
                }

            default:
                return ResponseEntity.badRequest().body(Response.error("Unknown type", null, HttpStatus.BAD_REQUEST.value()));
        }
    }

    @GetMapping("/paged-simple")
    public ResponseEntity<?> getPagedSimple(@RequestParam String type,
                                            @RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "10") int size,
                                            @RequestParam(defaultValue = "id") String sortField,
                                            @RequestParam(defaultValue = "ASC") String sortOrder) {
        logger.debug("getPagedSimple: type={}", type);

        switch (type.toLowerCase()) {
            case "user":
                List<MyBatisUserUser> users = myBatisUserService.getUsersPaged(page, size, sortField, sortOrder);
                int total = myBatisUserService.getTotalUserCount();
                Map<String, Object> data = new HashMap<>();
                data.put("users", users);
                data.put("total", total);
                return ResponseEntity.ok(Response.success("Paged users retrieved successfully", data, HttpStatus.OK.value()));

            case "property":
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                        .body(Response.error("Paged-simple not implemented for property", null, HttpStatus.NOT_IMPLEMENTED.value()));

            case "config":
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                        .body(Response.error("Paged-simple not implemented for config", null, HttpStatus.NOT_IMPLEMENTED.value()));

            default:
                return ResponseEntity.badRequest().body(Response.error("Unknown type", null, HttpStatus.BAD_REQUEST.value()));
        }
    }
}
