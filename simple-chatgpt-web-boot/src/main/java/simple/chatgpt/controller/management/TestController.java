package simple.chatgpt.controller.management;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {

    private static final Logger logger = LogManager.getLogger(TestController.class);

    @GetMapping("/test")
    public void testPage(HttpServletResponse response) throws IOException {
        logger.debug("TestController: /test endpoint called");
        // let tomcat serve the page
        response.sendRedirect("/chatgpt/this_is_test.html");
    }
}
