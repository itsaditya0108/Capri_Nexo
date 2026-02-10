package com.company.video_service.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

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
            FilterChain filterChain) throws ServletException, IOException {

        // 0️⃣ Allow OPTIONS (CORS preflight) requests
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            filterChain.doFilter(request, response);
            return;
        }

        // 1️⃣ Read Authorization header
        String authHeader = request.getHeader("Authorization");

        // 1.1️⃣ Check for token in Query Param (for <video> tags if needed)
        if (authHeader == null && request.getParameter("token") != null) {
            authHeader = "Bearer " + request.getParameter("token");
        }

        // Allow streaming without token if needed, or handle via query param as above
        // For now, strict JWT check unless it's a public endpoint check later

        String uri = request.getRequestURI();

        // Specific exclusions if any (e.g. public videos?)
        // if (uri.startsWith("/api/v1/videos/public/")) { ... }

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // Check if it's a request that might be permitted anonymously
            // But since strict mode, we might just continue and let SecurityConfig decide
            // However, to extract user ID, we need token.
            filterChain.doFilter(request, response);
            return;
        }

        // 2️⃣ Extract token
        String token = authHeader.substring(7);

        try {
            // 3️⃣ Validate token (signature + expiry)
            Claims claims = jwtUtil.validateAndGetClaims(token);

            // 4️⃣ Check Revocation (New)
            if (!isSessionValid(token)) {
                System.out.println("DEBUG: Session validation failed for token: " + token.substring(0, 10) + "...");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            // 5️⃣ Extract userId from standard JWT "sub"
            String subject = claims.getSubject(); // sub
            if (subject == null) {
                System.out.println("DEBUG: JWT subject is null");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            Long userId;
            try {
                userId = Long.parseLong(subject);
            } catch (NumberFormatException e) {
                System.out.println("DEBUG: JWT subject is not a valid Long: " + subject);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            // 6️⃣ Attach userId to request for downstream layers
            request.setAttribute("userId", userId);

            // 6.5️⃣ Populate Spring Security Context (CRITICAL for Prod Profile)
            org.springframework.security.authentication.UsernamePasswordAuthenticationToken auth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                    userId, null, java.util.Collections.emptyList());
            org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(auth);

            // 7️⃣ Continue request
            filterChain.doFilter(request, response);

        } catch (Exception ex) {
            // Any JWT validation failure
            System.out.println("DEBUG: JWT Validation Exception: " + ex.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private static final java.util.Map<String, Long> VALID_SESSION_CACHE = new java.util.concurrent.ConcurrentHashMap<>();
    private static final long CACHE_TTL_MS = 30000; // 30 seconds

    private boolean isSessionValid(String token) {
        long now = System.currentTimeMillis();
        if (VALID_SESSION_CACHE.containsKey(token)) {
            if (now - VALID_SESSION_CACHE.get(token) < CACHE_TTL_MS) {
                return true;
            }
        }

        try {
            java.net.URL url = new java.net.URL(authServiceUrl + "/api/auth/validate-session");
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
            return false;
        } catch (Exception e) {
            // If authapp is down, fail-safe or fail-fast?
            // To be secure, we should fail-fast (return false)
            System.err.println("Session validation failed (Auth service unreachable): " + e.getMessage());
            return false;
        }
    }
}
