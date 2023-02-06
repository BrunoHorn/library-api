package com.api.library.resource;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Optional;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.api.library.controller.LoanController;
import com.api.library.dto.LoanDto;
import com.api.library.dto.ReturnedLoanDto;
import com.api.library.model.entity.Book;
import com.api.library.model.entity.Loan;
import com.api.library.service.BookService;
import com.api.library.service.LoanService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.exception.BusinessException;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = LoanController.class)
@AutoConfigureMockMvc
public class LoanControllerTest {

	static final String LOAN_API = "/api/loans";
	
	@Autowired
	MockMvc mvc;
	
	@MockBean
	BookService bookService;
	
	@MockBean
	LoanService loanService;
	
	@Test
	@DisplayName("Deve Realizar um empréstimo")
	public void createLoanTest() throws Exception{
		
		LoanDto dto = LoanDto.builder().isbn("123").email("customer@gmail.com").customer("Fulano").build();
		String json = new ObjectMapper().writeValueAsString(dto);
		
		Book book = Book.builder().id(1L).isbn("123").build();
		BDDMockito.given(bookService.getBookByIsbn("123")).willReturn(Optional.of(book));

		Loan loan = Loan.builder().id(1L).customer("Fulano").book(book).loanDate(LocalDate.now()).build();
		BDDMockito.given(loanService.save(Mockito.any(Loan.class))).willReturn(loan);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json);
		
		mvc.perform(request)
		   .andExpect(status().isCreated())
		   .andExpect(content().string("1"));
	}
	
	@Test
	@DisplayName("Deve retornar erro ao tentar fazer empréstimo de um livro inexistente")
	public void invalidIsbnCreateLoanTest() throws Exception{
		LoanDto dto = LoanDto.builder().isbn("123").customer("Fulano").build();
		String json = new ObjectMapper().writeValueAsString(dto);
		
		BDDMockito.given(bookService.getBookByIsbn("123")).willReturn(Optional.empty());
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json);
		
		mvc.perform(request)
		   .andExpect(status().isBadRequest())
		   .andExpect(jsonPath("errors", Matchers.hasSize(1)))
		   .andExpect(jsonPath("errors[0]").value("Book not found for passed isbn"));
	}
	
	@Test
	@DisplayName("Deve retornar erro ao tentar fazer empréstimo de um livro emprestado")
	public void loanedBookErrorOnCreateLoanTest() throws Exception{
		LoanDto dto = LoanDto.builder().isbn("123").customer("Fulano").build();
		String json = new ObjectMapper().writeValueAsString(dto);
		
		Book book = Book.builder().id(1L).isbn("123").build();
		BDDMockito.given(bookService.getBookByIsbn("123")).willReturn(Optional.of(book));
		
		BDDMockito.given(loanService.save(Mockito.any(Loan.class)))
		          .willThrow(new BusinessException("Book already loaned"));
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json);
		
		mvc.perform(request)
		   .andExpect(status().isBadRequest())
		   .andExpect(jsonPath("errors", Matchers.hasSize(1)))
		   .andExpect(jsonPath("errors[0]").value("Book already loaned"));
	}
	
	@Test
	@DisplayName("Deve retornar um livro")
	public void returnBookTest() throws Exception{
		Loan loan = Loan.builder().id(1L).build();
		
		ReturnedLoanDto dto = ReturnedLoanDto.builder().returned(true).build();
		BDDMockito.given(loanService.getById(Mockito.anyLong()))
			.willReturn(Optional.of(loan));
		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		mvc.perform(patch(LOAN_API.concat("/1"))
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json)).andExpect(status().isOk());
		
		Mockito.verify(loanService, Mockito.times(1)).update(loan);
	}
	
	@Test
	@DisplayName("Deve retornar 404 quando tentar devolver um livro inexistente")
	public void returnInexistentBookTest() throws Exception{
		
		
		ReturnedLoanDto dto = ReturnedLoanDto.builder().returned(true).build();
		BDDMockito.given(loanService.getById(Mockito.anyLong()))
			.willReturn(Optional.empty());
		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		mvc.perform(patch(LOAN_API.concat("/1"))
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json)).andExpect(status().isNotFound());
		
	}
	
	
}
