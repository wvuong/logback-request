package com.willvuong.logbackrequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: will
 * Date: 12/12/13
 * Time: 7:39 PM
 */
@WebServlet(urlPatterns = "/testservlet")
public class TestServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(TestServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.debug("TestServlet.doGet()");
        resp.setContentType("text/html");
        resp.getWriter().write("<html><head><title>TestServlet</title></head><body>Hello from servlet</body></html>");
        resp.getWriter().flush();
    }
}
