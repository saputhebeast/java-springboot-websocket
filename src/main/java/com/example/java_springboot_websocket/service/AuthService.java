package com.example.java_springboot_websocket.service;

import com.example.java_springboot_websocket.payload.request.RefreshTokenRequestDto;
import com.example.java_springboot_websocket.payload.request.SignInRequestDto;
import com.example.java_springboot_websocket.payload.request.SignUpRequestDto;
import com.example.java_springboot_websocket.payload.response.ResponseEntityDto;

public interface AuthService {

	ResponseEntityDto signIn(SignInRequestDto signInRequestDto);

	ResponseEntityDto signUp(SignUpRequestDto signUpRequestDto);

	ResponseEntityDto refreshAccessToken(RefreshTokenRequestDto refreshTokenRequestDto);

}
