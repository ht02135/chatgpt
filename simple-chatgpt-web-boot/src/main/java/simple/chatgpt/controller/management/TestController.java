package simple.chatgpt.controller.management;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {

    private static final Logger logger = LogManager.getLogger(TestController.class);
    
    @GetMapping("/test")
    public void root(HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.debug("TestController: / endpoint called");

        String contextPath = request.getContextPath(); // dynamically gets /chatgpt-production
        logger.debug("TestController: contextPath={}", contextPath);

        response.sendRedirect(contextPath + "/public/this_is_test.html"); // no hardcoding
    }
}
