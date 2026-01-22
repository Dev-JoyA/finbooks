package com.whytelabeltech.finbooks.app.book.repository;

import com.whytelabeltech.finbooks.app.book.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
