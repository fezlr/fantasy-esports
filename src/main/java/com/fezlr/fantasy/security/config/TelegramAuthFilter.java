package com.fezlr.fantasy.security.config;

import com.fezlr.fantasy.telegram.security.InitDataValidator;
import com.fezlr.fantasy.user.security.UserDetailsImpl;
import com.fezlr.fantasy.user.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@RequiredArgsConstructor
@Component
public class TelegramAuthFilter extends OncePerRequestFilter {
    private final InitDataValidator initDataValidator;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String initData = request.getHeader("X-Telegram-Init-Data");

        if (initData == null || initData.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            var rawUserData = initDataValidator.validateAndExtractUser(initData);
            var savedUser = userService.createOrUpdate(rawUserData);
            UserDetailsImpl userDetails = new UserDetailsImpl(savedUser);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
