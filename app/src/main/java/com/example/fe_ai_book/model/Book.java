package com.example.fe_ai_book.model;

//더미 데이터. 추후 API 연결 후 수정 필요//

public class Book {
    private String title;
    private String author;
    private String dateSaved;
    private int imageResId; // drawable resource id
    private String publishDate;
    private String publisher;
    private String isbn;

    // 기존 생성자
    public Book(String title, String author, String dateSaved, int imageResId) {
        this.title = title;
        this.author = author;
        this.dateSaved = dateSaved;
        this.imageResId = imageResId;
    }

    // 기본 생성자 추가
    public Book() {
    }

    // 세 번째 생성자 (dateSaved 없음)
    public Book(String title, String author, int imageResId) {
        this.title = title;
        this.author = author;
        this.dateSaved = null;
        this.imageResId = imageResId;
    }

    // 모든 필드를 포함하는 생성자
    public Book(String title, String author, String dateSaved, int imageResId, 
                String publishDate, String publisher, String isbn) {
        this.title = title;
        this.author = author;
        this.dateSaved = dateSaved;
        this.imageResId = imageResId;
        this.publishDate = publishDate;
        this.publisher = publisher;
        this.isbn = isbn;
    }

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

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
}



