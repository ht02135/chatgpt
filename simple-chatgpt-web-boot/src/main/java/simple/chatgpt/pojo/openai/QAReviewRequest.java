package simple.chatgpt.pojo.openai;

import org.apache.ibatis.type.Alias;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

/*
 hung: POJO representing a quality assurance review request
 */
@Alias("openaiQAReviewRequest")		// for MyBatis    
public class QAReviewRequest {
    private static final Logger logger = LogManager.getLogger(QAReviewRequest.class);

    private final String criteria;

    public QAReviewRequest(String criteria) {
        logger.debug("QAReviewRequest constructor called");
        logger.debug("QAReviewRequest criteria={}", criteria);

        this.criteria = criteria;

        // log whole object
        logger.debug("QAReviewRequest this={}", this);
    }

    public String getCriteria() {
        logger.debug("getCriteria called");
        return criteria;
    }

    @Override
    public String toString() {
        return "QAReviewRequest{" +
                "criteria='" + criteria + '\'' +
                '}';
    }
}
