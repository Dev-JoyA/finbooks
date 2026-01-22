package com.whytelabeltech.finbooks.app.author.repository;

import com.whytelabeltech.finbooks.app.author.model.Author;
import jakarta.validation.constraints.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    Optional<Author> findByEmail(@Email String email);
}
