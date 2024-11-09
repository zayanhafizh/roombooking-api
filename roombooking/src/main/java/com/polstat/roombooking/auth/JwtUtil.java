package com.polstat.roombooking.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    // Metode untuk menghasilkan token JWT
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // Token berlaku selama 10 jam
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes()) // Gunakan kunci dalam bentuk byte
                .compact();
    }

    // Metode untuk mengekstrak klaim dari token
    public Claims extractClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey.getBytes()) // Gunakan kunci dalam bentuk byte
                .parseClaimsJws(token)
                .getBody();
    }

    // Metode untuk mengekstrak username dari token
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    // Metode untuk memeriksa apakah token sudah kedaluwarsa
    public boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    // Metode untuk memvalidasi token dengan username
    public boolean validateToken(String token, String username) {
        return (username.equals(extractUsername(token)) && !isTokenExpired(token));
    }
}
