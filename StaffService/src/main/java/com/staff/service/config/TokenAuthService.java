/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.staff.service.config;

import java.util.Date;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author PSB
 */
public class TokenAuthService {
    
    static final long EXPIRATIONTIME = 864_000_000; // 10 days for expiration
    
    static final String TOKEN_PREFIX = "Bearer";
    
    static final String HEADER_STRING = "Authorization";
    
    public static String generateToken(String username, String password, String SECRET) {
        String JWT = Jwts.builder()
                .claim("username", username)
                .claim("password", password)
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME))
                .signWith(SignatureAlgorithm.HS512, SECRET).compact();
        return TOKEN_PREFIX + " " + JWT;
    }

    public static Map<String, String> getAuthentication(String request, String SECRET) {
        String token = request;
        if (token != null) {
            String user = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token.replace(TOKEN_PREFIX, "")).getBody()
                    .getSubject();
            String pwds = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token.replace(TOKEN_PREFIX, "")).getBody()
                    .get("password", String.class);           
            
            Map resp =  new HashMap<String, String>();
       resp.put("user", user); 
       resp.put("pwds", pwds);
       
       return resp;
            
        }
        return null;
    }
}
