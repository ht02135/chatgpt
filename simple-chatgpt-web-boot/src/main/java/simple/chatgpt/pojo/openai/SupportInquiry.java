package simple.chatgpt.pojo.openai;

import org.apache.ibatis.type.Alias;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

/*
 hung: POJO representing a customer support inquiry
 */
@Alias("openaiSupportInquiry")		// for MyBatis    
public class SupportInquiry {
    private static final Logger logger = LogManager.getLogger(SupportInquiry.class);

    private final String customerId;
    private final String message;

    public SupportInquiry(String customerId, String message) {
        logger.debug("SupportInquiry constructor called");
        logger.debug("SupportInquiry customerId={}", customerId);
        logger.debug("SupportInquiry message={}", message);

        this.customerId = customerId;
        this.message = message;

        // log whole object
        logger.debug("SupportInquiry this={}", this);
    }

    public String getCustomerId() {
        logger.debug("getCustomerId called");
        return customerId;
    }

    public String getMessage() {
        logger.debug("getMessage called");
        return message;
    }

    @Override
    public String toString() {
        return "SupportInquiry{" +
                "customerId='" + customerId + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
