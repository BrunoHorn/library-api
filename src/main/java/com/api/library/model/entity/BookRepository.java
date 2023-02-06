package com.api.library.model.entity;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {

	boolean existsByIsbn(String isbn);

	Optional<Book> findByIsbn(String isbn);

}
