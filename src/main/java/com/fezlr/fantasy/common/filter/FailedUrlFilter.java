package com.fezlr.fantasy.common.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class FailedUrlFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String path = req.getRequestURI();

        if (path.startsWith("/api/")) {
            chain.doFilter(request, response);
            return;
        }

        int lastDotIndex = path.lastIndexOf('.');
        int lastSlashIndex = path.lastIndexOf('/');
        if (lastDotIndex > lastSlashIndex) {
            chain.doFilter(request, response);
            return;
        }

        req.getRequestDispatcher("/index.html").forward(request, response);
    }
}
