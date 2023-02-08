package com.api.library.dto;

import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoanDto {
	
	private Long id;
	
	@NotEmpty
	private String isbn;
	private String customer;
	private String email;
	public BookDto book ;

		
	
}
