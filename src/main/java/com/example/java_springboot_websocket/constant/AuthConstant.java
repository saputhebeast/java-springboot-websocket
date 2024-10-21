package com.example.java_springboot_websocket.constant;

public class AuthConstant {

	public static final String AUTH_ROLE = "ROLE_";

	public static final String TOKEN_TYPE = "token_type";

	public static final String ROLES = "roles";

	public static final String AUTHORIZATION = "Authorization";

	public static final String BEARER = "Bearer ";

	public static final String TOKEN = "token";

	public static final String USER_ID = "userId";

	private AuthConstant() {
		throw new IllegalStateException("Illegal instantiate");
	}

}
