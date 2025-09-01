package simple.chatgpt.controller.mybatis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import simple.chatgpt.pojo.mybatis.Property;
import simple.chatgpt.service.mybatis.PropertyService;
import simple.chatgpt.util.PropertyKey;
import simple.chatgpt.util.Response;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mybatis/properties")
public class PropertyController {
    private final PropertyService propertyService;

    @Autowired
    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    /*
    curl -X GET "http://localhost:8080/chatgpt/api/mybatis/properties/all" -H "Accept: application/json"
    */
    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response<List<Property>>> getAllProperties() {
        List<Property> properties = propertyService.getAllProperties();
        Response<List<Property>> response = Response.success("Properties retrieved successfully", properties, HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response<Void>> updateProperty(@RequestBody Map<String, String> payload) {
        String key = payload.get("key");
        String value = payload.get("value");
        try {
            PropertyKey propertyKey = PropertyKey.valueOf(key);
            propertyService.updateProperty(propertyKey, value);
            Response<Void> response = Response.success("Property updated successfully", null, HttpStatus.OK.value());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Response<Void> response = Response.error("Invalid property key", null, HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}