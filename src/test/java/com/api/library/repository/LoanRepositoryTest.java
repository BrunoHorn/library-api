package com.api.library.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.api.library.model.entity.Book;
import com.api.library.model.entity.Loan;
import com.api.library.model.entity.LoanRepository;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class LoanRepositoryTest {

	@Autowired
	TestEntityManager entityManager;
	
	@Autowired
	LoanRepository repository;
	
	
	@Test
	@DisplayName("Deve verificar se existe empréstimo não devolvido para o livro")
	public void existsByBookAndNotReturned(){
		Loan loan = createAndPersistLoan(LocalDate.now());
		Book book = loan.getBook();
		
		//execução
		Boolean exists = repository.existsByBookAndNotReturned(book);
		
		assertThat(exists).isTrue();
		
	}
	
	@Test
	@DisplayName("Deve buscar empréstimo pelo isbn do livro ou customer")
	public void findByBookIsbnOrCustomer() {
		
		Loan loan = createAndPersistLoan(LocalDate.now());	
		Page<Loan> result = repository.findByBookIsbnOrCustomer("123", "Fulano", PageRequest.of(0,10));
		
		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent()).contains(loan);
		assertThat(result.getPageable().getPageSize()).isEqualTo(10);
		assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
		assertThat(result.getTotalElements()).isEqualTo(1);
	}
	
	
	public Loan createAndPersistLoan(LocalDate loanDate) {
		//cenario
		Book book = Book.builder().isbn("123").title("As aventuras").author("Fulano").build();
		entityManager.persist(book);
						
		Loan loan = Loan.builder().customer("Fulano").book(book).loanDate(loanDate).build();
		entityManager.persist(loan);
		
		return loan;
	}
	
	@Test
	@DisplayName("Deve obter empréstimos cuja data emprestimo for menor ou igual a tres dias atrás e não retornados")
	public void findByLoanDateLessThanAndNotReturned() {
		Loan loan = createAndPersistLoan(LocalDate.now().minusDays(5));
		
		List<Loan> result = repository.findByLoanDateLessThanAndNotReturned(LocalDate.now().minusDays(4));
		assertThat(result).hasSize(1).contains(loan);
	}
	
	@Test
	@DisplayName("Deve retornar vazio quando não houver empréstimos atrasados")
	public void notFindByLoanDateLessThanAndNotReturned() {
		createAndPersistLoan(LocalDate.now());
		
		List<Loan> result = repository.findByLoanDateLessThanAndNotReturned(LocalDate.now().minusDays(4));
		assertThat(result).isEmpty();
	}
	
}
