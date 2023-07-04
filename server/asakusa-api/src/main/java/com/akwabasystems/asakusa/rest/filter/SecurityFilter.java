
package com.akwabasystems.asakusa.rest.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.stereotype.Component;


@Component
@WebFilter("/api/*")
public class SecurityFilter implements Filter {

    /**
     * Processes the incoming request, adds the required HTTP security headers 
     * to the response, and proceeds with the next filter in the filter chain
     * 
     * @param request           the incoming request
     * @param response          the response object
     * @param chain             the filter chain handling the request
     * @throws ServletException if the request cannot be processed
     * @throws IOException if the response cannot be output to the stream
     */
    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain)
                        throws ServletException, IOException {
    
        HttpServletRequest servletRequest = (HttpServletRequest) request;
        HttpServletResponse servletResponse = (HttpServletResponse) response;
        
        /** 
         * Add the default Web security headers on the response sending it to the client 
         */
        servletResponse.addHeader("X-Content-Type-Options", "nosniff");
        servletResponse.addHeader("X-XSS-Protection", "1; mode=block");
        servletResponse.addHeader("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate");
        servletResponse.addHeader("X-Frame-Options", "DENY");
        servletResponse.addHeader("Strict-Transport-Security", "max-age=31536000 ; includeSubDomains");
        
        chain.doFilter(servletRequest, servletResponse);
        
    }
    
}
