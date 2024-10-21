package com.example.java_springboot_websocket.service;

import com.example.java_springboot_websocket.payload.response.ResponseEntityDto;

public interface NotificationService {

	ResponseEntityDto sendNotification(String userId, String message);

}
