package com.api.library.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ReturnedLoanDto {
	
	public ReturnedLoanDto() {
		
	}
	
	private Boolean returned;
}
