package com.api.library.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.api.library.dto.BookDto;
import com.api.library.model.entity.Book;
import com.api.library.service.BookService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/books")
public class BookController {

	private ModelMapper modelMapper;
	
	@Autowired
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
	
	
	@GetMapping("{id}")
	public BookDto get(@PathVariable Long id){
		return service.getById(id).map(book -> modelMapper.map(book, BookDto.class))
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
	}	
	@DeleteMapping("{id}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id){
		Book book = service.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		service.delete(book);
		
	}
	
	@PutMapping("{id}")
	@ApiOperation("Update a book")
	public BookDto update(@PathVariable Long id, @RequestBody @Valid BookDto dto){
		return service.getById(id).map(book -> {
				book.setAuthor(dto.getAuthor());
				book.setTitle(dto.getTitle());
				book = service.update(book);

		return modelMapper.map(book, BookDto.class);		
						
		}).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
	
	}
	@GetMapping
	@ApiOperation("Find books")
	public Page<BookDto> find (BookDto dto, Pageable pageRequest){
		Book filter  = modelMapper.map(dto, Book.class);
		Page<Book> result = service.find(filter, pageRequest);
		List<BookDto> list = result.getContent()
								   .stream()
								   .map(entity -> modelMapper.map(entity, BookDto.class))
								   .collect(Collectors.toList());
		
		return new PageImpl<BookDto>(list, pageRequest, result.getTotalElements());
	}
	
	
	

	
}
