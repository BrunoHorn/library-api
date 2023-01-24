package com.api.library.service;

import org.springframework.stereotype.Service;

import com.api.library.model.entity.Book;

@Service
public interface BookService {

	Book save(Book book);

}
