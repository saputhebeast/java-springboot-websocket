package com.example.java_springboot_websocket.exception;

import com.example.java_springboot_websocket.payload.response.ErrorResponse;
import com.example.java_springboot_websocket.payload.response.ResponseEntityDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(BindException.class)
	public ResponseEntity<ResponseEntityDto> handleValidationErrors(BindException e) {

		String err = "Validation failure.";
		HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
		ErrorResponse errorResponse = new ErrorResponse(status, err);
		for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
			errorResponse.addValidationError(fieldError.getField(), fieldError.getDefaultMessage());
		}
		return new ResponseEntity<>(new ResponseEntityDto(true, errorResponse), status);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ResponseEntityDto> handleExceptions(Exception e) {
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
		return new ResponseEntity<>(new ResponseEntityDto(true, new ErrorResponse(status, e.getMessage())), status);
	}

	@ExceptionHandler(ModuleException.class)
	public ResponseEntity<ResponseEntityDto> handleModuleExceptions(Exception e) {
		HttpStatus status = HttpStatus.BAD_REQUEST;
		return new ResponseEntity<>(new ResponseEntityDto(true, new ErrorResponse(status, e.getMessage())), status);
	}

}
