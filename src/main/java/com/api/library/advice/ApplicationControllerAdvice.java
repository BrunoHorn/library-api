package com.api.library.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import com.api.library.exception.ApiErros;
import com.library.exception.BusinessException;

@RestControllerAdvice
public class ApplicationControllerAdvice {
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	public ApiErros handleValidationExceptions(MethodArgumentNotValidException ex) {
		BindingResult bindingResult =	ex.getBindingResult();

		return new ApiErros (bindingResult);
		
	}
	@ExceptionHandler(BusinessException.class)
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	public ApiErros handleBusinessExceptions(BusinessException ex) {
		return new ApiErros (ex);
	}
	
	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity handleResponseException(ResponseStatusException ex) {
		return new ResponseEntity(new ApiErros(ex), ex.getStatus());
	}
	

}
