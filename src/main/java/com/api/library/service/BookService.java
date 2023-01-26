package com.api.library.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.api.library.model.entity.Book;

@Service
public interface BookService {

	Book save(Book book);

	Optional<Book> getById(Long id);

	void delete(Book book);

	Book update(Book book);

}
