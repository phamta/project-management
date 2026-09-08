package com.tanvan.backend.auth.security.utils;

// backend/src/main/java/com/collabflow/auth/security/utils/JwtUtil.java

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private Long expiration;
    
    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;
    
    private Key getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    public String extractUserId(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("authorities", userDetails.getAuthorities());
        
        // Lấy userId từ UserDetails (nếu User implements UserDetails)
        String userId = null;
        if (userDetails instanceof com.tanvan.backend.auth.entity.User) {
            userId = ((com.tanvan.backend.auth.entity.User) userDetails).getId();
        } else {
            // Fallback: nếu không lấy được userId, dùng username
            userId = userDetails.getUsername();
        }
        
        return createToken(claims, userId);
    }
    
    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        
        String userId = null;
        if (userDetails instanceof com.tanvan.backend.auth.entity.User) {
            userId = ((com.tanvan.backend.auth.entity.User) userDetails).getId();
        } else {
            userId = userDetails.getUsername();
        }
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String userId = extractUserId(token);
        
        // Lấy userId từ UserDetails
        String expectedUserId = null;
        if (userDetails instanceof com.tanvan.backend.auth.entity.User) {
            expectedUserId = ((com.tanvan.backend.auth.entity.User) userDetails).getId();
        } else {
            expectedUserId = userDetails.getUsername();
        }
        
        return (userId.equals(expectedUserId) && !isTokenExpired(token));
    }

    public Boolean validateRefreshToken(String token) {
        return !isTokenExpired(token);
    }
}
