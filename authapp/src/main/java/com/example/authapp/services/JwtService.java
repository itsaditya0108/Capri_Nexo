package com.example.authapp.services;

import com.example.authapp.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

        @Value("${jwt.secret}")
        private String secret;

        private final long ACCESS_TOKEN_TTL_MIN = 60;

        public String generateAccessToken(User user, Long sessionId) {
                return Jwts.builder()
                                .setSubject(String.valueOf(user.getId()))
                                .claim("email", user.getEmail())
                                .claim("status", user.getStatus().getName())
                                .claim("sid", sessionId)
                                .setIssuedAt(new Date())
                                .setExpiration(
                                                new Date(System.currentTimeMillis()
                                                                + ACCESS_TOKEN_TTL_MIN * 60 * 1000))
                                .signWith(
                                                Keys.hmacShaKeyFor(secret.getBytes()),
                                                SignatureAlgorithm.HS256)
                                .compact();
        }

        public Long validateAndGetUserId(String token) {
                Claims claims = Jwts.parserBuilder()
                                .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes()))
                                .build()
                                .parseClaimsJws(token)
                                .getBody();

                return Long.parseLong(claims.getSubject());
        }

        public Long getSessionId(String token) {
                Claims claims = Jwts.parserBuilder()
                                .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes()))
                                .build()
                                .parseClaimsJws(token)
                                .getBody();

                return claims.get("sid", Long.class);
        }

        // This throws exception automatically if:
        // token expired
        // token invalid
        // signature tampered
}
