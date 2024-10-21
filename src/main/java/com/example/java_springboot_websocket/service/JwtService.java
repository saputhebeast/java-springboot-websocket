package com.example.java_springboot_websocket.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {

	String extractUserEmail(String token);

	String extractTokenType(String token);

	String generateAccessToken(UserDetails userDetails);

	String generateRefreshToken(UserDetails userDetails);

	boolean isTokenValid(String token, UserDetails userDetails);

	boolean isRefreshToken(String refreshToken);

	boolean isTokenExpired(String token);

}
