package com.example.java_springboot_websocket.payload.response;

import com.example.java_springboot_websocket.type.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccessTokenResponseDto {

	private Long id;

	private String email;

	private Role role;

	private String accessToken;

}
