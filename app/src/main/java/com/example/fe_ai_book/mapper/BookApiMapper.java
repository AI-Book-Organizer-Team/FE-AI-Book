package com.example.fe_ai_book.mapper;

import androidx.annotation.NonNull;

import com.example.fe_ai_book.model.Book;
import com.example.fe_ai_book.model.BookDetailEnvelope;
import com.example.fe_ai_book.entity.BookEntity;
import com.example.fe_ai_book.model.BookSearchEnvelope;

public final class BookApiMapper {
    private BookApiMapper(){}

    @NonNull
    public static Book toUi(@NonNull BookDetailEnvelope.Book api) {
        Book b = new Book();
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

        // 카테고리 매핑
        String rawCategory = api.class_nm;
        if (rawCategory != null && !rawCategory.isBlank()) {
            // " > " 구분자로 잘라서 첫 부분만 사용
            String majorCategory = rawCategory.split(">")[0].trim();
            b.setCategory(majorCategory);
        } else {
            b.setCategory("기타");
        }

        return b;
    }


    // Convert UI Book model to BookEntity for database storage
    @NonNull
    public static BookEntity toEntity(@NonNull Book book) {
        BookEntity entity = new BookEntity();
        
        // Generate unique ID using ISBN or title+author combination
        String id;
        if (book.getIsbn() != null && !book.getIsbn().trim().isEmpty()) {
            id = book.getIsbn().trim();
        } else {
            // Fallback: create ID from title and author
            String title = book.getTitle() != null ? book.getTitle().trim() : "unknown";
            String author = book.getAuthor() != null ? book.getAuthor().trim() : "unknown";
            id = (title + "_" + author).replaceAll("[^a-zA-Z0-9_]", "_");
        }
        
//        entity.setId(id);
        entity.setTitle(nullToEmpty(book.getTitle()));
        entity.setAuthor(nullToEmpty(book.getAuthor()));
        entity.setPublisher(nullToEmpty(book.getPublisher()));
        entity.setPublishDate(nullToEmpty(book.getPublishDate()));
        entity.setIsbn(nullToEmpty(book.getIsbn()));
        entity.setImageUrl(nullToEmpty(book.getImageUrl()));
        
        // Set default values for fields not available in Book model
        entity.setDescription("");
        entity.setCategory("");
        entity.setPageCount(null);
        entity.setRating(0.0);
        entity.setNotes("");
        
        return entity;
    }
    
    private static String nullToEmpty(String s){ return s == null ? "" : s; }
    private static String firstNonEmpty(String... arr){
        for (String s: arr){ if (s != null && !s.isBlank()) return s; }
        return "";
    }

    @NonNull
    public static Book toUi(@NonNull BookSearchEnvelope.Book api) {
        Book b = new Book();
        b.setTitle(nullToEmpty(api.bookname));
        b.setAuthor(nullToEmpty(api.authors));
        b.setPublisher(nullToEmpty(api.publisher));
        b.setPublishDate(nullToEmpty(api.publication_year));
        b.setIsbn(nullToEmpty(api.isbn13));
        b.setImageUrl(api.bookImageURL);
        b.setCategory(api.class_nm != null ? api.class_nm : "기타");
        return b;
    }

}
