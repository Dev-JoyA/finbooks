package com.whytelabeltech.finbooks.app.author.repository;

import com.whytelabeltech.finbooks.app.author.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, Long> {
}
