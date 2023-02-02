package com.api.library.model.entity;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Loan {

	private Long id;
	private String customer;
	private Book book;
	private LocalDate loanDate;
	private Boolean returned;

}
