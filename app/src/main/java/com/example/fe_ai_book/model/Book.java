package com.example.fe_ai_book.model;

//더미 데이터. 추후 API 연결 후 수정 필요//

public class Book {
    private String title;
    private String author;
    private String dateSaved;
    private int imageResId; // drawable resource id

    public Book(String title, String author, String dateSaved, int imageResId) {
        this.title = title;
        this.author = author;
        this.dateSaved = dateSaved;
        this.imageResId = imageResId;
    }

    public Book(String title, String author, int imageResId) {
        this.title = title;
        this.author = author;
        this.dateSaved = null;
        this.imageResId = imageResId;
    }

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
}



