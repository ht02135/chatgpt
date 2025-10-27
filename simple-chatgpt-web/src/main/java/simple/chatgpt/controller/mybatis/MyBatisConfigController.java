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

import simple.chatgpt.config.ActionGroupConfig;
import simple.chatgpt.config.FormConfig;
import simple.chatgpt.config.GridConfig;
import simple.chatgpt.config.RegexConfig;
import simple.chatgpt.config.ValidatorGroupConfig;   // ✅ import new class
import simple.chatgpt.config.mybatis.loader.ConfigLoader;
import simple.chatgpt.util.Response;

@Controller
@RequestMapping("/mybatis/config")
public class MyBatisConfigController {

    private static final Logger logger = LogManager.getLogger(MyBatisConfigController.class);
    private final ConfigLoader loader = new ConfigLoader();

    @GetMapping("/all")
    @ResponseBody
    public ResponseEntity<Response<Map<String, Object>>> getAllConfig() {
        logger.info("MyBatisConfigController getAllConfig");

        try {
            List<GridConfig> grids = loader.loadGrids();
            List<FormConfig> forms = loader.loadForms();
            List<RegexConfig> regexes = loader.loadRegexes();
            List<ActionGroupConfig> actionGroups = loader.loadActionGroups();
            List<ValidatorGroupConfig> validatorGroups = loader.loadValidators(); // ✅ new

            Map<String, Object> configMap = Map.of(
                "grids", grids,
                "forms", forms,
                "regex", regexes,
                "actions", actionGroups,
                "validators", validatorGroups
            );

            return ResponseEntity.ok(Response.success("Loaded config", configMap, 200));
        } catch (Exception e) {
            logger.error("Failed to load config", e);
            return ResponseEntity
                    .status(500)
                    .body(Response.error("Failed to load config", null, 500));
        }
    }
}
