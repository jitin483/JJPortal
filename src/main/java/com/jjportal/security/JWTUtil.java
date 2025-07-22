package com.jjportal.security;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

@Component
public class JWTUtil {

    @Value("${jwt_secret}")
    private String secret;

    private final String ISSUER = "Event Scheduler";
    private final String SUBJECT = "User Details";

    // Generate token with email claim
    public String generateToken(String email) throws JWTCreationException {
        return JWT.create()
                .withSubject(SUBJECT)
                .withClaim("email", email)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000)) // 24 hours expiry
                .withIssuer(ISSUER)
                .sign(Algorithm.HMAC256(secret));
    }

    // Verify token and extract email
    public String validateTokenAndRetrieveEmail(String token) throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret))
                .withSubject(SUBJECT)
                .withIssuer(ISSUER)
                .build();

        DecodedJWT jwt = verifier.verify(token);
        return jwt.getClaim("email").asString();
    }
}
