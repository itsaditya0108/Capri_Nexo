package com.company.video_service.security; // Package for security components

import io.jsonwebtoken.Claims; // JJWT Claims
import jakarta.servlet.FilterChain; // Servlet FilterChain
import jakarta.servlet.ServletException; // ServletException
import jakarta.servlet.http.HttpServletRequest; // HttpServletRequest
import jakarta.servlet.http.HttpServletResponse; // HttpServletResponse
import org.springframework.web.filter.OncePerRequestFilter; // Base class for filters

import java.io.IOException; // IOException

public class JwtAuthenticationFilter extends OncePerRequestFilter { // Custom JWT filter ensuring execution once per
                                                                    // request

    private final JwtUtil jwtUtil; // Utility for JWT operations
    private final String authServiceUrl; // URL of the Auth Service for validation

    // Constructor injection
    public JwtAuthenticationFilter(JwtUtil jwtUtil, String authServiceUrl) {
        this.jwtUtil = jwtUtil;
        this.authServiceUrl = authServiceUrl;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException { // Core filter logic

        // 0️⃣ Allow OPTIONS (CORS preflight) requests
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK); // Return 200 OK for preflight
            filterChain.doFilter(request, response); // Continue chain
            return;
        }

        // 1️⃣ Read Authorization header
        String authHeader = request.getHeader("Authorization");

        // 1.1️⃣ Check for token in Query Param (fallback for specific clients or
        // <video> tags)
        if (authHeader == null && request.getParameter("token") != null) {
            authHeader = "Bearer " + request.getParameter("token");
        }

        // Allow streams or logic to proceed without token if needed
        // but generally we enforce JWT. If header is missing, we pass it down
        // and let SecurityConfig deny access if endpoint requires it.

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // No valid Bearer token found.
            // Continue chain (anonymous request) - SecurityConfig will handle 403 if auth
            // is required.
            filterChain.doFilter(request, response);
            return;
        }

        // 2️⃣ Extract token (remove "Bearer " prefix)
        String token = authHeader.substring(7);

        try {
            // 3️⃣ Validate token (check signature and expiration locally)
            Claims claims = jwtUtil.validateAndGetClaims(token);

            // 4️⃣ Check Revocation (Verify with Auth Service if session is still valid)
            if (!isSessionValid(token)) {
                System.out.println("DEBUG: Session validation failed for token: " + token.substring(0, 10) + "...");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
                return;
            }

            // 5️⃣ Extract userId from standard JWT "sub" (subject) claim
            String subject = claims.getSubject(); // sub
            if (subject == null) {
                System.out.println("DEBUG: JWT subject is null");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            Long userId;
            try {
                userId = Long.parseLong(subject); // Parse subject as Long ID
            } catch (NumberFormatException e) {
                System.out.println("DEBUG: JWT subject is not a valid Long: " + subject);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            // 6️⃣ Attach userId to request attributes for usage in Controllers/Services
            request.setAttribute("userId", userId);

            // 6.5️⃣ Populate Spring Security Context (CRITICAL for Prod Profile and
            // @PreAuthorize)
            // Create an Authentication object with the userId and empty authorities
            org.springframework.security.authentication.UsernamePasswordAuthenticationToken auth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                    userId, null, java.util.Collections.emptyList());
            // Set the authentication in the SecurityContext
            org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(auth);

            // 7️⃣ Continue request processing
            filterChain.doFilter(request, response);

        } catch (Exception ex) {
            // Catch any JWT validation failures (Expired, Malformed, Signature, etc.)
            System.out.println("DEBUG: JWT Validation Exception: " + ex.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
        }
    }

    // Simple in-memory cache to avoid hitting Auth Service on every request
    private static final java.util.Map<String, Long> VALID_SESSION_CACHE = new java.util.concurrent.ConcurrentHashMap<>();
    private static final long CACHE_TTL_MS = 30000; // 30 seconds TTL

    // Helper method to validate session against Auth Service
    private boolean isSessionValid(String token) {
        long now = System.currentTimeMillis();
        // Check cache first
        if (VALID_SESSION_CACHE.containsKey(token)) {
            if (now - VALID_SESSION_CACHE.get(token) < CACHE_TTL_MS) {
                return true; // Valid in cache
            }
        }

        try {
            // Call Auth Service API
            java.net.URL url = new java.net.URL(authServiceUrl + "/api/auth/validate-session");
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + token); // Pass token
            conn.setConnectTimeout(2000); // 2s timeout
            conn.setReadTimeout(2000); // 2s timeout

            int code = conn.getResponseCode();
            if (code == 200) { // 200 OK means session is valid
                VALID_SESSION_CACHE.put(token, now); // Update cache
                return true;
            }
            return false; // Any other code means invalid
        } catch (Exception e) {
            // If authapp is down or unreachable
            // Fail-safe approach: return false to be secure (deny access)
            System.err.println("Session validation failed (Auth service unreachable): " + e.getMessage());
            return false;
        }
    }
}
