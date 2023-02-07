package com.api.library.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class LoanFilterDto {

	private String isbn;
	private String customer;
}
