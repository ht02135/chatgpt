package simple.chatgpt.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ParamWrapper {

    private static final Logger logger = LogManager.getLogger(ParamWrapper.class);

    // -------------------- FLAT WRAP --------------------
    public static Map<String, Object> wrap(Object... keyValues) {
        logger.debug("wrap (flat) called, keyValues={}", (Object) keyValues);

        Map<String, Object> map = new HashMap<>();

        if (keyValues.length == 1 && keyValues[0] instanceof Map) {
            map.putAll((Map<String, Object>) keyValues[0]);
        } else {
            if (keyValues.length % 2 != 0) {
                throw new IllegalArgumentException("Key-values must come in pairs");
            }
            for (int i = 0; i < keyValues.length; i += 2) {
                map.put((String) keyValues[i], keyValues[i + 1]);
            }
        }

        logger.debug("wrap (flat) returning map={}", map);
        return map;
    }

    // -------------------- NESTED WRAP (extraWrap) --------------------
    public static Map<String, Object> extraWrap(Object... keyValues) {
        logger.debug("extraWrap (nested) called, keyValues={}", (Object) keyValues);

        Map<String, Object> inner = wrap(keyValues); // use flat wrap to build inner map
        Map<String, Object> outer = new HashMap<>();
        outer.put("params", inner);

        logger.debug("extraWrap (nested) returning outer={}", outer);
        return outer;
    }

    // -------------------- UNWRAP --------------------
    public static <T> T unwrap(Map<?, ?> map, String key) {
        if (map == null || key == null) return null;

        Object value = map.get(key);
        if (value != null) return (T) value;

        Object inner = map.get("params");
        if (inner instanceof Map) {
            return unwrap((Map<?, ?>) inner, key);
        }

        return null;
    }

    // -------------------- UNWRAP WITH DEFAULT --------------------
    public static <T> T unwrap(Map<?, ?> map, String key, T defaultValue) {
        Object raw = unwrap(map, key);
        if (raw == null) return defaultValue;

        if (defaultValue == null || defaultValue.getClass().isInstance(raw)) {
            return (T) raw;
        }

        if (raw instanceof String) {
            String s = (String) raw;
            try {
                if (defaultValue instanceof Integer) return (T) Integer.valueOf(s);
                if (defaultValue instanceof Long) return (T) Long.valueOf(s);
                if (defaultValue instanceof Boolean) return (T) Boolean.valueOf(s);
                if (defaultValue instanceof String) return (T) s;
            } catch (Exception e) {
                logger.error("unwrap conversion failed for key={} value={} targetType={}", key, s, defaultValue.getClass().getName(), e);
            }
        }

        return defaultValue;
    }
}
