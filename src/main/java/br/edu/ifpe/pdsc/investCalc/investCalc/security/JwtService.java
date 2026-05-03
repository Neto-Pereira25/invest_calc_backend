package br.edu.ifpe.pdsc.investCalc.investCalc.security;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private static final long EXPIRATION_TIME = 1000 * 120;
    private static final String CLAIM_NAME = "name";
    private static final String CLAIM_EMAIL = "email";
    private final String SECRET = "minha-chave-super-secreta-minimo-32-bytes";

    private Key getSignKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    public String generateToken(String name, String email) {

        return Jwts.builder()
                .setSubject(email)
                .claim(CLAIM_NAME, name)
                .claim(CLAIM_EMAIL, email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractEmail(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        String email = claims.get(CLAIM_EMAIL, String.class);
        return email != null ? email : claims.getSubject();
    }

    public boolean isTokenValid(String token, String email) {
        return extractEmail(token).equals(email);
    }

    public String generateRefreshToken() {
        return UUID.randomUUID().toString();
    }
}
