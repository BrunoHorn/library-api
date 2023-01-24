package com.api.library.controller;

import java.util.List;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.api.library.dto.BookDto;
import com.api.library.exception.ApiErros;
import com.api.library.model.entity.Book;
import com.api.library.service.BookService;
import com.library.exception.BusinessException;

@RestController
@RequestMapping("/api/books")
public class BookController {

	private ModelMapper modelMapper;
	
	private BookService service;
	
	public BookController(ModelMapper modelMapper) {
		this.service = service;
		this.modelMapper = modelMapper;
	}
	
	@PostMapping
	@ResponseStatus(code = HttpStatus.CREATED)
	public BookDto create(@RequestBody  @Valid	BookDto dto) {
		Book entity = modelMapper.map(dto, Book.class);	
		entity = service.save(entity);
		return modelMapper.map(entity, BookDto.class); 
	}
	
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
	
}
