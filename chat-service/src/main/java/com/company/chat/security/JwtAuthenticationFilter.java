package com.company.chat.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws IOException {

        try {
            String token = null;

            // Check Authorization header first
            String header = request.getHeader("Authorization");
            if (header != null && header.startsWith("Bearer ")) {
                token = header.substring(7);
            }
            // For SSE endpoints, check query parameter
            else if (request.getParameter("token") != null) {
                token = request.getParameter("token");
            }

            if (token != null) {
                Claims claims = jwtUtil.parseToken(token);

                // userId is stored in "sub"
                Long userId = Long.valueOf(claims.getSubject());

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        userId,
                        null,
                        Collections.emptyList());

                SecurityContextHolder.getContext().setAuthentication(auth);

                System.out.println("==== JWT FILTER ====");
                System.out.println("URI = " + request.getRequestURI());
                System.out.println("User ID = " + userId);
            }

            filterChain.doFilter(request, response);

        } catch (Exception ex) {
            // Clear context to avoid leakage
            SecurityContextHolder.clearContext();

            // Log the actual error for debugging
            System.err.println("==== JWT AUTHENTICATION FAILED ====");
            System.err.println("Error Type: " + ex.getClass().getName());
            System.err.println("Error Message: " + ex.getMessage());
            ex.printStackTrace();

            // Invalid JWT → 401, not 500
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("""
                        {
                          "error": "Invalid or expired token"
                        }
                    """);
        }
    }
}




