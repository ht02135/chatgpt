package simple.chatgpt.util;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum PropertyKey {
	/*
	Every Java enum automatically gets a static method called values().
	It returns an array of all the constants declared in that enum
	////////////////////
	Here’s what’s happening step by step:
	1>values() → gives you the array of all enum constants.
	→ [SOME_BOOLEAN, SOME_INTEGER, SOME_STRING, SOME_DECIMAL]
	2>Arrays.stream(values()) 
	→ turns that array into a Stream so we can process it.
	3>.collect(Collectors.toMap(PropertyKey::getKey, e -> e)) 
	→ builds a Map where:
		Key = PropertyKey.getKey() (like "some_string")
		Value = the enum constant itself (like PropertyKey.SOME_STRING)
	////////////////////
	*/
	
	MANAGEMENT_CONFIG_RELATIVE_PATH("management_config_relative_path", String.class, "/config/management/config.xml"),
	VALIDATION_CONFIG_RELATIVE_PATH("validation_config_relative_path", String.class, "/config/management/validation-config.xml"),
	UPLOAD_CONFIG_RELATIVE_PATH("upload_config_relative_path", String.class, "/config/management/upload-config.xml"),
	DOWNLOAD_CONFIG_RELATIVE_PATH("download_config_relative_path", String.class, "/config/management/download-config.xml"),
	SAMPLE_CSV_RELATIVE_PATH("sample_csv_relative_path", String.class, "/management/data/user_lists/test_user_lists_1.csv"),
	
	LOGIN_URL_RELATIVE_PATH("login_url_relative_path", String.class, "/login"),
	LOGIN_REDIRECT_URL_RELATIVE_PATH("login_redirect_url_relative_path", String.class, "/management/jsp/auth/login.jsp"),
	
	LOGOUT_URL_RELATIVE_PATH("logout_url_relative_path", String.class, "/logout"),
	LOGOUT_REDIRECT_URL_RELATIVE_PATH("logout_redirect_url_relative_path", String.class, "/management/jsp/auth/logout.jsp"),
	
	RELOAD_USER_PASSWORD("reload_user_password", Boolean.class, Boolean.TRUE),
	DISABLE_USER_SAVE("disable_user_save", Boolean.class, Boolean.FALSE),
	
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
