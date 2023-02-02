package com.api.library.controller;

import java.time.LocalDate;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.api.library.dto.LoanDto;
import com.api.library.model.entity.Book;
import com.api.library.model.entity.Loan;
import com.api.library.service.BookService;
import com.api.library.service.LoanService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

	private final LoanService service;
	
	private final BookService bookService;
	
	private final ModelMapper modelMapper;
	
	@PostMapping
	@ResponseStatus(code = HttpStatus.CREATED)
	public Long create(@RequestBody LoanDto dto) {
		
		Book book = bookService.getBookByIsbn(dto.getIsbn())
				.orElseThrow(()-> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book not found for passed isbn"));
		Loan entity = Loan.builder()
						  .book(book)
						  .customer(dto.getCustomer())
						  .loanDate(LocalDate.now())
						  .build();
		
		entity = service.save(entity);
		return entity.getId();
	}
	
}
