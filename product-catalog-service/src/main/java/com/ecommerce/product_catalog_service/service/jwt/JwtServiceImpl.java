package com.ecommerce.product_catalog_service.service.jwt;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.secret}")
      private String jwtSecret;

      public JwtServiceImpl(@Value("${jwt.secret}") String jwtSecret) {
          this.jwtSecret = jwtSecret;
      }

    private Key getKey() {
        byte[] keyBytes = (byte[])Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String getEmailFromToken(String token) {
        return getClaim(token, Claims::getSubject);
    }

    public String getRoleFromToken(String token) {
        return getClaim(token, claims -> claims.get("role", String.class));
    }

    public boolean isTokenValid(String token, String email) {
        String tokenEmail = getEmailFromToken(token);
        return tokenEmail.equals(email) && !isTokenExpired(token);
    }

    private Claims getAllClaims(String token) {
        try {
           return Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException var3) {
           throw new JwtException("Token Error: " + var3.getMessage());
        }
    }

    public <T> T getClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = getAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Date getExpiration(String token) {
        return getClaim(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token) {
        return getExpiration(token).before(new Date());
    }

}


