package com.example.java_springboot_websocket.service.impl;

import com.example.java_springboot_websocket.component.WebSocketHandler;
import com.example.java_springboot_websocket.payload.response.ResponseEntityDto;
import com.example.java_springboot_websocket.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

	private final WebSocketHandler webSocketHandler;

	@Override
	public ResponseEntityDto sendNotification(String userId, String message) {

		webSocketHandler.sendNotificationToUser(userId, message);
		log.info("Notification sent to user: {}", userId);

		return new ResponseEntityDto(false, "Notification sent successfully!");
	}

}
