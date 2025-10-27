package simple.chatgpt.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import simple.chatgpt.util.GenericCache;

@Configuration
@EnableTransactionManagement  // Replaces: <tx:advice> + <aop:config>
@EnableAspectJAutoProxy(proxyTargetClass = true)  // Replaces: <aop:aspectj-autoproxy>
public class ApplicationContextConfig {

    private static final Logger logger = LogManager.getLogger(ApplicationContextConfig.class);

    // FROM applicationContext.xml: property-placeholder values
    @Value("${jdbc.driver}")
    private String jdbcDriver;

    @Value("${jdbc.url}")
    private String jdbcUrl;

    @Value("${db.username}")
    private String dbUsername;

    @Value("${db.password}")
    private String dbPassword;

    // FROM applicationContext.xml: propertyCache bean
    /*
    Hung: dont delete this
    before you define this in xml
     	<bean id="propertyCache" class="simple.chatgpt.util.GenericCache">
    	<!-- Constructor argument for expireAfterMinutes -->
    	<constructor-arg value="30" type="long"/>
    	<!-- Constructor argument for maximumSize -->
    	<constructor-arg value="1000" type="long"/>
	</bean>
	<bean id="xyzCache" class="simple.chatgpt.util.GenericCache">
    	<!-- Constructor argument for expireAfterMinutes -->
    	<constructor-arg value="30" type="long"/>
    	<!-- Constructor argument for maximumSize -->
    	<constructor-arg value="1000" type="long"/>
	</bean> 
	/////////////////////
	now you define as this way....
	then you use @Qualifier("propertyCache") at point of injection
    */
    @Bean
    public GenericCache propertyCache() {
        logger.debug("propertyCache called");
        GenericCache cache = new GenericCache(30L, 1000L);  // expireAfterMinutes, maximumSize
        logger.debug("propertyCache created with expireAfterMinutes={} maximumSize={}", 30L, 1000L);
        return cache;
    }
    
    @Bean
    public GenericCache roleCache() {
        logger.debug("roleCache called");
        GenericCache cache = new GenericCache(30L, 1000L);
        logger.debug("roleCache created with expireAfterMinutes={} maximumSize={}", 30L, 1000L);
        return cache;
    }

    @Bean
    public GenericCache roleGroupCache() {
        logger.debug("roleGroupCache called");
        GenericCache cache = new GenericCache(30L, 1000L);
        logger.debug("roleGroupCache created with expireAfterMinutes={} maximumSize={}", 30L, 1000L);
        return cache;
    }

    // FROM applicationContext.xml: dataSource bean
    @Bean
    public DataSource dataSource() {
        logger.debug("dataSource called");
        logger.debug("dataSource param jdbcDriver={}", jdbcDriver);
        logger.debug("dataSource param jdbcUrl={}", jdbcUrl);
        logger.debug("dataSource param dbUsername={}", dbUsername);

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(jdbcDriver);
        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(dbUsername);
        dataSource.setPassword(dbPassword);

        logger.debug("dataSource created with driver={}, url={}, username={}", jdbcDriver, jdbcUrl, dbUsername);
        return dataSource;
    }

    // FROM applicationContext.xml: mybatisSqlSessionFactory bean
    @Bean
    public SqlSessionFactory mybatisSqlSessionFactory(DataSource dataSource) throws Exception {
        logger.debug("mybatisSqlSessionFactory called");
        logger.debug("mybatisSqlSessionFactory param dataSource={}", dataSource);

        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setTypeAliasesPackage("simple.chatgpt.pojo");

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        factoryBean.setMapperLocations(resolver.getResources("classpath*:mapper/**/*.xml"));

        Properties props = new Properties();
        props.setProperty("mapUnderscoreToCamelCase", "true");
        factoryBean.setConfigurationProperties(props);

        logger.debug("mybatisSqlSessionFactory created with typeAliasesPackage=simple.chatgpt.pojo and mapperLocations=classpath*:mapper/**/*.xml");
        return factoryBean.getObject();
    }

    // FROM applicationContext.xml: mybatisTransactionManager bean
    @Bean
    public DataSourceTransactionManager mybatisTransactionManager(DataSource dataSource) {
        logger.debug("mybatisTransactionManager called");
        logger.debug("mybatisTransactionManager param dataSource={}", dataSource);

        DataSourceTransactionManager txManager = new DataSourceTransactionManager(dataSource);
        logger.debug("mybatisTransactionManager created");
        return txManager;
    }

    // FROM applicationContext.xml: Validator
    @Bean
    public LocalValidatorFactoryBean validator() {
        logger.debug("validator called");
        LocalValidatorFactoryBean v = new LocalValidatorFactoryBean();
        logger.debug("validator created");
        return v;
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
