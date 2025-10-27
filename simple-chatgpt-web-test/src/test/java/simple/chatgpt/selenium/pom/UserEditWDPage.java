package simple.chatgpt.selenium.pom;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class UserEditWDPage extends AbstractWDPage {

    private static final Logger logger = LogManager.getLogger(UserEditWDPage.class);

    public static final String USER_EDIT_PAGE = "editUser.jsp";
    
    private final String myPageUrl;

    public UserEditWDPage(WebDriver driver, String baseURL) {
        super(driver, baseURL);
        logger.debug("UserEditWDPage constructor called");
        logger.debug("driver={}", driver);
        logger.debug("baseURL={}", baseURL);

        this.myPageUrl = baseURL + USER_EDIT_PAGE;
        logger.debug("myPageUrl={}", myPageUrl);
    }

    @Override
    public void gotoMyPage() {
        logger.debug("gotoMyPage called");
        driver.get(myPageUrl);
        logger.debug("Navigated to URL={}", myPageUrl);
        explicitWaitForUrlContains(USER_EDIT_PAGE, WAIT_TIME_IN_SECONDS);
        implicitWait(WAIT_TIME_IN_SECONDS);
        
        logger.debug("Confirmed URL contains '{}'", USER_EDIT_PAGE);
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
    
    public void clickSave() {
        logger.debug("clickSave called");
        
		if (!isInMyOwnPage()) {
			logger.debug("Not on editUser.jsp page");
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
			logger.debug("Not on editUser.jsp page");
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
    
    public void enterFirstName(String firstName) {
        logger.debug("enterFirstName called");
        logger.debug("firstName={}", firstName);
        
		if (!isInMyOwnPage()) {
			logger.debug("Not on editUser.jsp page");
			return;
		}
		implicitWait(WAIT_TIME_IN_SECONDS);
		
        WebElement element = findElement(By.xpath("//label[text()='First Name:']/following-sibling::input"));
        element.clear();
        element.sendKeys(firstName);
    }

    public void enterLastName(String lastName) {
        logger.debug("enterLastName called");
        logger.debug("lastName={}", lastName);
        
		if (!isInMyOwnPage()) {
			logger.debug("Not on editUser.jsp page");
			return;
		}
		implicitWait(WAIT_TIME_IN_SECONDS);
		
        WebElement element = findElement(By.xpath("//label[text()='Last Name:']/following-sibling::input"));
        element.clear();
        element.sendKeys(lastName);
    }
}
