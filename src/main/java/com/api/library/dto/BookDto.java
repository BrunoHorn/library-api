package com.api.library.dto;

import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
//@NoArgsConstructor
public class BookDto {

	public BookDto() {}
	
	private Long id;
	
	@NotEmpty
	private String title;	
	
	@NotEmpty
	private String author;
	
	@NotEmpty
	private String isbn;
}
