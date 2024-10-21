
# Java Spring Boot WebSocket Project

## Overview

This project implements a WebSocket-based notification system using Spring Boot. The system includes JWT-based authentication and role-based access control, with the ability to send notifications to specific users in real time.

## Technologies

- **Java 21**
- **Spring Boot**
- **WebSocket**
- **Spring Security**
- **JWT (JSON Web Token)**
- **Hibernate / JPA**
- **MySQL** (Database)
- **Lombok**
- **BCrypt** (for password encryption)

## Project Structure

```
src
│── main
│   ├── java
│   │   └── com.example.java_springboot_websocket
│   │       ├── component
│   │       ├── config
│   │       ├── constant
│   │       ├── controller
│   │       ├── exception
│   │       ├── model
│   │       ├── payload
│   │       ├── repository
│   │       ├── service
│   │       └── type
│   └── resources
│       ├── application.yml
└── test
```

### Packages

- **component**: Contains filters and interceptors (e.g., `JwtAuthFilter`, `WebSocketAuthInterceptor`).
- **config**: Configuration files for security and WebSocket setup (`SecurityConfig`, `WebSocketConfig`).
- **constant**: Constants used across the application.
- **controller**: REST controllers (`AuthController`, `NotificationController`).
- **exception**: Custom exceptions and global exception handling.
- **model**: Entity classes (e.g., `User`).
- **payload**: Request and response DTOs.
- **repository**: Data access layer using JPA.
- **service**: Business logic, including JWT handling and notification service.
- **type**: Enum for `Role` and `TokenType`.

## Security

### Authentication

Authentication is handled via JWT tokens. The `JwtAuthFilter` is used to intercept incoming requests, extract the JWT from the `Authorization` header, and authenticate the user based on the token.

- **JwtAuthFilter**:
  - Extracts the JWT token from the `Authorization` header.
  - Validates the token and extracts the user’s email.
  - Sets the security context with `UsernamePasswordAuthenticationToken` if valid.

```java
final String authHeader = request.getHeader(AuthConstant.AUTHORIZATION);
if (StringUtils.isEmpty(authHeader) || !StringUtils.startsWith(authHeader, AuthConstant.BEARER)) {
    filterChain.doFilter(request, response);
    return;
}
```

### Authorization

Authorization is role-based. Users are assigned roles (e.g., `USER`). Role-specific access is controlled in the `SecurityConfig` class.

```java
http.authorizeHttpRequests(auth -> 
    auth.requestMatchers("/v1/auth/**", "/ws/**").permitAll()
    .anyRequest().authenticated());
```

## WebSocket

WebSocket connections are handled by `WebSocketHandler`. The connection is authenticated using a JWT token passed in the URL query string.

- **WebSocketHandler**: Manages WebSocket sessions, handles message exchange, and supports sending notifications to specific users.
- **WebSocketAuthInterceptor**: Validates the JWT token during the WebSocket handshake.

### WebSocket Auth Interceptor

```java
String token = getQueryParam(query);
if (token != null && !jwtService.isTokenExpired(token)) {
    String userEmail = jwtService.extractUserEmail(token);
    Optional<User> optionalUser = userDao.findByEmail(userEmail);
    if (optionalUser.isEmpty()) {
        throw new ModuleException("User not found!");
    }
    attributes.put(AuthConstant.USER_ID, optionalUser.get().getUserId());
    return true;
} else {
    return false;
}
```

## JWT Service

The `JwtService` is responsible for generating and validating JWT tokens. Tokens are generated with claims that include user roles.

- **Access Tokens**: Short-lived tokens used for regular API access.
- **Refresh Tokens**: Longer-lived tokens used to obtain new access tokens.

### Token Generation

```java
Map<String, Object> claims = new HashMap<>();
claims.put(AuthConstant.TOKEN_TYPE, TokenType.ACCESS);
return generateToken(claims, userDetails, jwtAccessTokenExpirationMs);
```

## Exception Handling

Global exception handling is implemented using `@ControllerAdvice` in the `GlobalExceptionHandler` class. Custom exceptions such as `ModuleException` are thrown for business logic errors.

### Example Exception Handler

```java
@ExceptionHandler(ModuleException.class)
public ResponseEntity<ResponseEntityDto> handleModuleExceptions(Exception e) {
    HttpStatus status = HttpStatus.BAD_REQUEST;
    return new ResponseEntity<>(new ResponseEntityDto(true, new ErrorResponse(status, e.getMessage())), status);
}
```

## REST API Endpoints

### Authentication

- **POST /auth/sign-in**
  - Authenticates a user and returns JWT tokens (access and refresh tokens).

- **POST /auth/sign-up**
  - Registers a new user.

- **POST /auth/refresh-token**
  - Refreshes an expired access token using a valid refresh token.

### Notification

- **POST /notification/send**
  - Sends a WebSocket notification to a user.

  **Parameters**:
  - `userId`: The ID of the user to notify.
  - `message`: The notification message.

## WebSocket Endpoint

### WebSocket Connection

- **Endpoint**: `/ws/notification`
- **Authentication**: JWT token passed as a query parameter.

## CORS Configuration

CORS is configured globally to allow requests from any origin:

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(List.of("*"));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type"));
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
```

## Dependencies

- **Spring Boot Starter Web**
- **Spring Boot Starter Security**
- **Spring Boot Starter WebSocket**
- **Spring Boot Starter Data JPA**
- **Lombok**
- **BCrypt**
- **JWT (io.jsonwebtoken)**
