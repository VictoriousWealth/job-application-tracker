package com.nick.job_application_tracker.config.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;


@Component
public class LoggingFilter extends OncePerRequestFilter {

    /***
     * The purpose of this class to log all responses that leave through port
     * 8080 or the port by which tomcat apache is listening to
     * @param request  The request to process
     * @param response The response associated with the request
     * @param filterChain    Provides access to the next filter in the chain for this filter to pass the request and response
     *                     to for further processing
     *
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        System.out.println("======================================================");
        System.out.println("🌐 Incoming Request: " + request.getMethod() + " " + request.getRequestURI() + " with headers: " + getHeaderContent(request));

        filterChain.doFilter(request, response);

        System.out.println("🌐 Outgoing Response with status code " + response.getStatus() + " with headers: {" + getHeaderContent(response)+"}");
        System.out.println("======================================================\n");

    }

    private Map<String, String> getHeaderContent(HttpServletResponse res) {
        Collection<String> headers = res.getHeaderNames();
        Map<String, String> headerMap = new HashMap<>();
        for (String header : headers) {
            headerMap.put(header, res.getHeader(header));
        }
        return headerMap;
    }

    private Map<String, String> getHeaderContent(HttpServletRequest req) {
        Enumeration<String> headers = req.getHeaderNames();
        Map<String, String> headerMap = new HashMap<>();
        Iterator<String> iterator = headers.asIterator();
        while (iterator.hasNext()) {
            String header = iterator.next();
            headerMap.put(header, req.getHeader(header));
        }
        return headerMap;
    }
}
