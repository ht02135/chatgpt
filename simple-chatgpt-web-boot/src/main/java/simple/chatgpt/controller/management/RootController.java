package simple.chatgpt.controller.management;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
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
	
	public static final String INDEX_FILE     = "/index.jsp";
    
    @GetMapping("/")
    public void root(HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.debug("RootController: / endpoint called");

        String contextPath = request.getContextPath(); // dynamically gets /chatgpt-production
        logger.debug("RootController: contextPath={}", contextPath);
        
        logger.debug("RootController: sendRedirect=", contextPath + "/index.html");
        response.sendRedirect(contextPath + INDEX_FILE); // no hardcoding
    }
}
