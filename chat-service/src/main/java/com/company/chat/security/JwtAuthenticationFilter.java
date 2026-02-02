package com.company.chat.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final String authServiceUrl;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, String authServiceUrl) {
        this.jwtUtil = jwtUtil;
        this.authServiceUrl = authServiceUrl;
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

                // Check Revocation (New)
                if (!isSessionValid(token)) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }

                // userId is stored in "sub"
                Long userId = Long.valueOf(claims.getSubject());

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        userId,
                        null,
                        Collections.emptyList());

                SecurityContextHolder.getContext().setAuthentication(auth);

                System.out.println("==== JWT FILTER ====");
                System.out.println("User ID = " + userId);
            }

            filterChain.doFilter(request, response);

        } catch (Exception ex) {
            SecurityContextHolder.clearContext();
            System.err.println("==== JWT AUTHENTICATION FAILED ====");
            ex.printStackTrace(); // Print stack trace for debugging
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("""
                        {
                          "error": "Invalid or expired token"
                        }
                    """);
        }
    }

    private static final java.util.Map<String, Long> VALID_SESSION_CACHE = new java.util.concurrent.ConcurrentHashMap<>();
    private static final long CACHE_TTL_MS = 30000;

    private boolean isSessionValid(String token) {
        long now = System.currentTimeMillis();
        if (VALID_SESSION_CACHE.containsKey(token)) {
            if (now - VALID_SESSION_CACHE.get(token) < CACHE_TTL_MS) {
                return true;
            }
        }

        try {
            // Use configured URL
            String validateUrl = authServiceUrl + "/api/auth/validate-session";
            java.net.URL url = new java.net.URL(validateUrl);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setConnectTimeout(2000);
            conn.setReadTimeout(2000);

            int code = conn.getResponseCode();
            if (code == 200) {
                VALID_SESSION_CACHE.put(token, now);
                return true;
            }
            System.err.println("Session validation failed: HTTP " + code);
            return false;
        } catch (Exception e) {
            System.err.println("Session validation failed (Auth service unreachable): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
