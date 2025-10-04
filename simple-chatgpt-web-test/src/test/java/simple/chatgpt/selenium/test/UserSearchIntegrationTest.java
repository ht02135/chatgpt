package simple.chatgpt.selenium.test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import simple.chatgpt.selenium.pom.UsersWDPage;

public class UserSearchIntegrationTest {

    private static final Logger logger = LogManager.getLogger(UserSearchIntegrationTest.class);

    private WebDriver driver;
    private UsersWDPage usersPage;

    @BeforeEach
    public void setUp() {
        logger.debug("setUp called");
        driver = new FirefoxDriver();
        logger.debug("driver={}", driver);
    }

    /*
     * This will automatically run the test for each baseUrl in @ValueSource
     */
    @ParameterizedTest
    @ValueSource(strings = {
        "http://localhost:8080/chatgpt/management/jsp/user/",
        "http://localhost:8080/chatgpt-production/management/jsp/user/"
    })
    public void testSearchUserByUsername(String baseUrl) {
        logger.debug("testSearchUserByUsername ############");
        logger.debug("testSearchUserByUsername called");
        logger.debug("param baseUrl={}", baseUrl);
        logger.debug("testSearchUserByUsername ############");

        usersPage = new UsersWDPage(driver, baseUrl);
        logger.debug("UsersWDPage initialized");
        logger.debug("UsersWDPage param driver={}", driver);
        logger.debug("UsersWDPage param baseUrl={}", baseUrl);
        logger.debug("usersPage instance={}", usersPage);

        String username = "ht02135@yahoo.com";
        logger.debug("username={}", username);

        // 1> go to users.jsp
        usersPage.gotoMyPage();
        logger.debug("called usersPage.gotoMyPage()");
        assertTrue(usersPage.isInMyOwnPage(), "Expected to be on users.jsp after navigation");
        logger.debug("isInMyOwnPage()={}", usersPage.isInMyOwnPage());

        // 2> enter Username
        usersPage.enterSearchUsername(username);
        logger.debug("called usersPage.enterSearchUsername()");
        logger.debug("param username={}", username);

        // 3> click search
        usersPage.clickSearch();
        logger.debug("called usersPage.clickSearch()");
        assertTrue(usersPage.isInMyOwnPage(), "Expected to remain on users.jsp after searching");
        logger.debug("isInMyOwnPage() after search={}", usersPage.isInMyOwnPage());

        // 4> verify user appears in grid
        usersPage.assertUserPresentInGrid(username);
        logger.debug("called usersPage.assertUserPresentInGrid()");
        logger.debug("param username={}", username);
        logger.debug("User {} verified in grid for baseUrl={}", username, baseUrl);
    }

    @AfterEach
    public void tearDown() {
        logger.debug("tearDown called");
        if (driver != null) {
            driver.quit();
            logger.debug("driver.quit() called successfully");
        } else {
            logger.debug("driver is null, nothing to quit");
        }
    }
}
