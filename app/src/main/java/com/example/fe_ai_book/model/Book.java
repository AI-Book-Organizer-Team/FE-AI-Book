package com.example.fe_ai_book.model;
import com.google.firebase.Timestamp;

public class Book {

    // Firestore에서 문서 ID는 isbn 사용
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private String publishDate;
    private String category;
    private String description;
    private String imageUrl;
    private int pageCount;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    private int imageResId; // drawable 리소스 ID (Firestore에는 저장 X)

    public Book() {} // Firestore 역직렬화용 필수

    public Book(String title, String author, int imageResId) {
        this.title = title;
        this.author = author;
        this.imageResId = imageResId;
    }

    public Book(String isbn, String title, String author, String publisher,
                String publishDate, String category, String description,
                String imageUrl, int pageCount) {

        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.publishDate = publishDate;
        this.category = category;
        this.description = description;
        this.imageUrl = imageUrl;
        this.pageCount = pageCount;
    }

    // --- Getters ---
    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getPublisher() { return publisher; }
    public String getPublishDate() { return publishDate; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public int getPageCount() { return pageCount; }
    public Timestamp getCreatedAt() { return createdAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }
    public int getImageResId() { return imageResId; }

    // --- Setters ---
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    public void setPublishDate(String publishDate) { this.publishDate = publishDate; }
    public void setCategory(String category) { this.category = category; }
    public void setDescription(String description) { this.description = description; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setPageCount(int pageCount) { this.pageCount = pageCount; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }
}
