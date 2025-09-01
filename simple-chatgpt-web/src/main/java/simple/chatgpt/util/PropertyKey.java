package simple.chatgpt.util;

public enum PropertyKey {
    SOME_BOOLEAN("some_boolean", Boolean.class, Boolean.FALSE),
    SOME_INTEGER("some_integer", Integer.class, 20),
    SOME_STRING("some_string", String.class, "find"),
    SOME_DECIMAL("some_decimal", java.math.BigDecimal.class, new java.math.BigDecimal("1.5"));

    private final String key;
    private final Class<?> type;
    private final Object defaultValue;

    <T> PropertyKey(String key, Class<T> type, T defaultValue) {
        this.key = key;
        this.type = type;
        this.defaultValue = defaultValue;
    }

    public String key() { return key; }
    public Class<?> type() { return type; }

    @SuppressWarnings("unchecked")
    public <T> T defaultValue() {
        return (T) defaultValue;
    }

    public String getKey() { return key; }
    public Class<?> getType() { return type; }
    public Object getDefaultValue() { return defaultValue; }
    public String getTypeName() {
        if (type == Boolean.class) return "Boolean";
        if (type == Integer.class) return "Integer";
        if (type == String.class) return "String";
        if (type == java.math.BigDecimal.class) return "BigDecimal";
        return type.getSimpleName();
    }
}