package simple.chatgpt.config;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Configuration
@EnableWebMvc  // Replaces: <mvc:annotation-driven>
public class WebConfig implements WebMvcConfigurer {

    private static final Logger logger = LogManager.getLogger(WebConfig.class);

    // FROM api-servlet.xml: <mvc:annotation-driven> JSON message converter
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        logger.debug("configureMessageConverters called");
        logger.debug("configureMessageConverters param converters={}", converters);

        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();

        jsonConverter.setSupportedMediaTypes(Arrays.asList(
            MediaType.APPLICATION_JSON,
            new MediaType("application", "json", StandardCharsets.UTF_8),
            new MediaType("text", "json")
        ));

        converters.add(jsonConverter);

        logger.debug("configureMessageConverters added MappingJackson2HttpMessageConverter");
    }

    // FROM api-servlet.xml: <mvc:cors>
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        logger.debug("addCorsMappings called");
        logger.debug("addCorsMappings param registry={}", registry);

        registry.addMapping("/api/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");

        logger.debug("addCorsMappings configured for /api/**");
    }

    /*
    Hung : DONT REMOVE
    this is only useful if controller return view and need to be
    resolved, but controller we have are get/return json for jsp
    to inject with knockoutjs. this is completely unused. we need
    to disable it by commenting out...
    */
    // FROM api-servlet.xml: View Resolver
    /*
    @Bean
    public ViewResolver viewResolver() {
        logger.debug("viewResolver called");

        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/jsp/");
        resolver.setSuffix(".jsp");

        logger.debug("viewResolver created with prefix=/WEB-INF/jsp/ and suffix=.jsp");
        return resolver;
    }
    */

    // FROM api-servlet.xml: Multipart Resolver
    @Bean
    public StandardServletMultipartResolver multipartResolver() {
        logger.debug("multipartResolver called");
        StandardServletMultipartResolver resolver = new StandardServletMultipartResolver();
        logger.debug("multipartResolver created");
        return resolver;
    }
}
