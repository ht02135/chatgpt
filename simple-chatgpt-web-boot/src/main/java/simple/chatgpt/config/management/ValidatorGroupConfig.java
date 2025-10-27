package simple.chatgpt.config.management;

import java.util.ArrayList;
import java.util.List;

import simple.chatgpt.config.management.validation.ValidatorConfig;

public class ValidatorGroupConfig {
    private String id;
    private List<ValidatorConfig> validators = new ArrayList<>();

    public ValidatorGroupConfig() {}

    public ValidatorGroupConfig(String id) { this.id = id; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public List<ValidatorConfig> getValidators() { return validators; }
    public void setValidators(List<ValidatorConfig> validators) { this.validators = validators; }

    public void addValidator(ValidatorConfig validator) { this.validators.add(validator); }
}
