package com.company.video_service.security; // Package for security components

import io.jsonwebtoken.Claims; // JJWT Claims
import io.jsonwebtoken.Jwts; // JJWT Builder
import io.jsonwebtoken.security.Keys; // JJWT Keys utility

import java.nio.charset.StandardCharsets; // Charset constants
import java.security.Key; // Key interface

public class JwtUtil { // Utility class for JWT operations

    private final Key signingKey; // The key used for signing/verifying tokens

    // Constructor to initialize the key from the secret
    public JwtUtil(String secret) {
        this.signingKey = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)); // Generate HmacSHA key from secret bytes
    }

    // Method to validate the token and retrieve its claims (payload)
    public Claims validateAndGetClaims(String token) {
        return Jwts.parserBuilder() // Create a parser builder
                .setSigningKey(signingKey) // Set the signing key to verify signature
                .build() // Build the parser
                .parseClaimsJws(token) // Parse the signed JWT
                .getBody(); // Return the body (claims) if valid
    }
}
