package org.fidoshenyata.http;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;

public class JwtCoder {
    private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public String generateJws(String email, String password){
        return Jwts.builder().setSubject(email+password).signWith(key).compact();
    }

    public boolean isJwsValid(String jws) throws ExpiredJwtException {
        try{
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jws);
        }catch(JwtException e){
            return false;
        }
        return true;
    }
}
