package com.example.java_springboot_websocket.service;

import com.example.java_springboot_websocket.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService {

	UserDetailsService userDetailsService();

	User getCurrentUser();

}
