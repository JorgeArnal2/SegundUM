package com.arso.auth;

import java.time.Instant;
import java.util.Date;
import java.util.Map;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtUtils {

	private static final String SECRETO = System.getenv("JWT_SECRET") != null 
		? System.getenv("JWT_SECRET") 
		: "secreto";
	
	private static final long TIEMPO = System.getenv("JWT_EXPIRATION") != null 
		? Long.parseLong(System.getenv("JWT_EXPIRATION")) 
		: 3600; // 1 hora
	
	public static String generateToken(Map<String, Object> claims) {
		
		Date caducidad = Date.from(Instant.now().plusSeconds(TIEMPO));

		String token = Jwts.builder()
			.setClaims(claims)
			.signWith(SignatureAlgorithm.HS256, SECRETO)
			.setExpiration(caducidad)
			.compact();
		
		return token;		
	}
	
	public static Claims validateToken(String token) {
		
		Claims claims = Jwts.parser()
                .setSigningKey(SECRETO)
                .parseClaimsJws(token)
                .getBody();
		
		return claims;
	}
	
}

