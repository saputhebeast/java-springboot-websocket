package com.example.java_springboot_websocket.service.impl;

import com.example.java_springboot_websocket.exception.ModuleException;
import com.example.java_springboot_websocket.model.User;
import com.example.java_springboot_websocket.payload.request.RefreshTokenRequestDto;
import com.example.java_springboot_websocket.payload.request.SignInRequestDto;
import com.example.java_springboot_websocket.payload.request.SignUpRequestDto;
import com.example.java_springboot_websocket.payload.response.AccessTokenResponseDto;
import com.example.java_springboot_websocket.payload.response.ResponseEntityDto;
import com.example.java_springboot_websocket.payload.response.SignInResponseDto;
import com.example.java_springboot_websocket.repository.UserDao;
import com.example.java_springboot_websocket.service.AuthService;
import com.example.java_springboot_websocket.service.JwtService;
import com.example.java_springboot_websocket.service.UserService;
import com.example.java_springboot_websocket.type.Role;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	@NonNull
	private final UserDao userDao;

	@NonNull
	private final JwtService jwtService;

	@NonNull
	private final AuthenticationManager authenticationManager;

	@NonNull
	private final PasswordEncoder passwordEncoder;

	@NonNull
	private final UserService userService;

	public static void isValidEmail(String email) throws ValidationException {
		if (!Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$").matcher(email).matches()) {
			throw new ValidationException("Please enter a valid email address");
		}
	}

	public static void isValidPassword(String password) throws ValidationException {
		if (!StringUtils.hasText(password)) {
			throw new ValidationException("Please enter the password");
		}
		if (!Pattern.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", password)) {
			throw new ValidationException(
					"Password requirement not met. Please enter a password that matches all the requirements");
		}
	}

	@Override
	public ResponseEntityDto signIn(SignInRequestDto signInRequestDto) {
		log.info("signIn: execution started");

		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(signInRequestDto.getEmail(), signInRequestDto.getPassword()));

		Optional<User> optionalUser = userDao.findByEmail(signInRequestDto.getEmail());
		if (optionalUser.isEmpty()) {
			throw new ModuleException("User not found!");
		}
		User user = optionalUser.get();

		if (Boolean.FALSE.equals(user.getIsActive())) {
			throw new ModuleException("User account is deactivated!");
		}

		SignInResponseDto signInResponseDto = getSignInResponseDto(user);

		log.info("signIn: execution ended");
		return new ResponseEntityDto(false, signInResponseDto);
	}

	@Transactional
	@Override
	public ResponseEntityDto signUp(SignUpRequestDto signUpRequestDto) {
		log.info("signUp: execution started");

		Optional<User> optionalUser = userDao.findByEmail(signUpRequestDto.getEmail());
		if (optionalUser.isPresent()) {
			throw new ModuleException("This email already exists in the system!");
		}

		isValidEmail(signUpRequestDto.getEmail());
		isValidPassword(signUpRequestDto.getPassword());

		User user = new User();
		user.setEmail(signUpRequestDto.getEmail());
		user.setIsActive(true);
		user.setRole(Role.USER);
		user.setPassword(passwordEncoder.encode(signUpRequestDto.getPassword()));

		userDao.save(user);

		SignInResponseDto signInResponseDto = getSignInResponseDto(user);

		log.info("signUp: execution ended");
		return new ResponseEntityDto(false, signInResponseDto);
	}

	@Override
	public ResponseEntityDto refreshAccessToken(RefreshTokenRequestDto refreshTokenRequestDto) {
		log.info("refreshAccessToken: execution started");

		if (!jwtService.isRefreshToken(refreshTokenRequestDto.getRefreshToken())
				|| jwtService.isTokenExpired(refreshTokenRequestDto.getRefreshToken())) {
			throw new ModuleException("Invalid refresh token!");
		}

		String userEmail = jwtService.extractUserEmail(refreshTokenRequestDto.getRefreshToken());
		UserDetails userDetails = userService.userDetailsService().loadUserByUsername(userEmail);

		if (!jwtService.isTokenValid(refreshTokenRequestDto.getRefreshToken(), userDetails)) {
			throw new ModuleException("Invalid refresh token!");
		}

		Optional<User> optionalUser = userDao.findByEmail(userEmail);
		if (optionalUser.isEmpty()) {
			throw new ModuleException("User not found!");
		}
		User user = optionalUser.get();

		String accessToken = jwtService.generateAccessToken(userDetails);

		AccessTokenResponseDto accessTokenResponseDto = new AccessTokenResponseDto();
		accessTokenResponseDto.setAccessToken(accessToken);
		accessTokenResponseDto.setId(user.getUserId());
		accessTokenResponseDto.setEmail(user.getEmail());

		log.info("refreshAccessToken: execution ended");
		return new ResponseEntityDto(false, accessTokenResponseDto);
	}

	private SignInResponseDto getSignInResponseDto(User user) {
		String accessToken = jwtService.generateAccessToken(user);
		String refreshToken = jwtService.generateRefreshToken(user);

		SignInResponseDto signInResponseDto = new SignInResponseDto();
		signInResponseDto.setId(user.getUserId());
		signInResponseDto.setEmail(user.getEmail());
		signInResponseDto.setRole(user.getRole());
		signInResponseDto.setAccessToken(accessToken);
		signInResponseDto.setRefreshToken(refreshToken);

		return signInResponseDto;
	}

}
