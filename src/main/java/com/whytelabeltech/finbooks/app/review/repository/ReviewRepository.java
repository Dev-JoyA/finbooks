package com.whytelabeltech.finbooks.app.review.repository;

import com.whytelabeltech.finbooks.app.review.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
}
