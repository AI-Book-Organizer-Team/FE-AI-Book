package com.example.fe_ai_book.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

import java.util.Date;

@Entity(tableName = "books")
public class BookEntity {
    @PrimaryKey
    @NonNull
    private String isbn;
//    private String id;

    private String title;
    private String author;
    private String publisher;
    private String publishDate;
    private String description;
    private String imageUrl;
    private String category;
    private Integer pageCount;
    private Double rating;
    private String notes; // 사용자 메모
    private Long createdAt;
    private Long updatedAt;
    private boolean isSyncedToCloud; // 클라우드 동기화 여부

    // 기본 생성자 (Room에서 필요)
    public BookEntity() {
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.isSyncedToCloud = false;
    }

    // 전체 생성자
    public BookEntity(@NonNull String isbn, String title, String author, String publisher,
                     String publishDate, String description, String imageUrl,
                     String category, Integer pageCount, Double rating, String notes) {
//        this.id = id;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.publishDate = publishDate;
        this.isbn = isbn;
        this.description = description;
        this.imageUrl = imageUrl;
        this.category = category;
        this.pageCount = pageCount;
        this.rating = rating;
        this.notes = notes;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.isSyncedToCloud = false;
    }

    // Getter & Setter
//    @NonNull
//    public String getId() { return id; }
//    public void setId(@NonNull String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { 
        this.title = title; 
        this.updatedAt = System.currentTimeMillis();
    }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { 
        this.author = author; 
        this.updatedAt = System.currentTimeMillis();
    }

    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { 
        this.publisher = publisher; 
        this.updatedAt = System.currentTimeMillis();
    }

    public String getPublishDate() { return publishDate; }
    public void setPublishDate(String publishDate) { 
        this.publishDate = publishDate; 
        this.updatedAt = System.currentTimeMillis();
    }

    @NonNull
    public String getIsbn() { return isbn; }
    public void setIsbn(@NonNull String isbn) {
        this.isbn = isbn; 
        this.updatedAt = System.currentTimeMillis();
    }

    public String getDescription() { return description; }
    public void setDescription(String description) { 
        this.description = description; 
        this.updatedAt = System.currentTimeMillis();
    }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { 
        this.imageUrl = imageUrl; 
        this.updatedAt = System.currentTimeMillis();
    }

    public String getCategory() { return category; }
    public void setCategory(String category) { 
        this.category = category; 
        this.updatedAt = System.currentTimeMillis();
    }

    public Integer getPageCount() { return pageCount; }
    public void setPageCount(Integer pageCount) { 
        this.pageCount = pageCount; 
        this.updatedAt = System.currentTimeMillis();
    }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { 
        this.rating = rating; 
        this.updatedAt = System.currentTimeMillis();
    }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { 
        this.notes = notes; 
        this.updatedAt = System.currentTimeMillis();
    }

    public Long getCreatedAt() { return createdAt; }
    public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }

    public Long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Long updatedAt) { this.updatedAt = updatedAt; }

    public boolean isSyncedToCloud() { return isSyncedToCloud; }
    public void setSyncedToCloud(boolean syncedToCloud) { 
        this.isSyncedToCloud = syncedToCloud; 
        this.updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "BookEntity{" +
//                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", publisher='" + publisher + '\'' +
                ", isbn='" + isbn + '\'' +
                '}';
    }
}
