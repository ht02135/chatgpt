package simple.chatgpt.controller.management;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/*
hung: forward root / to index.html in /static folder
*/
@Controller
public class RootController {
	
	private static final Logger logger = LogManager.getLogger(RootController.class);
    
    @GetMapping("/")
    public void root(HttpServletResponse response) throws IOException {
        logger.debug("TestController: /test endpoint called");
        // let tomcat serve the page
    	response.sendRedirect("/chatgpt/index.html");
    }
}
