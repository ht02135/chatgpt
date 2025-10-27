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
import simple.chatgpt.selenium.pom.UserEditWDPage;
import simple.chatgpt.selenium.pom.UsersWDPage;

public class UserEditIntegrationTest {

    private static final Logger logger = LogManager.getLogger(UserEditIntegrationTest.class);

    private WebDriver driver;
    private UsersWDPage usersPage;
    private UserCreateWDPage createPage;
    private UserEditWDPage editPage;

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
    public void testEditUser(String baseUrl) {
        logger.debug("testEditUser ############");
        logger.debug("testEditUser called");
        logger.debug("testEditUser baseUrl={}", baseUrl);
        logger.debug("testEditUser ############");

        usersPage = new UsersWDPage(driver, baseUrl);
        logger.debug("usersPage initialized: {}", usersPage);

        createPage = new UserCreateWDPage(driver, baseUrl);
        logger.debug("createPage initialized: {}", createPage);

        editPage = new UserEditWDPage(driver, baseUrl);
        logger.debug("editPage initialized: {}", editPage);

        String username = "testSelenium@yahoo.com";
        String newFirstName = "Test updated";
        String newLastName = "Selenium updated";

        logger.debug("testEditUser username={}", username);
        logger.debug("testEditUser newFirstName={}", newFirstName);
        logger.debug("testEditUser newLastName={}", newLastName);

        // 1> go to users.jsp
        usersPage.gotoMyPage();
        assertTrue(usersPage.isInMyOwnPage(), "Expected to be on users.jsp after navigation");

        // 2> check if user exists, else create
        usersPage.enterSearchUsername(username);
        usersPage.clickSearch();

        if (!usersPage.isUserPresentInGrid(username)) {
            logger.debug("User {} not found, creating it first", username);

            usersPage.clickCreate();
            createPage.gotoMyPage();
            assertTrue(createPage.isInMyOwnPage(), "Expected to be on addUser.jsp after clicking Create");

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
            createPage.clickSave();

            usersPage.gotoMyPage();
            assertTrue(usersPage.isInMyOwnPage(), "Expected to return to users.jsp after saving new user");

            usersPage.enterSearchUsername(username);
            usersPage.clickSearch();
            usersPage.assertUserPresentInGrid(username);
        }

        // 3> click edit on that user
        usersPage.clickEditForUser(username);
        editPage.gotoMyPage();
        assertTrue(editPage.isInMyOwnPage(), "Expected to be on editUser.jsp after clicking Edit");

        // 4> update first name and last name, then save
        editPage.enterFirstName(newFirstName);
        editPage.enterLastName(newLastName);
        editPage.clickSave();

        usersPage.gotoMyPage();
        assertTrue(usersPage.isInMyOwnPage(), "Expected to return to users.jsp after saving edited user");

        // 5> verify user exists (by username)
        usersPage.enterSearchUsername(username);
        usersPage.clickSearch();
        usersPage.assertUserPresentInGrid(username);
        logger.debug("User {} verified in grid after edit", username);
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
