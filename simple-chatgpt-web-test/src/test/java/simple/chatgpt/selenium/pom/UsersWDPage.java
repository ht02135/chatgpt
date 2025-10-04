package simple.chatgpt.selenium.pom;

import java.util.List;
import java.util.NoSuchElementException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class UsersWDPage extends AbstractWDPage {

	private static final Logger logger = LogManager.getLogger(UsersWDPage.class);

	public static final String USERS_PAGE = "users.jsp";

	private final String myPageUrl;

	public UsersWDPage(WebDriver driver, String baseURL) {
	    super(driver, baseURL);
	    logger.debug("UsersWDPage constructor called"); // ← updated
	    logger.debug("driver={}", driver);
	    logger.debug("baseURL={}", baseURL);

	    this.myPageUrl = baseURL + USERS_PAGE;
	    logger.debug("myPageUrl={}", myPageUrl);
	}

	@Override
	public void gotoMyPage() {
		logger.debug("gotoMyPage called");
		driver.get(myPageUrl);
		logger.debug("Navigated to URL={}", myPageUrl);
		explicitWaitForUrlContains(USERS_PAGE, WAIT_TIME_IN_SECONDS);
		implicitWait(WAIT_TIME_IN_SECONDS);

		logger.debug("Confirmed URL contains '{}'", USERS_PAGE);
	}

	@Override
	public boolean isInMyOwnPage() {
		logger.debug("isInMyOwnPage called");
		String currentUrl = getCurrentUrl();
		implicitWait(WAIT_TIME_IN_SECONDS);

		boolean result = currentUrl.equals(myPageUrl);
		logger.debug("Current URL={}", currentUrl);
		logger.debug("isInMyOwnPage result={}", result);
		implicitWait(WAIT_TIME_IN_SECONDS);

		return result;
	}

	public void enterSearchUsername(String username) {
		logger.debug("enterSearchUsername called");
		logger.debug("enterSearchUsername username={}", username);

		if (!isInMyOwnPage()) {
			logger.debug("Not on users.jsp page");
			return;
		}
		implicitWait(WAIT_TIME_IN_SECONDS);

		WebElement element = explicitWaitForPresenceOfElement(By.id("userName"), WAIT_TIME_IN_SECONDS);
		logger.debug("isInMyOwnPage element={}", element);
		implicitWait(WAIT_TIME_IN_SECONDS);

		element.clear();
		element.sendKeys(username);
	}

	public boolean isSearchLinkPresent() {
		logger.debug("isSearchLinkPresent called");

		if (!isInMyOwnPage()) {
			logger.debug("Not on users.jsp page");
			return false;
		}
		implicitWait(WAIT_TIME_IN_SECONDS);

		try {
			WebElement element = explicitWaitForVisibilityOfElement(By.linkText("Search"), WAIT_TIME_IN_SECONDS);
			implicitWait(WAIT_TIME_IN_SECONDS);
			logger.debug("isSearchLinkPresent ############");
			logger.debug("isSearchLinkPresent element={}", element);
			logger.debug("isSearchLinkPresent element.isDisplayed()={}", element.isDisplayed());
			logger.debug("isSearchLinkPresent ############");
			return element.isDisplayed();
		} catch (Exception e) {
			logger.debug("isSearchLinkPresent ############");
			logger.debug("isSearchLinkPresent Exception e={}", e);
			logger.debug("isSearchLinkPresent ############");
			return false;
		}
	}

	public void clickSearch() {
		logger.debug("clickSearch called");

		if (!isInMyOwnPage()) {
			logger.debug("Not on users.jsp page");
			return;
		}
		implicitWait(WAIT_TIME_IN_SECONDS);

		if (isSearchLinkPresent()) {
			WebElement element = explicitWaitForVisibilityOfElement(By.linkText("Search"), WAIT_TIME_IN_SECONDS);
			implicitWait(WAIT_TIME_IN_SECONDS);
			logger.debug("clickSearch ############");
			logger.debug("clickSearch element={}", element);
			logger.debug("clickSearch ############");
			element.click();
			handleAlertIfPresent();
		}
		implicitWait(WAIT_TIME_IN_SECONDS);
	}

	// pure check, safe for setup (don’t care if missing).
	public boolean isUserPresentInGrid(String username) {
		logger.debug("isUserPresentInGrid called");
		logger.debug("username={}", username);

		if (!isInMyOwnPage()) {
			logger.debug("Not on users.jsp page");
			return false;
		}
		implicitWait(WAIT_TIME_IN_SECONDS);

		try {
			WebElement element = explicitWaitForVisibilityOfElement(By.xpath("//td[text()='" + username + "']"),
					WAIT_TIME_IN_SECONDS);
			logger.debug("isUserPresentInGrid: #############");
			logger.debug("isUserPresentInGrid: element={}", element);
			logger.debug("isUserPresentInGrid: element.isDisplayed()={}", element.isDisplayed());
			logger.debug("isUserPresentInGrid: #############");
			return element.isDisplayed();
		} catch (Exception e) {
			logger.debug("User {} not found in grid", username);
			return false;
		}
	}

	// use when you expect the user must exist.
	public void assertUserPresentInGrid(String username) {
		if (!isInMyOwnPage()) {
			logger.debug("Not on users.jsp page");
			return;
		}

		if (!isUserPresentInGrid(username)) {
			logger.debug("assertUserPresentInGrid: #############");
			logger.debug("assertUserPresentInGrid : Expected user {} to be present in grid, but it is missing", username);
			logger.debug("assertUserPresentInGrid: #############");
			throw new AssertionError("Expected user " + username + " to be present in grid, but it is missing");
		}

	}

	// use when you expect the user must be gone.
	public void assertUserNotPresentInGrid(String username) {
		if (!isInMyOwnPage()) {
			logger.debug("Not on users.jsp page");
			return;
		}

		if (isUserPresentInGrid(username)) {
			logger.debug("assertUserNotPresentInGrid: #############");
			logger.debug("assertUserNotPresentInGrid: Expected user {} to be absent from grid, but it is present", username);
			logger.debug("assertUserNotPresentInGrid: #############");
			throw new AssertionError("Expected user " + username + " to be absent from grid, but it is present");
		}
	}

	public void clickCreate() {
		logger.debug("clickCreate called");

		if (!isInMyOwnPage()) {
			logger.debug("Not on users.jsp page");
			return;
		}
		implicitWait(WAIT_TIME_IN_SECONDS);

		if (isCreateLinkPresent()) {
			WebElement element = explicitWaitForVisibilityOfElement(By.linkText("Create"), WAIT_TIME_IN_SECONDS);
			implicitWait(WAIT_TIME_IN_SECONDS);
			logger.debug("clickCreate ############");
			logger.debug("clickCreate element={}", element);
			logger.debug("clickCreate ############");
			element.click();
			handleAlertIfPresent();
		}
		implicitWait(WAIT_TIME_IN_SECONDS);
	}

	public boolean isCreateLinkPresent() {
		logger.debug("isCreateLinkPresent called");

		if (!isInMyOwnPage()) {
			logger.debug("Not on users.jsp page");
			return false;
		}
		implicitWait(WAIT_TIME_IN_SECONDS);

		try {
			WebElement element = explicitWaitForVisibilityOfElement(By.linkText("Create"), WAIT_TIME_IN_SECONDS);
			implicitWait(WAIT_TIME_IN_SECONDS);
			logger.debug("isCreateLinkPresent ############");
			logger.debug("isCreateLinkPresent element={}", element);
			logger.debug("isCreateLinkPresent element.isDisplayed()={}", element.isDisplayed());
			logger.debug("isCreateLinkPresent ############");
			return element.isDisplayed();
		} catch (Exception e) {
			logger.debug("isCreateLinkPresent ############");
			logger.debug("isCreateLinkPresent Exception e={}", e);
			logger.debug("isCreateLinkPresent ############");
			return false;
		}
	}

	public void clickDeleteForUser(String username) {
		logger.debug("clickDeleteForUser called");
		logger.debug("clickDeleteForUser username={}", username);

		if (!isInMyOwnPage()) {
			logger.debug("Not on users.jsp page");
			return;
		}
		implicitWait(WAIT_TIME_IN_SECONDS);

		logger.debug("clickDeleteForUser ############");
		logger.debug("clickDeleteForUser on users.jsp page");
		logger.debug("clickDeleteForUser ############");

		try {
			// 1> find the row for the user by username
			WebElement row = userGridRowByUsername(username);
			implicitWait(WAIT_TIME_IN_SECONDS);
			logger.debug("clickDeleteForUser row={}", row);

			// 2> find the Delete link in that row
			WebElement deleteLink = row.findElement(By.linkText("Delete"));
			implicitWait(WAIT_TIME_IN_SECONDS);
			logger.debug("clickDeleteForUser ############");
			logger.debug("clickDeleteForUser deleteLink={}", deleteLink);
			logger.debug("clickDeleteForUser ############");

			// 3> click it
			deleteLink.click();
			handleAlertIfPresent();
			implicitWait(WAIT_TIME_IN_SECONDS);
			logger.debug("clickDeleteForUser clicked Delete for user {}", username);

			// optional: handle confirmation alert if your app has one
			// driver.switchTo().alert().accept();
		} catch (Exception e) {
			logger.debug("clickDeleteForUser Exception e={}", e);
			throw new RuntimeException("Delete link not found for user: " + username, e);
		}
		implicitWait(WAIT_TIME_IN_SECONDS);
	}

	public void clickEditForUser(String username) {
		logger.debug("clickEditForUser called");
		logger.debug("username={}", username);

		if (!isInMyOwnPage()) {
			logger.debug("Not on users.jsp page");
			return;
		}
		implicitWait(WAIT_TIME_IN_SECONDS);

		WebElement row = userGridRowByUsername(username);
		implicitWait(WAIT_TIME_IN_SECONDS);
		logger.debug("clickEditForUser row={}", row);
		
		WebElement editLink = row.findElement(By.linkText("Edit"));
		implicitWait(WAIT_TIME_IN_SECONDS);
		logger.debug("clickEditForUser editLink={}", editLink);
		editLink.click();
		// explicitWaitUntilUrlContains("editUser.jsp",WAIT_TIME_IN_SECONDS);
		handleAlertIfPresent();
		implicitWait(WAIT_TIME_IN_SECONDS);
	}

	/*
	 * hung: helper to find the row for a user in the grid by username
	 */
	private WebElement userGridRowByUsername(String username) {
		logger.debug("userGridRowByUsername called");
		logger.debug("userGridRowByUsername ############");
		logger.debug("userGridRowByUsername username={}", username);
		logger.debug("userGridRowByUsername ############");

		List<WebElement> rows = findElements(By.cssSelector("#usersPage table tbody tr"));
		implicitWait(WAIT_TIME_IN_SECONDS);
		for (WebElement row : rows) {
			WebElement usernameCell = row.findElement(By.xpath(".//td[contains(text(),'" + username + "')]"));
			implicitWait(WAIT_TIME_IN_SECONDS);
			if (usernameCell != null) {
				logger.debug("userGridRowByUsername ############");
				logger.debug("userGridRowByUsername found row={}", row);
				logger.debug("userGridRowByUsername ############");
				return row;
			}
		}
		throw new NoSuchElementException("No row found for username: " + username);
	}
}
