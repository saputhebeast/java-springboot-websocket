package com.example.java_springboot_websocket.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignInRequestDto {

	@Email
	@NotNull
	private String email;

	@NotNull
	private String password;

}
