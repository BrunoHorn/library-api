package com.api.library.service.impl;

import org.springframework.stereotype.Service;

import com.api.library.model.entity.Book;
import com.api.library.model.entity.BookRepository;
import com.api.library.service.BookService;
import com.library.exception.BusinessException;

@Service
public class BookServiceImpl implements BookService {

	
	BookRepository repository;
	
	public BookServiceImpl(BookRepository repository) {
		this.repository = repository;
	}
	
	
	@Override
	public Book save(Book book) {
		if(repository.existsByIsbn(book.getIsbn())) {
			throw new BusinessException("Isbn ja Cadastrado");
		}
		return repository.save(book);
	}

}
