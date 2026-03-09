package com.safeer.jiraclone.service;

import com.safeer.jiraclone.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTService {

    @Value("${jwt.secretkey}")
    private String secretKey;

//    public JWTService() throws NoSuchAlgorithmException {
//        KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
//        SecretKey sk = keyGen.generateKey();
//        secretKey = Base64.getEncoder().encodeToString(sk.getEncoded());
//    }

//    private SecretKey getKey(){
//        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
//        return Keys.hmacShaKeyFor(keyBytes);
//    }

    private SecretKey getSecretKey(){
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(User user){

        return Jwts.builder()
                .claim("userId", user.getId())
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 60 * 60 * 1000))
                .signWith(getSecretKey())
                .compact();

    }


    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    private Claims extractClaims(String token){
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String userName = extractUsername(token);
        final boolean isTokenExpired = extractClaims(token).getExpiration().before(new Date());
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired);
    }

}
