package com.api.library.service;

import java.util.Optional;


import com.api.library.model.entity.Loan;

public interface LoanService {

	Loan save(Loan loan);

	Optional<Loan> getById(Long id);

	void update(Loan loan);



}
