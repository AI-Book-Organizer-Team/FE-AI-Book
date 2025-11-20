package com.example.fe_ai_book.network.dto;

public class BookRecommendation {

    private String id;
    private String title;
    private String author;
    private String description;
    private double score;

    // 선택 필드 (서버에서 null/없음일 수 있음)
    private String isbn;
    private String imageUrl;
    private String category;

    public BookRecommendation() {
    }

    // 필요하면 생성자 추가해서 써도 됨
    // public BookRecommendation(...) { ... }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}

