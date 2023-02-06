package com.api.library.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
	@Test
	@DisplayName("Deve obter um livro por Id")
	public void getByIdTest() {
		//cenario
		Long id = 1L;
		Book book = createValidBook();
		book.setId(id);
		Mockito.when(repository.findById(id)).thenReturn(Optional.of(book));
	
		//execução
		Optional<Book> foundBook = service.getById(id);
		
		//verificações
		assertThat(foundBook.isPresent()).isTrue();
		assertThat(foundBook.get().getId()).isEqualTo(id);
		assertThat(foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
		assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());
		assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());
		
	}
	
	@Test
	@DisplayName("Deve retornar vazio ao obter um livro por Id quando ele não existe na base.")
	public void bookNotFoundByIdTest() {
		//cenario
		Long id = 1L;
	
		Mockito.when(repository.findById(id)).thenReturn(Optional.empty());
	
		//execução
		Optional<Book> book = service.getById(id);
		
		//verificações
		assertThat(book.isPresent()).isFalse();	
	}
	
	@Test
	@DisplayName("Deve deletar um livro")
	public void deleteBookTest() {
		Long id = 1L;
		
		//cenario
		Book book = Book.builder().id(id).isbn("123").author("Fulano").title("As aventuras").build();
		
		//execução
		//para garantir que não foi lançado nenhum erro
		org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> service.delete(book));
		 
		//verificações
		Mockito.verify(repository, Mockito.times(1)).delete(book);
	}

	@Test
	@DisplayName("Deve dar erro ao tentar deletar um livro inexistente")
	public void deleteInexistentBookTest() {
		
		Book book = createValidBook();
		
		//criei o a instância de livro mas não salvei
		Throwable exception = Assertions.catchThrowable(() ->service.delete(book));
		assertThat(exception).isInstanceOf(IllegalArgumentException.class)
							 .hasMessage("book id cant be null");
		
		org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, 
																	() ->  service.delete(book));
		
		Mockito.verify(repository, Mockito.never()).delete(book);
	}
	
	@Test
	@DisplayName("Deve dar erro ao tentar atualizar um livro inexistente")
	public void updateInexistentBookTest() {
		
		Book book = createValidBook();
		
		//criei o a instância de livro mas não salvei
		Throwable exception = Assertions.catchThrowable(() ->service.delete(book));
		assertThat(exception).isInstanceOf(IllegalArgumentException.class)
							 .hasMessage("book id cant be null");
		
		org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, 
																	() ->  service.update(book));
		
		Mockito.verify(repository, Mockito.never()).save(book);
	}

	@Test
	@DisplayName("Deve atualizar um livro")
	public void updateBookTest() {
		Long id =1L;
		Book updatingBook = Book.builder().id(id).build();
		
		//simulação
		Book updatedBook = createValidBook();
		updatedBook.setId(id);
		
		Mockito.when(repository.save(updatingBook)).thenReturn(updatedBook);
		
		//execução
		Book book = service.update(updatingBook);
		
		assertThat(book.getId()).isEqualTo(updatedBook.getId());
		assertThat(book.getIsbn()).isEqualTo(updatedBook.getIsbn());
		assertThat(book.getTitle()).isEqualTo(updatedBook.getTitle());
		assertThat(book.getAuthor()).isEqualTo(updatedBook.getAuthor());
	}
	
	@Test
	@DisplayName("Deve filtrar livros pelas propriedades")
	public void findBookTest() {
		
		//cenario
		Book book = createValidBook();
		
		PageRequest pageRequest =PageRequest.of(0, 100);
		List<Book> lista = Arrays.asList(book);
		Page<Book> page = new PageImpl<Book>(lista, pageRequest, 1);
		Mockito.when(repository.findAll(Mockito.any(Example.class),
					    Mockito.any(PageRequest.class))).thenReturn(page);
		//execução
		Page<Book> result = service.find(book, pageRequest);
		
		//verificações
		assertThat(result.getTotalElements()).isEqualTo(1);
		assertThat(result.getContent()).isEqualTo(lista);
		assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
		assertThat(result.getPageable().getPageSize()).isEqualTo(100);
	}
	
	
	@Test
	@DisplayName("Deve obter um livro pelo isbn")
	public void getBookByIsbnTest() {
		String isbn = "1230";
		Mockito.when(repository.findByIsbn(isbn))
			   .thenReturn(Optional.of(Book.builder().id(1L).isbn(isbn).build()));
		
		Optional<Book> book = service.getBookByIsbn(isbn);
		
		assertThat(book.isPresent()).isTrue();
		assertThat(book.get().getId()).isEqualTo(1L);
		assertThat(book.get().getIsbn()).isEqualTo(isbn);
		
		verify(repository, times(1)).findByIsbn(isbn); //verifica que passou pelo metodo uma  vez.
	}
	
	
	
	
	
	private Book createValidBook() {
		return Book.builder().author("Arthur").title("As asventuras").isbn("123l").build();
	}
	
}
