package com.example.fe_ai_book.model;

import com.google.firebase.Timestamp;

// Firestore에 저장할 수 있는 Book 모델
public class Book {
    private String id;
    private String title;
    private String author;
<<<<<<< HEAD
    private String dateSaved;
    private int imageResId; // drawable resource id
<<<<<<< HEAD
=======

    private String imageUrl;
>>>>>>> 5e7144d9bf40bbc30208eed98208c31d0daffa14
=======
    private String dateSaved;   // 앱에서 추가한 필드
    private int imageResId;     // 로컬 이미지 리소스 id (Firestore에는 저장 X)

    private String imageUrl;
>>>>>>> seonah
    private String publishDate;
    private String publisher;
    private String isbn;
    private String category;
    private String description;
    private String notes;
    private int pageCount;
    private double rating;
    private String userId;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // 빈 생성자 (Firestore 직렬화/역직렬화용 필수)
    public Book() {}

    // 생성자 오버로드
    public Book(String title, String author, String publishDate, String publisher, String isbn) {
        this.title = title;
        this.author = author;
        this.publishDate = publishDate;
        this.publisher = publisher;
        this.isbn = isbn;
    }

<<<<<<< HEAD
<<<<<<< HEAD
=======
    // 모든 필드를 포함하는 생성자
    public Book(String title, String author, String dateSaved, String imageUrl,
                String publishDate, String publisher, String isbn) {
        this.title = title;
        this.author = author;
        this.dateSaved = dateSaved;
        this.imageUrl = imageUrl;
        this.publishDate = publishDate;
        this.publisher = publisher;
        this.isbn = isbn;
    }

>>>>>>> 5e7144d9bf40bbc30208eed98208c31d0daffa14
    // Getters
    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getDateSaved() {
        return dateSaved;
    }

    public int getImageResId() {
        return imageResId;
    }

<<<<<<< HEAD
=======
    public String getImageUrl() {
        return imageUrl;
    }
>>>>>>> 5e7144d9bf40bbc30208eed98208c31d0daffa14
    public String getPublishDate() {
        return publishDate;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getIsbn() {
        return isbn;
    }

    // Setters
    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setDateSaved(String dateSaved) {
        this.dateSaved = dateSaved;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

<<<<<<< HEAD
=======
    public void setImageUrl (String ImageUrl) {
        this.imageUrl = ImageUrl;
    }

>>>>>>> 5e7144d9bf40bbc30208eed98208c31d0daffa14
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
=======
    // --- Getters ---
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getDateSaved() { return dateSaved; }
    public int getImageResId() { return imageResId; }
    public String getImageUrl() { return imageUrl; }
    public String getPublishDate() { return publishDate; }
    public String getPublisher() { return publisher; }
    public String getIsbn() { return isbn; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public String getNotes() { return notes; }
    public int getPageCount() { return pageCount; }
    public double getRating() { return rating; }
    public String getUserId() { return userId; }
    public Timestamp getCreatedAt() { return createdAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }

    // --- Setters ---
    public void setId(String id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setDateSaved(String dateSaved) { this.dateSaved = dateSaved; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setPublishDate(String publishDate) { this.publishDate = publishDate; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public void setCategory(String category) { this.category = category; }
    public void setDescription(String description) { this.description = description; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setPageCount(int pageCount) { this.pageCount = pageCount; }
    public void setRating(double rating) { this.rating = rating; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
>>>>>>> seonah
}
