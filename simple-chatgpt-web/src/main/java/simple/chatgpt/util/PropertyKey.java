package simple.chatgpt.util;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

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

    // 🔑 Add a lookup Map for fast reverse lookup
    private static final Map<String, PropertyKey> LOOKUP =
        Arrays.stream(values()).collect(Collectors.toMap(PropertyKey::getKey, e -> e));

    // 🔑 Public method to fetch enum by string key
    public static PropertyKey fromKey(String key) {
        PropertyKey pk = LOOKUP.get(key);
        if (pk == null) {
            throw new IllegalArgumentException("Unknown property key: " + key);
        }
        return pk;
    }
}
