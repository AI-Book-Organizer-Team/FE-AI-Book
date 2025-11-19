package com.example.fe_ai_book.network.dto;

import java.util.List;

public class RecommendResponse {

    private String user_id;
    private int count;
    private List<BookRecommendation> items;
    private String message;   // 없으면 null

    public RecommendResponse() {
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<BookRecommendation> getItems() {
        return items;
    }

    public void setItems(List<BookRecommendation> items) {
        this.items = items;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

