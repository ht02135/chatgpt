package simple.chatgpt.selenium.pom;

import java.time.Duration;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class AbstractWDPage {

    private static final Logger logger = LogManager.getLogger(AbstractWDPage.class);

    public static final long WAIT_TIME_IN_SECONDS = 5;

    protected WebDriver driver;
    protected String baseURL;
    protected WebDriverWait wait;

    public AbstractWDPage(WebDriver driver, String baseURL) {
        logger.debug("AbstractWDPage constructor called");
        logger.debug("driver={}", driver);
        logger.debug("baseURL={}", baseURL);

        this.driver = driver;
        this.baseURL = baseURL;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_TIME_IN_SECONDS));
    }

    public abstract void gotoMyPage();
    
    public abstract boolean isInMyOwnPage();

    public WebElement findElement(By locator) {
        logger.debug("findElement called");
        logger.debug("locator={}", locator);
        WebElement element = driver.findElement(locator);
        implicitWait(WAIT_TIME_IN_SECONDS);
        
        return element;
    }
    
    public List<WebElement> findElements(By locator) {
        logger.debug("findElements called");
        logger.debug("locator={}", locator);
        List<WebElement> elements = driver.findElements(locator);
        implicitWait(WAIT_TIME_IN_SECONDS);
        
        return elements;
    }
    
    public void implicitWait(long waitTimeInSeconds) {
    	logger.debug("implicitWait called");
    	logger.debug("implicitWait waitTimeInSeconds={}",waitTimeInSeconds);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(waitTimeInSeconds));
    }
    
    /*
    presenceOfElementLocated → exists in DOM only
	visibilityOfElementLocated → exists and visible
	elementToBeClickable → visible and enabled
	urlContains → URL contains a specific substring
    */
    
    /*
    wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    Waits until the element exists in the DOM, but it doesn’t have to be visible.
    WebElement input = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("username")));
	input.sendKeys("hung");
    */
    public WebElement explicitWaitForPresenceOfElement(By locator, long waitTimeInSeconds) {
    	logger.debug("explicitWaitForPresenceOfElement called");
    	logger.debug("explicitWaitForPresenceOfElement locator={}",locator);
    	logger.debug("explicitWaitForPresenceOfElement waitTimeInSeconds={}",waitTimeInSeconds);
    	WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(waitTimeInSeconds));
    	implicitWait(WAIT_TIME_IN_SECONDS);
    	
    	return customWait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }
    
    /*
    customWait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    Waits until the element is both in the DOM and visible (height & width > 0).
    WebElement message = customWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("successMsg")));
	System.out.println(message.getText());
    */
    public WebElement explicitWaitForVisibilityOfElement(By locator, long waitTimeInSeconds) {
    	logger.debug("explicitWaitForVisibilityOfElement called");
    	logger.debug("explicitWaitForVisibilityOfElement locator={}",locator);
    	logger.debug("explicitWaitForVisibilityOfElement waitTimeInSeconds={}",waitTimeInSeconds);
        WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(waitTimeInSeconds));
        implicitWait(WAIT_TIME_IN_SECONDS);
        
        return customWait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }
    
    /*
    wait.until(ExpectedConditions.elementToBeClickable(locator));
    Waits until the element is both visible and enabled, meaning 
    you can click it safely.
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
	WebElement button = wait.until(ExpectedConditions.elementToBeClickable(By.id("submitBtn")));
	button.click();
	*/
    public WebElement explicitWaitForElementToBeClickable(By locator, long waitTimeInSeconds) {
    	logger.debug("explicitWaitForElementToBeClickable called");
    	logger.debug("explicitWaitForElementToBeClickable locator={}",locator);
    	logger.debug("explicitWaitForElementToBeClickable waitTimeInSeconds={}",waitTimeInSeconds);
        WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(waitTimeInSeconds));
        implicitWait(WAIT_TIME_IN_SECONDS);
        
        return customWait.until(ExpectedConditions.elementToBeClickable(locator));
    }
    
    /*
    customWait.until(ExpectedConditions.urlContains(urlFragment));
    Waits until the current URL contains the given substring.
    customWait.until(ExpectedConditions.urlContains("dashboard"));
    */
    public boolean explicitWaitForUrlContains(String urlFragment, long waitTimeInSeconds) {
    	logger.debug("explicitWaitForUrlContains called");
    	logger.debug("explicitWaitForUrlContains urlFragment={}",urlFragment);
    	logger.debug("explicitWaitForUrlContains waitTimeInSeconds={}",waitTimeInSeconds);
        WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(waitTimeInSeconds));
        implicitWait(WAIT_TIME_IN_SECONDS);
        
        return customWait.until(ExpectedConditions.urlContains(urlFragment));
    }
    
    public String getCurrentUrl() {
        logger.debug("getCurrentUrl called");
        String currentUrl = driver.getCurrentUrl();
        logger.debug("currentUrl={}", currentUrl);
        implicitWait(WAIT_TIME_IN_SECONDS);

        return currentUrl;
    }

    
    public void handleAlertIfPresent() {
        logger.debug("handleAlertIfPresent called");
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            logger.debug("Alert text={}", alert.getText());
            alert.accept();
            logger.debug("Alert accepted");
        } catch (Exception e) {
        	logger.debug("No alert present, exception={}", e.getMessage());
        }
    }
}
