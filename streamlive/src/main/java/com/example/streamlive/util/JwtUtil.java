package com.example.streamlive.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.private.key}")
    private String PRIVATE_KEY;

    @Value("${jwt.duration}")
    private int duration;

    public String getToken(Map<String, Object> dataMap) {
        return JWT.create()
                .withClaim("user", dataMap)//set data and wrap
                .withExpiresAt(new Date(System.currentTimeMillis() + duration))//set token duration
                .sign(Algorithm.HMAC256(PRIVATE_KEY));//sign
    }

    public Map<String, Object> getClaims(String token) {
        try {
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(PRIVATE_KEY)).build();//build a decoder
            DecodedJWT decodedJWT = jwtVerifier.verify(token);//parse JWT
            Map<String, Claim> claimsMap = decodedJWT.getClaims();//get claims map
            return claimsMap.get("user").asMap();//get data
        } catch (JWTVerificationException e) {
            return null;
        }
    }

    public boolean isTokenValid(String token) {
        try {
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(PRIVATE_KEY)).build();
            DecodedJWT decodedJWT = jwtVerifier.verify(token);
            Date expirationDate = decodedJWT.getExpiresAt();
            return expirationDate != null && expirationDate.after(new Date());
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    public int getExpiration() {
        return duration;
    }
}
