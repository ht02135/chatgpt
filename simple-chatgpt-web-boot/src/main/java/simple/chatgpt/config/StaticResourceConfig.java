package simple.chatgpt.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // map all /static/** URLs to classpath:/static/
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }
}
