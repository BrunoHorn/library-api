package com.api.library.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.api.library.model.entity.Book;
import com.api.library.model.entity.Loan;
import com.api.library.model.entity.LoanRepository;
import com.api.library.service.impl.LoanServiceImpl;
import com.library.exception.BusinessException;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LoanServiceTest {
	
	@MockBean
	private LoanRepository loanRepository;
	
	@MockBean
	private LoanService loanService;
	
	@BeforeEach //executado antes de cada teste
	public void setUp() {
		this.loanService = new LoanServiceImpl(loanRepository);
	}
	
	@Test
	@DisplayName("Deve salvar um empréstimo")
	public void saveLoanTest() {
		Book book = Book.builder().id(1L).build();
		String customer = "Fulano";
		
		Loan savingLoan = Loan.builder().book(book)
								  .customer(customer)
								  .loanDate(LocalDate.now())
								  .build();
		
		Loan savedLoan = Loan.builder().book(book)
									   .id(1L)
				  					   .customer(customer)
				  					   .loanDate(LocalDate.now())
				  					   .build();
		when(loanRepository.existsByBookAndNotReturned(book)).thenReturn(false); 
		when(loanRepository.save(savingLoan)).thenReturn(savedLoan);
		
		Loan loan = loanService.save(savingLoan);
		
		assertThat(loan.getId()).isEqualTo(savedLoan.getId());
		assertThat(loan.getCustomer()).isEqualTo(savedLoan.getCustomer());
		assertThat(loan.getLoanDate()).isEqualTo(savedLoan.getLoanDate());
		assertThat(loan.getBook()).isEqualTo(savedLoan.getBook());
	}
	
	@Test
	@DisplayName("Deve lançar erro de negócio ao salvar um empréstimo com livro já emprestado")
	public void loanedBookSaveTest() {
		Book book = Book.builder().id(1L).build();
		String customer = "Fulano";
		
		Loan savingLoan = Loan.builder().book(book)
								  .customer(customer)
								  .loanDate(LocalDate.now())
								  .build();
		
		when(loanRepository.existsByBookAndNotReturned(book)).thenReturn(true); //sempre vai retornar verdadeiro
		
		Throwable exception = catchThrowable(() -> loanService.save(savingLoan));
		assertThat(exception).isInstanceOf(BusinessException.class).hasMessage("Book already loaned");
		
		verify(loanRepository , never()).save(savingLoan);
	}
	

}
