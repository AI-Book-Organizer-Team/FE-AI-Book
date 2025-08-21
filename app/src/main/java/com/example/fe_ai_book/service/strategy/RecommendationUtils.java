package com.example.fe_ai_book.service.strategy;

import com.example.fe_ai_book.model.Book;
import com.example.fe_ai_book.model.BookRecommendation;

import java.util.ArrayList;
import java.util.List;

public class RecommendationUtils {
    public static BookRecommendation fromBook(String userId, Book book, double score, String reason, String type) {
        return new BookRecommendation(userId,
                convertToBookEntity(book),
                score,
                reason,
                type);
    }

    private static com.example.fe_ai_book.entity.BookEntity convertToBookEntity(Book book) {
        com.example.fe_ai_book.entity.BookEntity entity = new com.example.fe_ai_book.entity.BookEntity();
        entity.setTitle(book.getTitle());
        entity.setAuthor(book.getAuthor());
        entity.setIsbn(book.getIsbn());
        entity.setCategory(book.getCategory());
        entity.setPublisher(book.getPublisher());
        entity.setImageUrl(book.getImageUrl());
        return entity;
    }

    public static List<BookRecommendation> limit(List<BookRecommendation> list, int max) {
        List<BookRecommendation> out = new ArrayList<>();
        for (int i = 0; i < list.size() && i < max; i++) out.add(list.get(i));
        return out;
    }
}
