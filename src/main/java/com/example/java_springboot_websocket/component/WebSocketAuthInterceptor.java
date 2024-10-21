package com.example.java_springboot_websocket.component;

import com.example.java_springboot_websocket.constant.AuthConstant;
import com.example.java_springboot_websocket.exception.ModuleException;
import com.example.java_springboot_websocket.model.User;
import com.example.java_springboot_websocket.repository.UserDao;
import com.example.java_springboot_websocket.service.JwtService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.net.URI;
import java.util.Map;
import java.util.Optional;

@Component
@AllArgsConstructor
public class WebSocketAuthInterceptor implements HandshakeInterceptor {

	private final JwtService jwtService;

	private final UserDao userDao;

	@Override
	public boolean beforeHandshake(ServerHttpRequest request, @NonNull ServerHttpResponse response,
			@NonNull WebSocketHandler wsHandler, @NonNull Map<String, Object> attributes) {
		URI uri = request.getURI();
		String query = uri.getQuery();
		String token = getQueryParam(query);

		if (token != null && !jwtService.isTokenExpired(token) && !jwtService.isRefreshToken(token)) {
			String userEmail = jwtService.extractUserEmail(token);

			Optional<User> optionalUser = userDao.findByEmail(userEmail);
			if (optionalUser.isEmpty()) {
				throw new ModuleException("User not found!");
			}

			attributes.put(AuthConstant.USER_ID, optionalUser.get().getUserId());
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public void afterHandshake(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response,
							   @NonNull WebSocketHandler wsHandler, Exception exception) {
		// This method is intentionally left empty as no post-handshake logic is required at the moment.
		// It can be implemented later if actions need to be taken after the WebSocket handshake completes.
	}

	private String getQueryParam(String query) {
		if (query != null && !query.isEmpty()) {
			String[] params = query.split("&");
			for (String p : params) {
				String[] pair = p.split("=");
				if (pair.length == 2 && pair[0].equals(AuthConstant.TOKEN)) {
					return pair[1];
				}
			}
		}
		return null;
	}

}
