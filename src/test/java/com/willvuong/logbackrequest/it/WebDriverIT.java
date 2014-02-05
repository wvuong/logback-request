package com.willvuong.logbackrequest.it;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

/**
 * Created with IntelliJ IDEA.
 * User: will
 * Date: 12/27/13
 * Time: 7:59 PM
 */
public class WebDriverIT extends AbstractWebDriverTest {

    private static final Logger logger = LoggerFactory.getLogger(WebDriverIT.class);

    @Test
    public void testLogOutputInHtmlResponse() throws Exception {
        String url = getBaseUrl();
        logger.info("making request to '{}'", url);
        webDriver.get(url);

        String title = webDriver.getTitle();
        assertThat(title, is("Bootstrap 101 Template"));

        String source = webDriver.getPageSource();
        assertThat(source, is(notNullValue()));

        JavascriptExecutor executor = (JavascriptExecutor) webDriver;
        Double length = (Double) executor.executeScript("return logged.length;");
        assertThat(length.intValue(), is(greaterThan(0)));
    }

    @Test
    public void testLogOutputInServletResponse() throws Exception {
        String url = getBaseUrl() + "/testservlet";
        logger.info("making request to '{}'", url);
        webDriver.get(url);

        String title = webDriver.getTitle();
        assertThat(title, is("TestServlet"));

        String source = webDriver.getPageSource();
        assertThat(source, is(notNullValue()));

        JavascriptExecutor executor = (JavascriptExecutor) webDriver;
        Double length = (Double) executor.executeScript("return logged.length;");
        assertThat(length.intValue(), is(greaterThan(0)));
    }

    @Test
    public void testLogOutputInForwardedRequest() throws Exception {
        String url = getBaseUrl() + "/forwardservlet";
        webDriver.get(url);

        String title = webDriver.getTitle();
        assertThat(title, is("WEB-INF/jsp/forward.jsp"));

        String source = webDriver.getPageSource();
        assertThat(source, is(notNullValue()));
        System.out.println(source);

        JavascriptExecutor executor = (JavascriptExecutor) webDriver;
        Double length = (Double) executor.executeScript("return logged.length;");
        assertThat(length.intValue(), is(greaterThan(0)));
    }
}
