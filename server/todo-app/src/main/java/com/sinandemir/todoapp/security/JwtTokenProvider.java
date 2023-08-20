package com.sinandemir.todoapp.security;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {

    @Value("${app.jwt-secret}")
    private String jwtSecret;
    @Value("${app.jwt-expiration-milliseconds}")
    private Long jwtExpirationDate;
    @Value("${app.refresh-token-expiration-milliseconds}")
    private Long refreshTokenExpirationDate;

    public String generateToken(String username) {

        Date expireDate = new Date(System.currentTimeMillis() + jwtExpirationDate);

        String jwtToken = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();

        return jwtToken;
    }

    public String generateRefreshToken(Long userId) {
        Date expireDate = new Date(System.currentTimeMillis() + refreshTokenExpirationDate);

        String refreshToken = Jwts.builder()
                .setSubject(Long.toString(userId))
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();

        return refreshToken;
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String getUsername(String jwtToken) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(jwtToken)
                .getBody();

        String username = claims.getSubject();
        return username;
    }

    public Long getUserId(String jwtToken) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(jwtToken)
                .getBody();

        Long userId = Long.parseLong(claims.getSubject());
        return userId;
    }

    public boolean validateToken(String jwtToken) {
        Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parse(jwtToken);
        return true;
    }
}
