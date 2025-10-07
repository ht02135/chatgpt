package simple.chatgpt.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ParamWrapper {

    private static final Logger logger = LogManager.getLogger(ParamWrapper.class);

    public static Map<String, Object> wrap(Object... keyValues) {
    	logger.debug("wrap #############");
    	logger.debug("wrap called with keyValues={}", (Object) keyValues);
    	logger.debug("wrap #############");
    	 
        Map<String, Object> inner;

        if (keyValues.length == 1) {
            Object obj = keyValues[0];
            if (obj instanceof Map) {
                inner = (Map<String, Object>) obj;
            } else {
                inner = new HashMap<>();
                inner.put("value", obj);
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

        Map<String, Object> outer = new HashMap<>();
        outer.put("params", inner);
        logger.debug("wrap #############");
        logger.debug("wrap returning outer={}", outer);
        logger.debug("wrap #############");

        return outer;
    }
}
