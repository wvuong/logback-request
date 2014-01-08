package com.willvuong.logbackrequest.it;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

/**
 * Created with IntelliJ IDEA.
 * User: will
 * Date: 12/27/13
 * Time: 7:59 PM
 */
public abstract class AbstractWebDriverTest {

    protected static WebDriver webDriver;

    @BeforeClass
    public static void initWebDriver() {
        webDriver = new HtmlUnitDriver(true);
    }

    @AfterClass
    public static void closeWebDriver() {
        if (webDriver != null) {
            webDriver.quit();
        }
    }

    protected String getBaseUrl() {
        return System.getProperty("url", "http://localhost:8080/it");
    }
}
