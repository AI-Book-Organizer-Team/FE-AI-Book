package com.example.fe_ai_book.model;

import com.example.fe_ai_book.entity.BookEntity;

/**
 * AI_02: 사용자 패턴 기반 추천 결과를 저장하는 모델
 */
public class BookRecommendation {
    private String recommendationId;
    private String userId;
    private BookEntity recommendedBook;
    private double confidenceScore; // 추천 신뢰도 (0.0 - 1.0)
    private String recommendationReason; // 추천 이유 설명
    private String recommendationType; // "genre", "author", "publisher", "collaborative" 등
    private long createdAt; // 추천 생성 시간
    private boolean isAccepted; // 사용자가 추천을 받아들였는지
    private boolean isViewed; // 사용자가 추천을 봤는지
    
    public BookRecommendation() {
        this.createdAt = System.currentTimeMillis();
        this.isAccepted = false;
        this.isViewed = false;
    }
    
    public BookRecommendation(String userId, BookEntity book, double score, String reason, String type) {
        this();
        this.userId = userId;
        this.recommendedBook = book;
        this.confidenceScore = score;
        this.recommendationReason = reason;
        this.recommendationType = type;
        this.recommendationId = generateId();
    }
    
    private String generateId() {
        return userId + "_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
    }
    
    // Getters and Setters
    public String getRecommendationId() { return recommendationId; }
    public void setRecommendationId(String recommendationId) { this.recommendationId = recommendationId; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public BookEntity getRecommendedBook() { return recommendedBook; }
    public void setRecommendedBook(BookEntity recommendedBook) { this.recommendedBook = recommendedBook; }
    
    public double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(double confidenceScore) { this.confidenceScore = confidenceScore; }
    
    public String getRecommendationReason() { return recommendationReason; }
    public void setRecommendationReason(String recommendationReason) { this.recommendationReason = recommendationReason; }
    
    public String getRecommendationType() { return recommendationType; }
    public void setRecommendationType(String recommendationType) { this.recommendationType = recommendationType; }
    
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    
    public boolean isAccepted() { return isAccepted; }
    public void setAccepted(boolean accepted) { isAccepted = accepted; }
    
    public boolean isViewed() { return isViewed; }
    public void setViewed(boolean viewed) { isViewed = viewed; }
    
    /**
     * 추천 신뢰도가 높은지 확인
     */
    public boolean isHighConfidence() {
        return confidenceScore >= 0.7;
    }
    
    /**
     * 추천 이유를 문구로 반환
     */
    public String getDisplayReason() {
        switch (recommendationType) {
            case "genre":
                return "선호하는 " + recommendationReason + " 장르 도서입니다";
            case "author":
                return recommendationReason + " 작가의 다른 도서입니다";
            case "publisher":
                return "자주 읽는 " + recommendationReason + " 출판사 도서입니다";
            case "collaborative":
                return "비슷한 취향의 독자들이 좋아한 도서입니다";
            case "high_rating":
                return "평점이 높은 인기 도서입니다";
            default:
                return recommendationReason;
        }
    }
    
    @Override
    public String toString() {
        return "BookRecommendation{" +
                "userId='" + userId + '\'' +
                ", bookTitle='" + (recommendedBook != null ? recommendedBook.getTitle() : "null") + '\'' +
                ", score=" + confidenceScore +
                ", type='" + recommendationType + '\'' +
                ", reason='" + recommendationReason + '\'' +
                '}';
    }
}
