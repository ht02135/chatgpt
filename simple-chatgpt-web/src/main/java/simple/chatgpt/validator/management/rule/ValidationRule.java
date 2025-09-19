package simple.chatgpt.validator.management.rule;

public class ValidationRule {
    private final String id;
    private final String field; // NEW: associated POJO field
    private final String regex;
    private final String error;

    public ValidationRule(String id, String field, String regex, String error) {
        this.id = id;
        this.field = field;
        this.regex = regex;
        this.error = error;
    }

    public String getId() { return id; }
    public String getField() { return field; } // getter for field
    public String getRegex() { return regex; }
    public String getError() { return error; }

    @Override
    public String toString() {
        return "ValidationRule{id='" + id + "', field='" + field + "', regex='" + regex + "', error='" + error + "'}";
    }
}
