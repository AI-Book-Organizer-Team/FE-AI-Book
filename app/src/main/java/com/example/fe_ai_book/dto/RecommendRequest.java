package com.example.fe_ai_book.dto;

import java.util.List;

// Request
public class RecommendRequest {
    public List<String> userBooks;
    public Integer topK;

    public RecommendRequest(List<String> userBooks, Integer topK) {
        this.userBooks = userBooks;
        this.topK = topK;
    }
}