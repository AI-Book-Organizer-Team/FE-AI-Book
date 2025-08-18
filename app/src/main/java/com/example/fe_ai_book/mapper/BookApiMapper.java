package com.example.fe_ai_book.mapper;

import androidx.annotation.NonNull;

import com.example.fe_ai_book.model.Book;
import com.example.fe_ai_book.model.BookDetailEnvelope;

public final class BookApiMapper {
    private BookApiMapper(){}

    @NonNull
    public static Book toUi(@NonNull BookDetailEnvelope.Book api) {
        com.example.fe_ai_book.model.Book b = new com.example.fe_ai_book.model.Book();
        b.setTitle(nullToEmpty(api.bookname));
        b.setAuthor(nullToEmpty(api.authors));
        b.setPublisher(nullToEmpty(api.publisher));
        // publishDate는 YYYY-MM-DD 쪽(api.publication_date) 우선
        b.setPublishDate(firstNonEmpty(api.publication_date, api.publication_year));
        b.setIsbn(firstNonEmpty(api.isbn13, api.isbn));
        b.setImageUrl(api.bookImageURL);
        b.setImageResId(0);
        // dateSaved는 더미만 쓰던 필드니까 비워둠
        b.setDateSaved(null);
        return b;
    }

    private static String nullToEmpty(String s){ return s == null ? "" : s; }
    private static String firstNonEmpty(String... arr){
        for (String s: arr){ if (s != null && !s.isBlank()) return s; }
        return "";
    }
}
