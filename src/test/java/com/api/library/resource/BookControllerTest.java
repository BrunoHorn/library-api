package com.api.library.resource;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.api.library.dto.BookDto;
import com.api.library.model.entity.Book;
import com.api.library.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.exception.BusinessException;

//Teste de integração
@ExtendWith(SpringExtension.class)//Cria um mini contexto para Rodar o teste
@ActiveProfiles("test") //roda apenas no contexto de teste
@WebMvcTest
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
			

		
		BookDto dto = creatNewBook();// livro que sera salvo
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
		.andExpect(jsonPath("id").value(dto.getId())) //espera o retorno do objeto criado - id não vazio
		.andExpect(jsonPath( "title").value(dto.getTitle()))
		.andExpect(jsonPath("author").value(dto.getAuthor()))
		.andExpect(jsonPath("isbn").value(dto.getIsbn()))
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
		BookDto dto = creatNewBook();// livro que sera salvo
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
	
	
	
	

	private BookDto creatNewBook() {
		return BookDto.builder().author("Artur").title("As Aventuras").isbn("001").build();
	}
	
}
