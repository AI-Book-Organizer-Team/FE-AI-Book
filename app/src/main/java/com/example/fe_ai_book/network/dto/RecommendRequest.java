// RecommendRequest.java
package com.example.fe_ai_book.network.dto;

public class RecommendRequest {

    private String user_id;  // Flask가 기대하는 필드명 그대로
    private int top_k;

    public RecommendRequest(String userId, int topK) {
        this.user_id = userId;
        this.top_k = topK;
    }

    // 기본 생성자 (Gson 등에서 필요할 수 있음)
    public RecommendRequest() {
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public int getTop_k() {
        return top_k;
    }

    public void setTop_k(int top_k) {
        this.top_k = top_k;
    }
}
