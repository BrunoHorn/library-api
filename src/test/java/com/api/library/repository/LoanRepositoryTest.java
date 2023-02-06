package com.api.library.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
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
	
	
	public Loan createAndPersistLoan(LocalDate loanDate) {
		//cenario
		Book book = Book.builder().isbn("123").title("As aventuras").author("Fulano").build();
		entityManager.persist(book);
						
		Loan loan = Loan.builder().customer("Fulano").book(book).loanDate(loanDate).build();
		entityManager.persist(loan);
		
		return loan;
	}
}
