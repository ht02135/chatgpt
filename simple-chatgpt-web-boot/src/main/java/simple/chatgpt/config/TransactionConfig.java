package simple.chatgpt.config;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Configuration;

/**
 * OPTION 1: Programmatic AOP for transactions (mimics XML behavior)
 * - Defines the pointcut that matches the XML version:
 *   execution(* simple.chatgpt.service.mybatis.*Service.*(..))
 *
 * NOTE:
 * - This class only defines the pointcut; actual transaction semantics should be
 *   provided via @Transactional on service methods OR by wiring a transaction interceptor.
 * - If you prefer XML-like NameMatchTransactionAttributeSource + interceptor, put that in ApplicationContextConfig.
 */
@Aspect
@Configuration
public class TransactionConfig {

    private static final Logger logger = LogManager.getLogger(TransactionConfig.class);

    public TransactionConfig() {
        logger.debug("TransactionConfig constructor called");
    }

    @Pointcut("execution(* simple.chatgpt.service.management.*Service.*(..))")
    public void mybatisServiceMethods() {
        // pointcut method must be empty
    }

    @PostConstruct
    public void postConstruct() {
        logger.debug("TransactionConfig postConstruct called");
        logger.debug("TransactionConfig pointcut=execution(* simple.chatgpt.service.management.*Service.*(..))");
    }
}
