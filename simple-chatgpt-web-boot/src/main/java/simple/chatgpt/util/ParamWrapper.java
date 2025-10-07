package simple.chatgpt.util;

import java.util.HashMap;
import java.util.Map;

public class ParamWrapper {

    public static Map<String, Object> wrap(Object... keyValues) {
        if (keyValues.length % 2 != 0) {
            throw new IllegalArgumentException("Key-values must come in pairs");
        }

        Map<String, Object> inner = new HashMap<>();
        for (int i = 0; i < keyValues.length; i += 2) {
            String key = (String) keyValues[i];
            Object value = keyValues[i + 1];
            inner.put(key, value);
        }

        Map<String, Object> outer = new HashMap<>();
        outer.put("params", inner);
        return outer;
    }
}
