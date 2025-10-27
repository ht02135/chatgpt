package simple.chatgpt;

import java.util.Arrays;

import javax.annotation.PostConstruct;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.util.unit.DataSize;
import org.springframework.web.servlet.DispatcherServlet;

@SpringBootApplication  // Replaces: ContextLoaderListener + component-scan
@MapperScan("simple.chatgpt.mapper")  // Hung: Replaces: MapperScannerConfigure, scan all MyBatis mappers automatically
public class SimpleChatgptWebBootApplication extends SpringBootServletInitializer {

    private static final Logger logger = LogManager.getLogger(SimpleChatgptWebBootApplication.class);

    @Autowired
    private ApplicationContext context;

    // ------------------------------
    // WAR support for external Tomcat
    // ------------------------------

    // Required no-arg constructor for WAR deployment
    public SimpleChatgptWebBootApplication() {
        logger.debug("SimpleChatgptWebBootApplication no-arg constructor called");
    }

    // Optional constructor if context is injected manually
    public SimpleChatgptWebBootApplication(ApplicationContext context) {
        this.context = context;
        logger.debug("SimpleChatgptWebBootApplication ApplicationContext constructor called");
    }

    public static void main(String[] args) {
        logger.debug("SimpleChatgptWebBootApplication.main called");
        SpringApplication.run(SimpleChatgptWebBootApplication.class, args);
    }

    // ------------------------------
    // WAR support for external Tomcat
    // ------------------------------
    /*
    hung: DONT REMOVE THIS COMMENT
    SimpleChatgptWebBootApplication.java  
    this is the Spring Boot main class version, but it will not 
    deploy properly as a WAR on external Tomcat yet.
    To fix it for external Tomcat deployment while keeping all your 
    logging and multipart/dispatcher config, you need to extend 
    SpringBootServletInitializer and override configure().
    ///////////////////////////
    SimpleChatgptWebBootApplication
    Purpose: your real Boot application class.
    Annotated with @SpringBootApplication and @MapperScan, so it does:
        Component scan
        Auto-configuration
        Starts the ApplicationContext
    Contains your custom beans (multipartConfigElement, 
    DispatcherServlet, etc).
    Defines main(...) so you can run the app as a standalone JAR too.
    */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        logger.debug("SimpleChatgptWebBootApplication.configure called");
        return application.sources(SimpleChatgptWebBootApplication.class);
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        logger.debug("SimpleChatgptWebBootApplication.onStartup called");
        super.onStartup(servletContext);
    }

    /* 
    ------------------------------
    hung: DONT REMOVE THIS COMMENT
    FROM web.xml: <multipart-config>
    Multipart Config
    ------------------------------
    Where the multipart error happens in request chain
    Spring filter order roughly:
    1>FilterChainProxy (Spring Security)
	2>JwtAuthenticationFilter
	3>DynamicAccessFilter
	4>DispatcherServlet (Spring MVC)
	The multipart parsing (StandardMultipartHttpServletRequest) happens 
	inside the DispatcherServlet, when it tries to parse the request body.
    */
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        logger.debug("SimpleChatgptWebBootApplication.multipartConfigElement called");
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setLocation("C:/apps/tmp");
        factory.setMaxFileSize(DataSize.ofBytes(10 * 1024 * 1024)); // 10MB
        factory.setMaxRequestSize(DataSize.ofBytes(20 * 1024 * 1024)); // 20MB
        factory.setFileSizeThreshold(DataSize.ofBytes(0));
        MultipartConfigElement mce = factory.createMultipartConfig();
        logger.debug("MultipartConfigElement created: {}", mce);
        return mce;
    }

    /*
    ------------------------------
    hung: DONT REMOVE THIS COMMENT
    Optional: register api DispatcherServlet path mapping similar to api-servlet.xml
    API DispatcherServlet registration
    ------------------------------
    You need to explicitly attach your multipart config to the servlet registration.
    Just modify it to inject and apply the MultipartConfigElement.
    */
    @Bean
    public ServletRegistrationBean<DispatcherServlet> apiDispatcherServlet(
    	DispatcherServlet dispatcherServlet,
        MultipartConfigElement multipartConfigElement) 
    {
        logger.debug("SimpleChatgptWebBootApplication.apiDispatcherServlet called");
        logger.debug("SimpleChatgptWebBootApplication.apiDispatcherServlet dispatcherServlet={}", dispatcherServlet);
        logger.debug("SimpleChatgptWebBootApplication.apiDispatcherServlet multipartConfigElement={}", multipartConfigElement);

        ServletRegistrationBean<DispatcherServlet> registration =
                new ServletRegistrationBean<>(dispatcherServlet, "/api/*");
        registration.setName("api");
        registration.setLoadOnStartup(1);

        // ✅ Attach multipart configuration here
        registration.setMultipartConfig(multipartConfigElement);

        logger.debug("DispatcherServlet registration with multipart config created: {}", registration);
        return registration;
    }

    // ------------------------------
    // hung: DONT REMOVE THIS COMMENT
    // Ensure default DispatcherServlet is NOT mapped to "/" to let Tomcat serve static/JSP
    // ------------------------------
    @Bean
    public DispatcherServlet dispatcherServlet() {
        logger.debug("SimpleChatgptWebBootApplication.dispatcherServlet called");
        DispatcherServlet dispatcherServlet = new DispatcherServlet();
        // Do not set any URL mapping here
        return dispatcherServlet;
    }

    // ------------------------------
    // Print all controllers on startup
    // ------------------------------
    @Bean
    CommandLineRunner printControllers(ApplicationContext ctx) {
        logger.debug("SimpleChatgptWebBootApplication.printControllers called");
        return args -> {
            String[] beans = ctx.getBeanNamesForAnnotation(org.springframework.stereotype.Controller.class);
            logger.debug("Controllers found: {}", Arrays.toString(beans));
        };
    }

    // ------------------------------
    // hung: DONT REMOVE THIS COMMENT
    // Mapper scan check (optional)
    // ------------------------------
    @PostConstruct
    public void checkMapperScan() {
        logger.debug("SimpleChatgptWebBootApplication.checkMapperScan called");
        String[] mapperBeans = context.getBeanNamesForType(org.apache.ibatis.mapping.MappedStatement.class);
        logger.debug("Mapper beans found: {}", Arrays.toString(mapperBeans));
    }
}
