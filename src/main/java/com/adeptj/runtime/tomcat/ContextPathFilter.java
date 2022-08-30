package com.adeptj.runtime.tomcat;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static javax.servlet.http.HttpServletResponse.SC_FOUND;

public class ContextPathFilter implements Filter {

    private static final String HEADER_LOC = "Location";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            HttpServletRequest req = (HttpServletRequest) request;
            HttpServletResponse resp = (HttpServletResponse) response;
            if (req.getRequestURI().equals("/")) {
                resp.setStatus(SC_FOUND);
                resp.setHeader(HEADER_LOC, "/system/console/bundles");
                return;
            }
        }
        chain.doFilter(request, response);
    }
}
