package com.example.java_springboot_websocket.repository;

import com.example.java_springboot_websocket.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserDao extends JpaRepository<User, Long> {

	Optional<User> findByEmail(@NotNull @Email String email);

}
