package com.example.fe_ai_book.model;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;


public class UserReadingPattern {
    private String userId;
    
    // 선호도 분석
    private Map<String, Double> genrePreferences; // 장르별 선호도 (0.0 - 1.0)
    private Map<String, Double> authorPreferences; // 작가별 선호도 (0.0 - 1.0)
    private Map<String, Double> publisherPreferences; // 출판사별 선호도 (0.0 - 1.0)
    
    // 독서 통계
    private double averageRating; // 평균 별점
    private int totalBooksRead; // 총 읽은 책 수
    private int totalBooksRated; // 별점을 준 책 수
    private int highRatingBooks; // 4점 이상 준 책 수
    
    // TOP 선호 정보
    private List<String> favoriteGenres; // 선호 장르 TOP 3
    private List<String> favoriteAuthors; // 선호 작가 TOP 5
    private List<String> favoritePublishers; // 선호 출판사 TOP 3
    
    // 시간 정보
    private long lastAnalyzed; // 마지막 분석 시간
    private long lastBookAdded; // 마지막 도서 추가 시간
    
    public UserReadingPattern() {
        this.genrePreferences = new HashMap<>();
        this.authorPreferences = new HashMap<>();
        this.publisherPreferences = new HashMap<>();
        this.favoriteGenres = new ArrayList<>();
        this.favoriteAuthors = new ArrayList<>();
        this.favoritePublishers = new ArrayList<>();
        this.averageRating = 0.0;
        this.totalBooksRead = 0;
        this.totalBooksRated = 0;
        this.highRatingBooks = 0;
        this.lastAnalyzed = System.currentTimeMillis();
    }
    
    public UserReadingPattern(String userId) {
        this();
        this.userId = userId;
    }
    
    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public Map<String, Double> getGenrePreferences() { return genrePreferences; }
    public void setGenrePreferences(Map<String, Double> genrePreferences) { this.genrePreferences = genrePreferences; }
    
    public Map<String, Double> getAuthorPreferences() { return authorPreferences; }
    public void setAuthorPreferences(Map<String, Double> authorPreferences) { this.authorPreferences = authorPreferences; }
    
    public Map<String, Double> getPublisherPreferences() { return publisherPreferences; }
    public void setPublisherPreferences(Map<String, Double> publisherPreferences) { this.publisherPreferences = publisherPreferences; }
    
    public double getAverageRating() { return averageRating; }
    public void setAverageRating(double averageRating) { this.averageRating = averageRating; }
    
    public int getTotalBooksRead() { return totalBooksRead; }
    public void setTotalBooksRead(int totalBooksRead) { this.totalBooksRead = totalBooksRead; }
    
    public int getTotalBooksRated() { return totalBooksRated; }
    public void setTotalBooksRated(int totalBooksRated) { this.totalBooksRated = totalBooksRated; }
    
    public int getHighRatingBooks() { return highRatingBooks; }
    public void setHighRatingBooks(int highRatingBooks) { this.highRatingBooks = highRatingBooks; }
    
    public List<String> getFavoriteGenres() { return favoriteGenres; }
    public void setFavoriteGenres(List<String> favoriteGenres) { this.favoriteGenres = favoriteGenres; }
    
    public List<String> getFavoriteAuthors() { return favoriteAuthors; }
    public void setFavoriteAuthors(List<String> favoriteAuthors) { this.favoriteAuthors = favoriteAuthors; }
    
    public List<String> getFavoritePublishers() { return favoritePublishers; }
    public void setFavoritePublishers(List<String> favoritePublishers) { this.favoritePublishers = favoritePublishers; }
    
    public long getLastAnalyzed() { return lastAnalyzed; }
    public void setLastAnalyzed(long lastAnalyzed) { this.lastAnalyzed = lastAnalyzed; }
    
    public long getLastBookAdded() { return lastBookAdded; }
    public void setLastBookAdded(long lastBookAdded) { this.lastBookAdded = lastBookAdded; }
    
    // 유틸리티 메소드들
    
    /**
     * 추천에 충분한 데이터가 있는지 확인
     * 별점 없이도 책이 3권 이상 있으면 추천 가능
     */
    public boolean hasEnoughDataForRecommendation() {
        return totalBooksRead >= 3; // 별점 조건 제거
    }
    
    /**
     * 특정 장르의 선호도 점수 반환
     */
    public double getGenreScore(String genre) {
        return genrePreferences.getOrDefault(genre, 0.0);
    }
    
    /**
     * 특정 작가의 선호도 점수 반환
     */
    public double getAuthorScore(String author) {
        return authorPreferences.getOrDefault(author, 0.0);
    }
    
    /**
     * 특정 출판사의 선호도 점수 반환
     */
    public double getPublisherScore(String publisher) {
        return publisherPreferences.getOrDefault(publisher, 0.0);
    }
    
    /**
     * 사용자가 까다로운 독자인지 확인 (평균 별점이 낮고 별점 편차가 큰 경우)
     */
    public boolean isSelectiveReader() {
        return averageRating < 3.5 && totalBooksRated >= 5;
    }
    
    /**
     * 사용자가 적극적인 독자인지 확인 (많은 책을 읽고 별점도 자주 주는 경우)
     */
    public boolean isActiveReader() {
        return totalBooksRead >= 10 && ((double) totalBooksRated / totalBooksRead) >= 0.7;
    }
    
    /**
     * 마지막 분석 시간 업데이트
     */
    public void updateAnalysisTime() {
        this.lastAnalyzed = System.currentTimeMillis();
    }
    
    /**
     * 패턴 분석이 최신인지 확인 (7일 이내)
     */
    public boolean isAnalysisRecent() {
        long sevenDaysAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000);
        return lastAnalyzed > sevenDaysAgo;
    }
    
    @Override
    public String toString() {
        return "UserReadingPattern{" +
                "userId='" + userId + '\'' +
                ", totalBooksRead=" + totalBooksRead +
                ", averageRating=" + averageRating +
                ", favoriteGenres=" + favoriteGenres +
                ", favoriteAuthors=" + favoriteAuthors +
                '}';
    }
}
