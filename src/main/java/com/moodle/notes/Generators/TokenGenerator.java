package com.moodle.notes.Generators;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class TokenGenerator {

    public static String generateTokenByUsername(String username){
        return Jwts
                .builder()
                .setSubject(username)
                .signWith(SignatureAlgorithm.HS512,"SECRET_KEY")
                .compact();
    }

    public static String generateUsernameByToken(String token){
        return Jwts
                .parser()
                .setSigningKey("SECRET_KEY")
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
