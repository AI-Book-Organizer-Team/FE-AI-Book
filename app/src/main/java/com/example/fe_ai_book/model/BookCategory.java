package com.example.fe_ai_book.model;

import java.util.List;

public class BookCategory {
    private String categoryTitle;
    private List<Book> bookList;

    public BookCategory(String categoryTitle, List<Book> bookList) {
        this.categoryTitle = categoryTitle;
        this.bookList = bookList;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public void setCategoryTitle(String categoryTitle) {
        this.categoryTitle = categoryTitle;
    }

    public List<Book> getBookList() {
        return bookList;
    }

    public void setBookList(List<Book> bookList) {
        this.bookList = bookList;
    }
}
