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

import simple.chatgpt.selenium.pom.UserCreateWDPage;
import simple.chatgpt.selenium.pom.UsersWDPage;

public class UserCreateIntegrationTest {

    private static final Logger logger = LogManager.getLogger(UserCreateIntegrationTest.class);

    private WebDriver driver;
    private UsersWDPage usersPage;
    private UserCreateWDPage createPage;

    @BeforeEach
    public void setUp() {
        logger.debug("setUp called");
        driver = new FirefoxDriver();
        logger.debug("WebDriver initialized: driver={}", driver);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "http://localhost:8080/chatgpt/management/jsp/user/",
        "http://localhost:8080/chatgpt-production/management/jsp/user/"
    })
    public void testCreateUser(String baseUrl) {
        logger.debug("testCreateUser ############");
        logger.debug("testCreateUser called");
        logger.debug("testCreateUser baseUrl={}", baseUrl);
        logger.debug("testCreateUser ############");

        usersPage = new UsersWDPage(driver, baseUrl);
        logger.debug("usersPage initialized: {}", usersPage);

        createPage = new UserCreateWDPage(driver, baseUrl);
        logger.debug("createPage initialized: {}", createPage);

        String username = "testSelenium@yahoo.com";
        logger.debug("testCreateUser username={}", username);

        // 1> go to users.jsp
        usersPage.gotoMyPage();
        assertTrue(usersPage.isInMyOwnPage(), "Expected to be on users.jsp after navigation");

        // 2> ensure user does not already exist
        usersPage.enterSearchUsername(username);
        usersPage.clickSearch();
        if (usersPage.isUserPresentInGrid(username)) {
            logger.debug("User {} already exists, deleting before create", username);
            usersPage.clickDeleteForUser(username);

            // verify deletion
            usersPage.enterSearchUsername(username);
            usersPage.clickSearch();
            usersPage.assertUserNotPresentInGrid(username);
        }

        // 3> click Create
        usersPage.clickCreate();
        createPage.gotoMyPage();
        assertTrue(createPage.isInMyOwnPage(), "Expected to be on addUser.jsp after clicking Create");

        // 4> fill in form fields
        createPage.enterUsername(username);
        createPage.enterUserKey(username);
        createPage.enterFirstName("Test");
        createPage.enterLastName("Selenium");
        createPage.enterEmail(username);
        createPage.enterPassword("ZAQ!zaq1");
        createPage.enterAddress1("123 Selenium Street");
        createPage.enterAddress2("Suite 456");
        createPage.enterCity("TestCity");
        createPage.enterState("TS");
        createPage.enterPostCode("99999");
        createPage.enterCountry("USA");

        // 5> click Save
        createPage.clickSave();
        usersPage.gotoMyPage();
        assertTrue(usersPage.isInMyOwnPage(), "Expected to return to users.jsp after saving");

        // 6> verify user was created
        usersPage.enterSearchUsername(username);
        usersPage.clickSearch();
        usersPage.assertUserPresentInGrid(username);
        logger.debug("User {} successfully created and verified", username);
    }

    @AfterEach
    public void tearDown() {
        logger.debug("tearDown called");
        if (driver != null) {
            driver.quit();
            logger.debug("WebDriver quit successfully");
        }
    }
}
