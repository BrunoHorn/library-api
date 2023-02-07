package com.api.library.service.impl;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.api.library.dto.LoanFilterDto;
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
		return  repository.findById(id);
	}

	@Override
	public Loan update(Loan loan) {
		return repository.save(loan);
	}

	@Override
	public Page<Loan> find(LoanFilterDto filterDto, Pageable pageRequest) {
		
		return repository.findByBookIsbnOrCustomer(filterDto.getIsbn(), filterDto.getCustomer(), pageRequest);
	}



}
