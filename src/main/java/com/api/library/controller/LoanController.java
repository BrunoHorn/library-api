package com.api.library.controller;


import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.api.library.dto.BookDto;
import com.api.library.dto.LoanDto;
import com.api.library.dto.LoanFilterDto;
import com.api.library.dto.ReturnedLoanDto;
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
	
	@PatchMapping("{id}")
	public void returnBook(@PathVariable Long id, @RequestBody ReturnedLoanDto dto) {
		
		Loan loan =service.getById(id).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND));
		loan.setReturned(dto.getReturned());
		service.update(loan);
	}
	
	 @GetMapping
	    public Page<LoanDto> find(LoanFilterDto dto, Pageable pageRequest) {
		  Page<Loan> result = service.find(dto, pageRequest);
	        List<LoanDto> loans = result
	                .getContent()
	                .stream()
	                .map(entity -> {

	                    Book book = entity.getBook();
	                    BookDto bookDTO = modelMapper.map(book, BookDto.class);
	                    LoanDto loanDTO = modelMapper.map(entity, LoanDto.class);
	                    loanDTO.setBook(bookDTO);
	                    return loanDTO;

	                }).collect(Collectors.toList());
			
			return new PageImpl<LoanDto>(loans,pageRequest,result.getTotalElements());
	    }
	
}
