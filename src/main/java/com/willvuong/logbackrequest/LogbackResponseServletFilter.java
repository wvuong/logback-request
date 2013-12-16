package com.willvuong.logbackrequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created with IntelliJ IDEA.
 * User: will
 * Date: 11/24/13
 * Time: 5:08 PM
 */
public class LogbackResponseServletFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(LogbackResponseServletFilter.class);

    private static final String ALREADY_APPLIED = LogbackResponseServletFilter.class.getName() + ".applied";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        // check to see if the request has already been decorated
        if (request.getAttribute(ALREADY_APPLIED) == Boolean.TRUE) {
            // already decorated, so skip
            chain.doFilter(request, response);
            ThreadLocalMemoryAppender.ThreadLocalHolder.clearLoggedEvents();
            return;
        }
        else {
            request.setAttribute(ALREADY_APPLIED, Boolean.TRUE);
        }

        // see if we actually should decorate this type of request based on the file name
        boolean disabled = "true".equals(request.getParameter("disabled"));
        String url = request.getRequestURL().toString();
        boolean notHtml = url.endsWith(".js") || url.endsWith(".css") || url.endsWith(".ico");

        if (disabled || notHtml) {
            logger.trace("skipping decoration because disabled: {}, notHtml: {}, contentType: {}", disabled, notHtml, resp.getContentType());
            chain.doFilter(request, response);
            ThreadLocalMemoryAppender.ThreadLocalHolder.clearLoggedEvents();
            return;
        }

        // create a response buffer and doFilter
        CharResponseWrapper wrapper = new CharResponseWrapper(response);
        try {
            chain.doFilter(request, wrapper);
        }
        finally {
            // see if we actually should decorate this type of request based on the response content type
            boolean doFilter = wrapper.getContentType() != null && wrapper.getContentType().contains("text/html");
            logger.debug("real.contentType: {}, wrapper.contentType: {}, filter this request? {}", response.getContentType(), wrapper.getContentType(), doFilter);

            if (!doFilter) {
                // do not modify response content
                String content = wrapper.toString();
                logger.warn("content type of {} bytes is not something we can decorate", content.length());
                response.getOutputStream().write(content.getBytes());
                ThreadLocalMemoryAppender.ThreadLocalHolder.clearLoggedEvents();
                return;
            }

            try {
                if (wrapper.toString() != null) {
                    String encoded = ThreadLocalMemoryAppender.ThreadLocalHolder.getBufferAsJson(null, null);

                    StringBuilder responseBody = new StringBuilder(wrapper.toString());
                    logger.trace("responseBody before: {} bytes", responseBody.length());

                    if (encoded != null) {
                        StringBuilder div = new StringBuilder(encoded);
                        logger.debug("log json: {} bytes", div.length());

                        int pos = responseBody.lastIndexOf("</body>");
                        if (pos == -1) {
                            logger.trace("responseBody does not have </body>");
                        }
                        else {
                            responseBody.insert(pos, div);
                            logger.trace("responseBody after: {} bytes", responseBody.length());
                        }
                        // logger.trace("{}", responseBody);
                    }
                    else {
                        logger.debug("no events to write");
                    }

                    response.setContentLength(responseBody.length());
                    response.getOutputStream().write(responseBody.toString().getBytes());

                    logger.debug("response written: {} bytes", responseBody.length());
                }
            }
            finally {
                ThreadLocalMemoryAppender.ThreadLocalHolder.clearLoggedEvents();
            }
        }
    }

    private class CharResponseWrapper extends HttpServletResponseWrapper {
        private CharArrayWriter writer;
        private ByteArrayOutputStream stream;

        private boolean usingWriter = false;

        public CharResponseWrapper(HttpServletResponse response) {
            super(response);
        }

        @Override
        public String toString() {
            if (usingWriter) {
                return writer.toString();
            }
            else return stream.toString();
        }

        @Override
        public PrintWriter getWriter() {
            if (writer == null) {
                writer = new CharArrayWriter(4096);
            }

            usingWriter = true;
            return new PrintWriter(writer);
        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            if (stream == null) {
                stream = new ByteArrayOutputStream(4096);
            }

            return new ServletOutputStream() {
                @Override
                public boolean isReady() {
                    return false;
                }

                @Override
                public void setWriteListener(WriteListener writeListener) {
                }

                @Override
                public void write(int b) throws IOException {
                    stream.write(b);
                }
            };
        }
    }
}
