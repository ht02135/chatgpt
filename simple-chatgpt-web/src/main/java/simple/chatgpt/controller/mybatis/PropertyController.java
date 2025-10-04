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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import simple.chatgpt.pojo.mybatis.MyBatisProperty;
import simple.chatgpt.service.mybatis.PropertyService;
import simple.chatgpt.util.PropertyKey;
import simple.chatgpt.util.Response;

@RestController
@RequestMapping("/mybatis/properties")
public class PropertyController {
	private static final Logger logger = LogManager.getLogger(PropertyController.class);
	
    private final PropertyService propertyService;

    @Autowired
    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response<Map<String, Object>>> getAllProperties(
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @RequestParam(value = "sort", required = false, defaultValue = "key") String sort,
            @RequestParam(value = "order", required = false, defaultValue = "ASC") String order
    ) {
        List<MyBatisProperty> properties = propertyService.getProperties(key, type, page, size, sort, order);
        int total = propertyService.countProperties(key, type);
        int maxPage = (int) Math.ceil((double) total / size);
        Map<String, Object> data = new HashMap<>();
        data.put("properties", properties);
        data.put("total", total);
        data.put("page", page);
        data.put("size", size);
        data.put("maxPage", maxPage);
        Response<Map<String, Object>> response =
                Response.success("Properties retrieved successfully", data, HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/{key}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response<MyBatisProperty>> getPropertyByKey(@PathVariable("key") String key) {
        MyBatisProperty property = propertyService.getAllProperties()
                                          .stream()
                                          .filter(p -> p.getKey().equals(key))
                                          .findFirst()
                                          .orElse(null);
        if (property == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.error("Property not found", null, HttpStatus.NOT_FOUND.value()));
        }
        return ResponseEntity.ok(Response.success("Property retrieved", property, HttpStatus.OK.value()));
    }

    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response<Void>> updateProperty(@RequestBody Map<String, String> payload) {
    	logger.debug("updateProperty payload: {}", payload);
    	
        String key = payload.get("key");
        String value = payload.get("value");
        logger.debug("updateProperty key: {}", key);
        logger.debug("updateProperty value: {}", value);
        
        try {
            PropertyKey propertyKey = PropertyKey.fromKey(key);
            propertyService.updateProperty(propertyKey, value);
            Response<Void> response = Response.success("Property updated successfully", null, HttpStatus.OK.value());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Response<Void> response = Response.error("Invalid property key", null, HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
