package simple.chatgpt;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

public class SimplePlaywrightTutorial {
    private static final Logger logger = LogManager.getLogger(SimplePlaywrightTutorial.class);

    public static void main(String[] args) {
        logger.debug("main called");

        try (Playwright playwright = Playwright.create()) {
            logger.debug("Playwright created");

            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions().setHeadless(false));
            logger.debug("Browser launched: {}", browser);

            BrowserContext context = browser.newContext();
            logger.debug("Browser context created: {}", context);

            Page page = context.newPage();
            logger.debug("Page created: {}", page);

            page.navigate("https://playwright.dev");
            logger.debug("Navigated to Playwright.dev");

            String title = page.title();
            logger.debug("Page title={}", title);

            System.out.println("Page title: " + title);

            browser.close();
            logger.debug("Browser closed");
        }
    }
}
