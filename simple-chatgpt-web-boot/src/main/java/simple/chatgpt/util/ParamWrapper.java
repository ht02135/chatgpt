package simple.chatgpt.util;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ParamWrapper {

    private static final Logger logger = LogManager.getLogger(ParamWrapper.class);

    // -------------------- WRAP --------------------
    public static Map<String, Object> wrap(Object... keyValues) {
        logger.debug("wrap #############");
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
            inner = new java.util.HashMap<>();
            for (int i = 0; i < keyValues.length; i += 2) {
                String key = (String) keyValues[i];
                Object value = keyValues[i + 1];
                inner.put(key, value);
            }
        }

        Map<String, Object> outer = Map.of("params", inner);
        logger.debug("wrap returning outer={}", outer);
        logger.debug("wrap #############");
        return outer;
    }

    // -------------------- UNWRAP --------------------
    /**
     * Recursively finds the value for the given key inside nested "params" layers.
     */
    @SuppressWarnings("unchecked")
    public static <T> T unwrap(Map<String, Object> map, String key) {
        if (map == null || key == null) {
            return null;
        }

        Object value = map.get(key);
        if (value != null) {
            return (T) value;
        }

        Object inner = map.get("params");
        if (inner instanceof Map) {
            logger.debug("unwrap(): drilling into nested 'params' for key={}", key);
            return unwrap((Map<String, Object>) inner, key);
        }

        return null;
    }
}
