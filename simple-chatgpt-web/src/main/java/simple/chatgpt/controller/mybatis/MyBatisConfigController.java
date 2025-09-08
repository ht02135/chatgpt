package simple.chatgpt.controller.mybatis;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import simple.chatgpt.config.ConfigLoader;
import simple.chatgpt.config.FormConfig;
import simple.chatgpt.config.GridConfig;

@Controller
@RequestMapping("/mybatis/config")
public class MyBatisConfigController {
	private static final Logger logger = LogManager.getLogger(MyBatisConfigController.class);

    private final ConfigLoader loader = new ConfigLoader();

    @GetMapping("/all")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAllConfig() {
    	logger.info("MyBatisConfigController getAllConfig");
    	
        try {
            List<GridConfig> grids = loader.loadGrids();
            List<FormConfig> forms = loader.loadForms();

            return ResponseEntity.ok(Map.of(
                    "grids", grids,
                    "forms", forms
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
