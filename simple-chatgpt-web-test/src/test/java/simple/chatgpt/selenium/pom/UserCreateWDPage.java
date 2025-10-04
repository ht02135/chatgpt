package simple.chatgpt.selenium.pom;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class UserCreateWDPage extends AbstractWDPage {

	private static final Logger logger = LogManager.getLogger(UserCreateWDPage.class);

	public static final String USER_CREATE_PAGE = "addUser.jsp";

	private final String myPageUrl;

	public UserCreateWDPage(WebDriver driver, String baseURL) {
		super(driver, baseURL);
		logger.debug("UserCreateWDPage constructor called");
		logger.debug("driver={}", driver);
		logger.debug("baseURL={}", baseURL);

		this.myPageUrl = baseURL + USER_CREATE_PAGE;
		logger.debug("myPageUrl={}", myPageUrl);
	}

	@Override
	public void gotoMyPage() {
		logger.debug("gotoMyPage called");
		driver.get(myPageUrl);
		logger.debug("Navigated to URL={}", myPageUrl);
		explicitWaitForUrlContains(USER_CREATE_PAGE, WAIT_TIME_IN_SECONDS);
		implicitWait(WAIT_TIME_IN_SECONDS);
		
		logger.debug("Confirmed URL contains '{}'", USER_CREATE_PAGE);
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

	public void enterUsername(String username) {
		logger.debug("enterUsername called");
		logger.debug("username={}", username);

		if (!isInMyOwnPage()) {
			logger.debug("Not on addUser.jsp page");
			return;
		}

		WebElement element = explicitWaitForVisibilityOfElement(
				By.xpath("//label[text()='Username:']/following-sibling::input"), WAIT_TIME_IN_SECONDS);
		implicitWait(WAIT_TIME_IN_SECONDS);
		element.clear();
		element.sendKeys(username);
	}

	public void enterUserKey(String userKey) {
		logger.debug("enterUserKey called");
		logger.debug("userKey={}", userKey);

		if (!isInMyOwnPage()) {
			logger.debug("Not on addUser.jsp page");
			return;
		}

		WebElement element = explicitWaitForVisibilityOfElement(
				By.xpath("//label[text()='User Key:']/following-sibling::input"), WAIT_TIME_IN_SECONDS);
		implicitWait(WAIT_TIME_IN_SECONDS);
		element.clear();
		element.sendKeys(userKey);
	}

	public void enterFirstName(String firstName) {
		logger.debug("enterFirstName called");
		logger.debug("firstName={}", firstName);

		if (!isInMyOwnPage()) {
			logger.debug("Not on addUser.jsp page");
			return;
		}

		WebElement element = explicitWaitForVisibilityOfElement(
				By.xpath("//label[text()='First Name:']/following-sibling::input"), WAIT_TIME_IN_SECONDS);
		implicitWait(WAIT_TIME_IN_SECONDS);
		element.clear();
		element.sendKeys(firstName);
	}

	public void enterLastName(String lastName) {
		logger.debug("enterLastName called");
		logger.debug("lastName={}", lastName);

		if (!isInMyOwnPage()) {
			logger.debug("Not on addUser.jsp page");
			return;
		}

		WebElement element = explicitWaitForVisibilityOfElement(
				By.xpath("//label[text()='Last Name:']/following-sibling::input"), WAIT_TIME_IN_SECONDS);
		implicitWait(WAIT_TIME_IN_SECONDS);
		element.clear();
		element.sendKeys(lastName);
	}

	public void enterEmail(String email) {
		logger.debug("enterEmail called");
		logger.debug("email={}", email);

		if (!isInMyOwnPage()) {
			logger.debug("Not on addUser.jsp page");
			return;
		}

		WebElement element = explicitWaitForVisibilityOfElement(
				By.xpath("//label[text()='Email:']/following-sibling::input"), WAIT_TIME_IN_SECONDS);
		implicitWait(WAIT_TIME_IN_SECONDS);
		element.clear();
		element.sendKeys(email);
	}

	public void enterPassword(String password) {
		logger.debug("enterPassword called");
		logger.debug("password={}", password);

		if (!isInMyOwnPage()) {
			logger.debug("Not on addUser.jsp page");
			return;
		}

		WebElement element = explicitWaitForVisibilityOfElement(
				By.xpath("//div[@class='form-row'][label[text()='Password:']]/input"), WAIT_TIME_IN_SECONDS);
		implicitWait(WAIT_TIME_IN_SECONDS);
		element.clear();
		element.sendKeys(password);
	}

	public void enterAddress1(String address1) {
		logger.debug("enterAddress1 called");
		logger.debug("address1={}", address1);

		if (!isInMyOwnPage()) {
			logger.debug("Not on addUser.jsp page");
			return;
		}

		WebElement element = explicitWaitForVisibilityOfElement(
				By.xpath("//label[text()='Address Line 1:']/following-sibling::input"), WAIT_TIME_IN_SECONDS);
		implicitWait(WAIT_TIME_IN_SECONDS);
		element.clear();
		element.sendKeys(address1);
	}

	public void enterAddress2(String address2) {
		logger.debug("enterAddress2 called");
		logger.debug("address2={}", address2);

		if (!isInMyOwnPage()) {
			logger.debug("Not on addUser.jsp page");
			return;
		}

		WebElement element = explicitWaitForVisibilityOfElement(
				By.xpath("//label[text()='Address Line 2:']/following-sibling::input"), WAIT_TIME_IN_SECONDS);
		implicitWait(WAIT_TIME_IN_SECONDS);
		element.clear();
		element.sendKeys(address2);
	}

	public void enterCity(String city) {
		logger.debug("enterCity called");
		logger.debug("city={}", city);

		if (!isInMyOwnPage()) {
			logger.debug("Not on addUser.jsp page");
			return;
		}

		WebElement element = explicitWaitForVisibilityOfElement(
				By.xpath("//label[text()='City:']/following-sibling::input"), WAIT_TIME_IN_SECONDS);
		implicitWait(WAIT_TIME_IN_SECONDS);
		element.clear();
		element.sendKeys(city);
	}

	public void enterState(String state) {
		logger.debug("enterState called");
		logger.debug("state={}", state);

		if (!isInMyOwnPage()) {
			logger.debug("Not on addUser.jsp page");
			return;
		}

		WebElement element = explicitWaitForVisibilityOfElement(
				By.xpath("//label[text()='State:']/following-sibling::input"), WAIT_TIME_IN_SECONDS);
		implicitWait(WAIT_TIME_IN_SECONDS);
		element.clear();
		element.sendKeys(state);
	}

	public void enterPostCode(String postCode) {
		logger.debug("enterPostCode called");
		logger.debug("postCode={}", postCode);

		if (!isInMyOwnPage()) {
			logger.debug("Not on addUser.jsp page");
			return;
		}

		WebElement element = explicitWaitForVisibilityOfElement(
				By.xpath("//label[text()='Post Code:']/following-sibling::input"), WAIT_TIME_IN_SECONDS);
		implicitWait(WAIT_TIME_IN_SECONDS);
		element.clear();
		element.sendKeys(postCode);
	}

	public void enterCountry(String country) {
		logger.debug("enterCountry called");
		logger.debug("country={}", country);

		if (!isInMyOwnPage()) {
			logger.debug("Not on addUser.jsp page");
			return;
		}

		WebElement element = explicitWaitForVisibilityOfElement(
				By.xpath("//label[text()='Country:']/following-sibling::input"), WAIT_TIME_IN_SECONDS);
		implicitWait(WAIT_TIME_IN_SECONDS);
		element.clear();
		element.sendKeys(country);
	}
	
    public void clickSave() {
        logger.debug("clickSave called");
        
		if (!isInMyOwnPage()) {
			logger.debug("Not on addUser.jsp page");
			return;
		}
		implicitWait(WAIT_TIME_IN_SECONDS);
		
        if (isSaveLinkPresent()) {     
        	WebElement element = explicitWaitForVisibilityOfElement(By.linkText("Save"), WAIT_TIME_IN_SECONDS);
        	implicitWait(WAIT_TIME_IN_SECONDS);
        	logger.debug("clickSave ############");
        	logger.debug("clickSave element={}",element);
        	logger.debug("clickSave ############");
        	element.click();
            handleAlertIfPresent();
        }
        implicitWait(WAIT_TIME_IN_SECONDS);
    }
    
    public boolean isSaveLinkPresent() {
        logger.debug("isSaveLinkPresent called");
        
		if (!isInMyOwnPage()) {
			logger.debug("Not on addUser.jsp page");
			return false;
		}
		implicitWait(WAIT_TIME_IN_SECONDS);
        
        try {
        	WebElement element = explicitWaitForVisibilityOfElement(By.linkText("Save"), WAIT_TIME_IN_SECONDS);
        	implicitWait(WAIT_TIME_IN_SECONDS);
        	logger.debug("isSaveLinkPresent ############");
        	logger.debug("isSaveLinkPresent element={}", element);
        	logger.debug("isSaveLinkPresent element.isDisplayed()={}", element.isDisplayed());
        	logger.debug("isSaveLinkPresent ############");
        	return element.isDisplayed();
        } catch (Exception e) {
        	logger.debug("isSaveLinkPresent ############");
        	logger.debug("isSaveLinkPresent Exception e={}", e);
        	logger.debug("isSaveLinkPresent ############");
            return false;
        }
    }
}
