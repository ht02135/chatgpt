package simple.chatgpt.config.management.validation;

public class ValidatorConfig {
    private String type;
    private String validRegexExpression;
    private String errorMessage;

    public ValidatorConfig() {}

    public ValidatorConfig(String type, String validRegexExpression, String errorMessage) {
        this.type = type;
        this.validRegexExpression = validRegexExpression;
        this.errorMessage = errorMessage;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getValidRegexExpression() { return validRegexExpression; }
    public void setValidRegexExpression(String validRegexExpression) { this.validRegexExpression = validRegexExpression; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}
