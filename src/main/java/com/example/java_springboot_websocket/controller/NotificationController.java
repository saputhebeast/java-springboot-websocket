package com.example.java_springboot_websocket.controller;

import com.example.java_springboot_websocket.payload.response.ResponseEntityDto;
import com.example.java_springboot_websocket.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notification")
public class NotificationController {

	private final NotificationService notificationService;

	@PostMapping("/send")
	public ResponseEntity<ResponseEntityDto> sendNotification(@RequestParam String userId,
			@RequestParam String message) {
		ResponseEntityDto response = notificationService.sendNotification(userId, message);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
