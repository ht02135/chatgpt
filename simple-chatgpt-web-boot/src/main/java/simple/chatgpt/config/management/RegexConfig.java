package simple.chatgpt.config.management;

public class RegexConfig {
    private String id;
    private String expression;
    private String errorMessage;

    public RegexConfig() {}

    public RegexConfig(String id, String expression, String errorMessage) {
        this.id = id;
        this.expression = expression;
        this.errorMessage = errorMessage;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getExpression() { return expression; }
    public void setExpression(String expression) { this.expression = expression; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}