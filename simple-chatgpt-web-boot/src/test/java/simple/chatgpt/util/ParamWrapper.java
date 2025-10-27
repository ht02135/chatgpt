package simple.chatgpt.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ParamWrapper {

    private static final Logger logger = LogManager.getLogger(ParamWrapper.class);

    // -------------------- WRAP --------------------
    public static Map<String, Object> wrap(Object... keyValues) {
        logger.debug("wrap called");
        logger.debug("wrap #####");
        logger.debug("wrap keyValues={}", (Object) keyValues);
        logger.debug("wrap #####");

        Map<String, Object> inner = new HashMap<>();

        if (keyValues.length == 1) {
            Object obj = keyValues[0];
            if (obj instanceof Map) {
                // Copy into a mutable map
                inner.putAll((Map<String, Object>) obj);
            } else {
                inner.put("value", obj);
            }
        } else {
            if (keyValues.length % 2 != 0) {
                throw new IllegalArgumentException("Key-values must come in pairs");
            }
            for (int i = 0; i < keyValues.length; i += 2) {
                String key = (String) keyValues[i];
                Object value = keyValues[i + 1];
                inner.put(key, value);
            }
        }

        // Top-level outer map must also be mutable
        Map<String, Object> outer = new HashMap<>();
        outer.put("params", inner);

        logger.debug("wrap #####");
        logger.debug("wrap returning outer={}", outer);
        logger.debug("wrap #####");
        return outer;
    }

    // -------------------- UNWRAP --------------------
    public static <T> T unwrap(Map<?, ?> map, String key) {
        logger.debug("unwrap(Map<?,?>, String) called");
        logger.debug("unwrap #####");
        logger.debug("unwrap map={}", map);
        logger.debug("unwrap key={}", key);
        logger.debug("unwrap #####");

        if (map == null || key == null) {
            logger.debug("unwrap #####");
            logger.debug("unwrap early exit: map or key is null");
            logger.debug("unwrap #####");
            return null;
        }

        Object value = map.get(key);
        if (value != null) {
            logger.debug("unwrap #####");
            logger.debug("unwrap found key={} at top level, returning value={}", key, value);
            logger.debug("unwrap #####");
            return (T) value;
        }

        Object inner = map.get("params");
        if (inner instanceof Map) {
            logger.debug("unwrap #####");
            logger.debug("unwrap drilling into nested 'params' for key={}", key);
            T nestedValue = unwrap((Map<?, ?>) inner, key);
            logger.debug("unwrap returning nested value={} for key={}", nestedValue, key);
            logger.debug("unwrap #####");
            return nestedValue;
        }

        logger.debug("unwrap #####");
        logger.debug("unwrap key={} not found, returning null", key);
        logger.debug("unwrap #####");
        return null;
    }

    // -------------------- UNWRAP WITH DEFAULT --------------------
    public static <T> T unwrap(Map<?, ?> map, String key, T defaultValue) {
        logger.debug("unwrap(Map<?,?>, String, T) called");
        logger.debug("unwrap map={}", map);
        logger.debug("unwrap key={}", key);
        logger.debug("unwrap defaultValue={}", defaultValue);

        Object raw = unwrap(map, key);
        if (raw == null) {
            logger.debug("unwrap key={} not found, returning defaultValue={}", key, defaultValue);
            return defaultValue;
        }

        logger.debug("unwrap found raw value={} for key={}", raw, key);

        if (defaultValue == null) {
            try {
                return (T) raw;
            } catch (ClassCastException e) {
                logger.error("unwrap cast failed when defaultValue is null for key={}, raw={}", key, raw, e);
                return null;
            }
        }

        if (defaultValue.getClass().isInstance(raw)) {
            return (T) raw;
        }

        if (raw instanceof String) {
            String s = (String) raw;
            try {
                if (defaultValue instanceof Integer) return (T) Integer.valueOf(s);
                if (defaultValue instanceof Long) return (T) Long.valueOf(s);
                if (defaultValue instanceof Boolean) return (T) Boolean.valueOf(s);
                if (defaultValue instanceof String) return (T) s;
                return (T) raw;
            } catch (Exception e) {
                logger.error("unwrap conversion failed for key={} value={} targetType={}", key, s, defaultValue.getClass().getName(), e);
                return defaultValue;
            }
        }

        logger.debug("unwrap raw type {} not assignable to target {}, returning defaultValue={}",
                raw.getClass().getName(), defaultValue.getClass().getName(), defaultValue);
        return defaultValue;
    }
}
