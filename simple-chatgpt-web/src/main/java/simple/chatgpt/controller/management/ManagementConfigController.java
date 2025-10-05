package simple.chatgpt.controller.management;

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
import simple.chatgpt.config.ValidatorGroupConfig;
import simple.chatgpt.config.management.loader.ManagementConfigLoader;
import simple.chatgpt.util.Response;

@Controller
@RequestMapping("/management/config")
public class ManagementConfigController {

    private static final Logger logger = LogManager.getLogger(ManagementConfigController.class);
    private final ManagementConfigLoader loader;

    public ManagementConfigController(ManagementConfigLoader loader) {
        this.loader = loader;
    }
    
    @GetMapping
    @ResponseBody
    public ResponseEntity<Response<Map<String, Object>>> getConfig() {
        logger.info("ManagementConfigController getConfig");

        try {
            List<GridConfig> grids = loader.loadGrids();
            List<FormConfig> forms = loader.loadForms();
            List<RegexConfig> regexes = loader.loadRegexes();
            List<ActionGroupConfig> actionGroups = loader.loadActionGroups();
            List<ValidatorGroupConfig> validatorGroups = loader.loadValidators();

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
