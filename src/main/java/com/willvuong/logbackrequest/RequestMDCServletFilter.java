package com.willvuong.logbackrequest;

import org.slf4j.MDC;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: will
 * Date: 11/25/13
 * Time: 8:46 AM
 */
public class RequestMDCServletFilter implements Filter {
    public static final String REQUESTURL = "REQUESTURL";
    public static final String REQUESTURI = "REQUESTURI";
    public static final String REQUESTID = "REQUESTID";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        MDC.put(REQUESTURL, request.getRequestURL().toString());
        MDC.put(REQUESTURI, request.getRequestURI());
        MDC.put(REQUESTID, UUID.randomUUID().toString());

        try {
            chain.doFilter(request, response);
        }
        finally {
            MDC.clear();
        }
    }

    @Override
    public void destroy() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
