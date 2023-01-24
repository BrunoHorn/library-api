package com.api.library.exception;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.validation.BindingResult;

import com.library.exception.BusinessException;

public class ApiErros {
	private List<String> errors;
	
	public ApiErros(BindingResult bindingResult) {
		this.errors = new ArrayList<>();
		bindingResult.getAllErrors().forEach(error -> this.errors.add(error.getDefaultMessage()));
	}

	public List<String> getErrors() {
		return errors;
	}
	public ApiErros(BusinessException ex) {
		this.errors = Arrays.asList(ex.getMessage());
	}
	
}
