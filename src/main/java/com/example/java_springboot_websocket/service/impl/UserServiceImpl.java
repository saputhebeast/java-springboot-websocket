package com.example.java_springboot_websocket.service.impl;

import com.example.java_springboot_websocket.exception.ModuleException;
import com.example.java_springboot_websocket.model.User;
import com.example.java_springboot_websocket.repository.UserDao;
import com.example.java_springboot_websocket.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

	@NonNull
	private final UserDao userDao;

	@NonNull
	private final ObjectMapper mapper;

	@Override
	public UserDetailsService userDetailsService() {
		return username -> userDao.findByEmail(username).orElseThrow(() -> new ModuleException("User not found!"));
	}

	@Override
	public User getCurrentUser() {
		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return (User) userDetails;
	}

}
