package com.whytelabeltech.finbooks.app.book.model;

import com.whytelabeltech.finbooks.app.author.model.Author;
import com.whytelabeltech.finbooks.app.category.model.Category;
import com.whytelabeltech.finbooks.app.review.model.Review;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String isbn;

    private LocalDateTime publishedDate;

//    @OneToMany(fetch = FetchType.EAGER)
//    @JsonIgnore
//    private Author author;

    private String rating;

    //private Review review;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private String comment;

    @CreationTimestamp
    private LocalDateTime createdAt;


}
