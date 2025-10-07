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
    @SuppressWarnings("unchecked")
    public static Map<String, Object> wrap(Object... keyValues) {
        logger.debug("wrap called");
        logger.debug("wrap #####");
        logger.debug("wrap keyValues={}", (Object) keyValues);
        logger.debug("wrap #####");

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
        logger.debug("wrap #####");
        logger.debug("wrap returning outer={}", outer);
        logger.debug("wrap #####");
        return outer;
    }

    // -------------------- UNWRAP (generic Map) --------------------
    /*
      hung: updated unwrap to accept Map<?,?> (works for Map<String,Object> and Map<String,String>)
    */
    @SuppressWarnings("unchecked")
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

    // -------------------- UNWRAP WITH DEFAULT (generic Map) --------------------
    /**
     * Recursively finds the value for the given key inside nested "params" layers.
     * Returns defaultValue if key is not found. If the found value is a String and
     * defaultValue is non-null of a primitive wrapper (Integer/Long/Boolean),
     * attempt simple parsing.
     */
    @SuppressWarnings("unchecked")
    public static <T> T unwrap(Map<?, ?> map, String key, T defaultValue) {
        logger.debug("unwrap(Map<?,?>, String, T) called");
        logger.debug("unwrap map={}", map);
        logger.debug("unwrap key={}", key);
        logger.debug("unwrap defaultValue={}", defaultValue);

        if (map == null || key == null) {
            logger.debug("unwrap early exit: map or key is null, returning defaultValue={}", defaultValue);
            return defaultValue;
        }

        Object raw = unwrap(map, key);
        if (raw == null) {
            logger.debug("unwrap key={} not found, returning defaultValue={}", key, defaultValue);
            return defaultValue;
        }

        logger.debug("unwrap found raw value={} for key={}", raw, key);

        // If types already match or defaultValue is null, return raw cast
        if (defaultValue == null) {
            try {
                return (T) raw;
            } catch (ClassCastException e) {
                logger.error("unwrap cast failed when defaultValue is null for key={}, raw={}, targetType=unknown", key, raw, e);
                return null;
            }
        }

        // If the raw value already matches the defaultValue's type, return it
        if (defaultValue.getClass().isInstance(raw)) {
            logger.debug("unwrap raw is instance of defaultValue type, returning raw casted");
            return (T) raw;
        }

        // If raw is a String, attempt basic conversions based on the defaultValue type
        if (raw instanceof String) {
            String s = (String) raw;
            try {
                if (defaultValue instanceof Integer) {
                    Integer intValue = Integer.valueOf(s);
                    logger.debug("unwrap parsed Integer value={}", intValue);
                    return (T) intValue;
                } else if (defaultValue instanceof Long) {
                    Long longValue = Long.valueOf(s);
                    logger.debug("unwrap parsed Long value={}", longValue);
                    return (T) longValue;
                } else if (defaultValue instanceof Boolean) {
                    Boolean boolValue = Boolean.valueOf(s);
                    logger.debug("unwrap parsed Boolean value={}", boolValue);
                    return (T) boolValue;
                } else if (defaultValue instanceof String) {
                    logger.debug("unwrap returning raw String value={}", s);
                    return (T) s;
                } else {
                    // Unknown target; try to return raw (may result in ClassCastException)
                    logger.debug("unwrap unknown defaultValue type {}, returning raw String", defaultValue.getClass().getName());
                    return (T) raw;
                }
            } catch (Exception e) {
                logger.error("unwrap conversion failed for key={} value={} targetType={}", key, s, defaultValue.getClass().getName(), e);
                return defaultValue;
            }
        }

        // If raw is some other type and not assignable, attempt to return default
        logger.debug("unwrap raw type {} not assignable to target {}, returning defaultValue={}", raw.getClass().getName(), defaultValue.getClass().getName(), defaultValue);
        return defaultValue;
    }
}
