package com.api.library.resource;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.api.library.controller.BookController;
import com.api.library.dto.BookDto;
import com.api.library.model.entity.Book;
import com.api.library.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.exception.BusinessException;

//Teste de integração
@ExtendWith(SpringExtension.class)//Cria um mini contexto para Rodar o teste
@ActiveProfiles("test") //roda apenas no contexto de teste
@WebMvcTest(controllers = BookController.class )
@AutoConfigureMockMvc//configura objeto para fazer as requisições
public class BookControllerTest {

	static String BOOK_API = "/api/books"; //rota da chamada
	
	@Autowired
	MockMvc mvc; //Moka as requisiçãoes 
	
	@MockBean
	BookService service;
	
	
	@Test
	@DisplayName("Deve criar um livro com sucesso.")
	public void createBookTeste()  throws Exception{
			

		
		BookDto dto = createNewBook();// livro que sera salvo
		Book savedBook = Book.builder().id(101L) .author("Artur").title("As Aventuras").isbn("001").build();//livro que foi salvo
		
		
		BDDMockito.given(service.save(Mockito.any(Book.class))).willReturn(savedBook); //para gerar o ID do objeto
		
		String json = new ObjectMapper().writeValueAsString(dto); //transforma objeto em json
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API) //passa o tipo de requisição e e o endereço
				  .contentType(MediaType.APPLICATION_JSON)
				  .accept(MediaType.APPLICATION_JSON)
				  .content(json);
		
		//verificador
		mvc.perform(request)
		.andExpect(MockMvcResultMatchers.status().isCreated())  //espera status 201 
		.andExpect(jsonPath("id").value(savedBook.getId())) //espera o retorno do objeto criado - id não vazio
		.andExpect(jsonPath( "title").value(savedBook.getTitle()))
		.andExpect(jsonPath("author").value(savedBook.getAuthor()))
		.andExpect(jsonPath("isbn").value(savedBook.getIsbn()))
		;
		
	}

	@Test
	@DisplayName("Deve lançar erro quando não houver dados o suficiente para criação do livro.")
	public void createInvalidBookTeste() throws Exception{
		
		String json = new ObjectMapper().writeValueAsString(new BookDto()); //transforma objeto em json
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API) //passa o tipo de requisição e e o endereço
				  .contentType(MediaType.APPLICATION_JSON)
				  .accept(MediaType.APPLICATION_JSON)
				  .content(json);
		//verificador
		mvc.perform(request)
		.andExpect(status().isBadRequest() )
		.andExpect(jsonPath("errors",Matchers.hasSize(3)));
		;
	}
	
	@Test
	@DisplayName("Deve lançar erro ao testar cadastrar livro com ISBN duplicado")
	public void createBookWithDuplicatedIsbn() throws  Exception{
		BookDto dto = createNewBook();// livro que sera salvo
		String mensagemErro = "isbn já cadastrado";
		String json = new ObjectMapper().writeValueAsString(dto); //transforma objeto em json
		BDDMockito.given(service.save(Mockito.any(Book.class)))
						.willThrow(new BusinessException(mensagemErro));
		
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API) //passa o tipo de requisição e e o endereço
				  .contentType(MediaType.APPLICATION_JSON)
				  .accept(MediaType.APPLICATION_JSON)
				  .content(json);
		
		mvc.perform(request).andExpect(status().isBadRequest())
		.andExpect(jsonPath("errors", Matchers.hasSize(1)))
		.andExpect(jsonPath("errors[0]").value(mensagemErro));
	}
	
	
	
	@Test
	@DisplayName("Deve obter informações de um livro")
	public void getBookDetailsTest() throws Exception{
		
		//cenario
		Long id = 11L;
		
		Book book = Book.builder().id(id)
				  .title(createNewBook().getTitle())
				  .author(createNewBook().getAuthor())
				  .isbn(createNewBook().getIsbn())
				  .build();
		
		
		BDDMockito.given(service.getById(id)).willReturn(Optional.of(book));
		
		//execução
				MockHttpServletRequestBuilder request = 
				MockMvcRequestBuilders.get(BOOK_API.concat("/" + id))
				 					  .accept(MediaType.APPLICATION_JSON);
				//validação
				mvc
					.perform(request).andExpect(status().isOk())
					.andExpect(jsonPath("id").isNotEmpty()) 
					.andExpect(jsonPath("title").value(createNewBook().getTitle()))
					.andExpect(jsonPath("author").value(createNewBook().getAuthor()))
					.andExpect(jsonPath("isbn").value(createNewBook().getIsbn()));  

	}
	
	@Test
	@DisplayName("Deve retornar resource not found quando livro procurado não existe")
	public void bookNotFound() throws Exception{
		
		BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());
		
		//execução
		MockHttpServletRequestBuilder request = 
		MockMvcRequestBuilders.get(BOOK_API.concat("/" + 1L))
		 					  .accept(MediaType.APPLICATION_JSON);
		
		//validação
		mvc
			.perform(request)
			.andExpect(status().isNotFound());
	}
	
	
	@Test
	@DisplayName("Deve deletar um livro")
	public void deleteBookTest() throws Exception {
		BDDMockito.given(service.getById(Mockito.anyLong()))
								.willReturn(Optional.of(Book.builder().id(11L).build()));
		
		//execução
		MockHttpServletRequestBuilder request = 
		MockMvcRequestBuilders.delete(BOOK_API.concat("/" + 11L));
	
		mvc.perform(request)
			.andExpect(status().isNoContent());
	}
	
	@Test
	@DisplayName("Deve retornar not found quando não encontrar livro para deletar")
	public void deleteInexistentBookTest() throws Exception {
		BDDMockito.given(service.getById(Mockito.anyLong()))
								.willReturn(Optional.empty());
		
		//execução
		MockHttpServletRequestBuilder request = 
		MockMvcRequestBuilders.delete(BOOK_API.concat("/" + 11L));
	
		mvc.perform(request)
			.andExpect(status().isNotFound());
	}
	
	
	@Test
	@DisplayName("Deve atualizar um livro")
	public void updateBookTest() throws Exception{
		Long id = 11L;
		String json = new ObjectMapper().writeValueAsString(createNewBook());

		Book updatingBook = Book.builder().id(id).author("some author").title("some title").isbn("001").build();
		
		BDDMockito.given(service.getById(id))
								.willReturn(Optional.of(updatingBook));
		
		Book updatedBook = Book.builder().id(id).author("Artur").title("As Aventuras").isbn("001").build();
		
		BDDMockito.given(service.update(updatingBook))
				                .willReturn(updatedBook);
		
		MockHttpServletRequestBuilder request = 
				MockMvcRequestBuilders.put(BOOK_API.concat("/" + 11L))
									  .content(json)
									  .accept(MediaType.APPLICATION_JSON)
									  .contentType(MediaType.APPLICATION_JSON);

		mvc.perform(request)
		   .andExpect(status().isOk())
			.andExpect(jsonPath("id").isNotEmpty()) 
			.andExpect(jsonPath("title").value(createNewBook().getTitle()))
			.andExpect(jsonPath("author").value(createNewBook().getAuthor()))
			.andExpect(jsonPath("isbn").value("001"));
		
	}
	
	@Test
	@DisplayName("Deve retornar 404 quando tentar atualizar um livro inexistente")
	public void updateInexistentBookTest() throws Exception{
			
			String json = new ObjectMapper().writeValueAsString(createNewBook());

			BDDMockito.given(service.getById(Mockito.anyLong()))
					  .willReturn(Optional.empty());

			MockHttpServletRequestBuilder request = 
					MockMvcRequestBuilders.put(BOOK_API.concat("/" + 11L))
										  .content(json)
										  .accept(MediaType.APPLICATION_JSON)
										  .contentType(MediaType.APPLICATION_JSON);

			mvc.perform(request)
			   .andExpect(status().isNotFound());
	}
	
	@Test
	@DisplayName("Deve filtrar livros")
	public void findBooksTest() throws Exception{
		
		Long id = 1L;
		
		Book book = Book.builder().id(id)
								  .author(createNewBook().getAuthor())
								  .title(createNewBook().getTitle())
								  .isbn(createNewBook().getIsbn())
								  .build();
		
		BDDMockito.given(service.find(Mockito.any(Book.class), Mockito.any(Pageable.class)))
		.willReturn(new PageImpl<Book>(Arrays.asList(book), PageRequest.of(0, 100), 1));
		
		String queryString = String.format("?title=%s&author=%s&page=0&size=100", 
				book.getTitle(), book.getAuthor());
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.get(BOOK_API.concat(queryString))
				.accept(MediaType.APPLICATION_JSON);
		
		mvc.perform(request).andExpect(status().isOk())
							.andExpect(jsonPath("content", Matchers.hasSize(1)))
							.andExpect(jsonPath("totalElements").value(1))
							.andExpect(jsonPath("pageable.pageSize").value(100))
							.andExpect(jsonPath("pageable.pageNumber").value(0))
							;
	}
	
	
	
	
	

	private BookDto createNewBook() {
		return BookDto.builder().author("Artur").title("As Aventuras").isbn("001").build();
	}
	
}
