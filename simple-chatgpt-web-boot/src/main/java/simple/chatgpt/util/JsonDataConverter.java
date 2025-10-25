package simple.chatgpt.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Converter
public class JsonDataConverter implements AttributeConverter<Map<String, Object>, String> {

    private static final Logger logger = LogManager.getLogger(JsonDataConverter.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<String, Object> attribute) {
        logger.debug("convertToDatabaseColumn called");
        logger.debug("convertToDatabaseColumn attribute={}", attribute);

        if (attribute == null) {
            logger.debug("convertToDatabaseColumn attribute is null, returning null");
            return null;
        }

        try {
            String json = objectMapper.writeValueAsString(attribute);
            logger.debug("convertToDatabaseColumn JSON output={}", json);
            return json;
        } catch (JsonProcessingException e) {
            logger.error("convertToDatabaseColumn failed to serialize Map", e);
            throw new IllegalArgumentException("Could not convert Map to JSON", e);
        }
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String dbData) {
        logger.debug("convertToEntityAttribute called");
        logger.debug("convertToEntityAttribute dbData={}", dbData);

        if (dbData == null || dbData.isEmpty()) {
            logger.debug("convertToEntityAttribute dbData is null or empty, returning empty Map");
            return new HashMap<>();
        }

        try {
            Map<String, Object> map = objectMapper.readValue(dbData, Map.class);
            logger.debug("convertToEntityAttribute parsed map={}", map);
            return map;
        } catch (IOException e) {
            logger.error("convertToEntityAttribute failed to parse JSON", e);
            throw new IllegalArgumentException("Could not convert JSON to Map", e);
        }
    }
}
