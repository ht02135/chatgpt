package simple.chatgpt.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ParamWrapper {

    private static final Logger logger = LogManager.getLogger(ParamWrapper.class);

    // -------------------- WRAP --------------------
    /**
     * Wraps key-value pairs into a map with a top-level "params" layer.
     * Always returns a top-level map with "params".
     */
    public static Map<String, Object> wrap(Object... keyValues) {
        logger.debug("wrap called with keyValues={}", (Object) keyValues);

        Map<String, Object> inner;

        if (keyValues.length == 1) {
            Object obj = keyValues[0];
            if (obj instanceof Map) {
                inner = (Map<String, Object>) obj;
            } else {
                inner = Map.of("value", obj);
            }
        } else {
            if (keyValues.length % 2 != 0) {
                throw new IllegalArgumentException("Key-values must come in pairs");
            }
            inner = new HashMap<>();
            for (int i = 0; i < keyValues.length; i += 2) {
                String key = (String) keyValues[i];
                Object value = keyValues[i + 1];
                inner.put(key, value);
            }
        }

        Map<String, Object> outer = Map.of("params", inner);
        logger.debug("wrap returning outer={}", outer);
        return outer;
    }

    // -------------------- UNWRAP --------------------
    /**
     * Recursively finds the value for the given key inside nested "params" layers.
     * Top-level match takes priority.
     */
    @SuppressWarnings("unchecked")
    public static <T> T unwrap(Map<String, Object> map, String key) {
        logger.debug("unwrap called for key={} on map={}", key, map);

        if (map == null || key == null) {
            logger.debug("unwrap early exit: map or key is null");
            return null;
        }

        Object value = map.get(key);
        if (value != null) {
            logger.debug("unwrap found key={} at top level, returning value={}", key, value);
            return (T) value;
        }

        Object inner = map.get("params");
        if (inner instanceof Map) {
            logger.debug("unwrap drilling into nested 'params' for key={}", key);
            T nestedValue = unwrap((Map<String, Object>) inner, key);
            logger.debug("unwrap returning nested value={} for key={}", nestedValue, key);
            return nestedValue;
        }

        logger.debug("unwrap key={} not found, returning null", key);
        return null;
    }

    // -------------------- UNWRAP WITH DEFAULT --------------------
    /**
     * Recursively finds the value for the given key inside nested "params" layers.
     * Returns defaultValue if key is not found.
     */
    public static <T> T unwrap(Map<String, Object> map, String key, T defaultValue) {
        logger.debug("unwrap with default called for key={} on map={}, default={}", key, map, defaultValue);

        T value = unwrap(map, key);
        if (value != null) {
            logger.debug("unwrap with default found key={} value={}", key, value);
            return value;
        }

        logger.debug("unwrap with default key={} not found, returning default={}", key, defaultValue);
        return defaultValue;
    }
}
