package com.api.library.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.api.library.model.entity.Book;
import com.api.library.model.entity.BookRepository;
import com.api.library.service.impl.BookServiceImpl;
import com.library.exception.BusinessException;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test") 
//teste unitarios
public class BookServiceTest {

	
	BookService service;
	
	@MockBean
	BookRepository repository;
	
	@BeforeEach
	public void setUp() {
		this.service = new BookServiceImpl(repository)	;	
	}
	
	@Test
	@DisplayName("Deve salvar um livro.")
	public void saveBookTest() {
		//cenario
		Book book = createValidBook();
		Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(false);
		Mockito.when(repository.save(book)).thenReturn(book.builder().id(1L)
										   .author("Arthur")
										   .title("As aventuras")
										   .isbn("123l").build())
		;	
		//execução
		Book savedBook = service.save(book);
	
		//verificação
		assertThat(savedBook.getId()).isNotNull();
		assertThat(savedBook.getAuthor()).isEqualTo("Arthur");
		assertThat(savedBook.getTitle()).isEqualTo("As aventuras");
		assertThat(savedBook.getIsbn()).isEqualTo("123l");
	}

	@Test
	@DisplayName("Deve lançar erro de negócio ao tentar salvar livro com isbn duplicado")
	public void shouldNotSaveABookWithDuplicatedISBN() {
		Book book = createValidBook();
		Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);
		
		//execução
		Throwable exception = Assertions.catchThrowable(() ->service.save(book));
						
		//verificação
		assertThat(exception).isInstanceOf(BusinessException.class)
		 				     .hasMessage("Isbn ja Cadastrado");		
		//verifica que método nunca será executado com esse parâmetro para não salvar
		Mockito.verify(repository, Mockito.never()).save(book);
		
	}
	
	
	
	
	private Book createValidBook() {
		return Book.builder().author("Arthur").title("As asventuras").isbn("123l").build();
	}
	
}
