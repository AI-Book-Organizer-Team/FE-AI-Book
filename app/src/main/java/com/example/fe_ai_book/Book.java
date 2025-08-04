package com.example.fe_ai_book;

public class Book {
    private String title;
    private String author;
    private String publisher;
    private String description;
    private String coverImageUrl;

    public Book(String title, String author, String publisher, String description, String coverImageUrl) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.description = description;
        this.coverImageUrl = coverImageUrl;
    }

    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getPublisher() { return publisher; }
    public String getDescription() { return description; }
    public String getCoverImageUrl() { return coverImageUrl; }
}