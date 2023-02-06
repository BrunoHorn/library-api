package com.api.library.service.impl;

import java.util.Optional;

import com.api.library.model.entity.Loan;
import com.api.library.model.entity.LoanRepository;
import com.api.library.service.LoanService;
import com.library.exception.BusinessException;

public class LoanServiceImpl implements LoanService {

	private LoanRepository repository;
	
	public LoanServiceImpl(LoanRepository repository) {
		this.repository = repository;
	}

	@Override
	public Loan save(Loan loan) {
		if(repository.existsByBookAndNotReturned(loan.getBook())) {
		 throw new BusinessException("Book already loaned");
	}
		return repository.save(loan);}

	@Override
	public Optional<Loan> getById(Long id) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public void update(Loan loan) {
		// TODO Auto-generated method stub
		
	}

}
