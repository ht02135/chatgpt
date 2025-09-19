package simple.chatgpt.validator.management.rule;

public class ValidationRule {
    private final String id;
    private final String regex;
    private final String error;

    public ValidationRule(String id, String regex, String error) {
        this.id = id;
        this.regex = regex;
        this.error = error;
    }

    public String getId() { return id; }
    public String getRegex() { return regex; }
    public String getError() { return error; }

    @Override
    public String toString() {
        return "ValidationRule{id='" + id + "', regex='" + regex + "', error='" + error + "'}";
    }
}
