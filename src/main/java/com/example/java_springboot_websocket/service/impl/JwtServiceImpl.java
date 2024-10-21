package com.example.java_springboot_websocket.service.impl;

import com.example.java_springboot_websocket.constant.AuthConstant;
import com.example.java_springboot_websocket.service.JwtService;
import com.example.java_springboot_websocket.type.TokenType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

	@Value("${jwt.access-token.signing-key}")
	private String jwtSigningKey;

	@Value("${jwt.access-token.expiration-time}")
	private Long jwtAccessTokenExpirationMs;

	@Value("${jwt.refresh-token.expiration-time}")
	private Long jwtRefreshTokenExpirationMs;

	@Override
	public String extractUserEmail(String token) {
		String email = "";
		try {
			email = extractClaim(token, Claims::getSubject);
		}
		catch (Exception e) {
			log.info(e.getMessage());
		}
		return email;
	}

	@Override
	public String extractTokenType(String token) {
		return extractClaim(token, claims -> claims.get(AuthConstant.TOKEN_TYPE, String.class));
	}

	@Override
	public String generateAccessToken(UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();
		claims.put(AuthConstant.TOKEN_TYPE, TokenType.ACCESS);
		return generateToken(claims, userDetails, jwtAccessTokenExpirationMs);
	}

	@Override
	public String generateRefreshToken(UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();
		claims.put(AuthConstant.TOKEN_TYPE, TokenType.REFRESH);
		return generateToken(claims, userDetails, jwtRefreshTokenExpirationMs);
	}

	@Override
	public boolean isTokenValid(String token, UserDetails userDetails) {
		final String userEmail = extractUserEmail(token);
		return (userEmail.equals(userDetails.getUsername())) && !isTokenExpired(token);
	}

	@Override
	public boolean isRefreshToken(String refreshToken) {
		return extractTokenType(refreshToken).equals(TokenType.REFRESH.name());
	}

	private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
		final Claims claims = extractAllClaims(token);
		return claimsResolvers.apply(claims);
	}

	private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails, Long expirationTime) {
		List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

		Map<String, Object> claims = new HashMap<>();
		claims.put(AuthConstant.ROLES, roles);
		if (extraClaims != null) {
			claims.putAll(extraClaims);
		}

		return Jwts.builder()
			.setClaims(claims)
			.setSubject(userDetails.getUsername())
			.setIssuedAt(new Date(System.currentTimeMillis()))
			.setExpiration(new Date(System.currentTimeMillis() + expirationTime))
			.signWith(getSigningKey(), SignatureAlgorithm.HS256)
			.compact();
	}

	@Override
	public boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	private Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
	}

	private Key getSigningKey() {
		byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey);
		return Keys.hmacShaKeyFor(keyBytes);
	}

}
