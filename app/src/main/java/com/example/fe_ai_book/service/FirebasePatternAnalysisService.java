package com.example.fe_ai_book.service;

import android.util.Log;

import com.example.fe_ai_book.model.Book;
import com.example.fe_ai_book.model.UserReadingPattern;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Firebase 기반 사용자 독서 패턴 분석 서비스
 * 별점 없이도 장르, 작가, 출판사 기반으로 패턴 분석
 */
public class FirebasePatternAnalysisService {
    private static final String TAG = "FirebasePatternAnalysis";
    
    private FirebaseBookQueryService bookQueryService;
    
    public FirebasePatternAnalysisService() {
        this.bookQueryService = new FirebaseBookQueryService();
    }
    
    public interface PatternAnalysisCallback {
        void onAnalysisComplete(UserReadingPattern pattern);
        void onAnalysisError(String error);
    }
    
    /**
     * Firebase에서 사용자 도서 데이터를 가져와서 패턴 분석 실행
     */
    public void analyzeUserPattern(String userId, PatternAnalysisCallback callback) {
        Log.d(TAG, "Starting Firebase-based pattern analysis for user: " + userId);
        
        bookQueryService.getUserBooks(userId, new FirebaseBookQueryService.BookQueryCallback() {
            @Override
            public void onBooksLoaded(List<Book> books) {
                try {
                    Log.d(TAG, "Loaded " + books.size() + " books from Firebase for user: " + userId);
                    
                    UserReadingPattern pattern = new UserReadingPattern(userId);
                    
                    if (books.isEmpty()) {
                        Log.d(TAG, "No books found for user: " + userId);
                        callback.onAnalysisComplete(pattern);
                        return;
                    }
                    
                    // 기본 통계 계산
                    calculateBasicStats(pattern, books);
                    
                    // 장르 선호도 분석 (별점 없이 도서 수 기반)
                    analyzeGenrePreferences(pattern, books);
                    
                    // 작가 선호도 분석
                    analyzeAuthorPreferences(pattern, books);
                    
                    // 출판사 선호도 분석
                    analyzePublisherPreferences(pattern, books);
                    
                    // TOP 선호 정보 업데이트
                    updateTopPreferences(pattern);
                    
                    // 분석 완료 시간 업데이트
                    pattern.updateAnalysisTime();
                    
                    Log.d(TAG, "Firebase pattern analysis completed for user: " + userId);
                    Log.d(TAG, "Analysis result: " + pattern.toString());
                    
                    callback.onAnalysisComplete(pattern);
                    
                } catch (Exception e) {
                    Log.e(TAG, "Error analyzing user pattern", e);
                    callback.onAnalysisError("Analysis failed: " + e.getMessage());
                }
            }
            
            @Override
            public void onError(String error) {
                Log.e(TAG, "Failed to load books from Firebase: " + error);
                callback.onAnalysisError("Failed to load user books: " + error);
            }
        });
    }
    
    /**
     * 기본 통계 계산 (별점 없이 도서 수 기반)
     */
    private void calculateBasicStats(UserReadingPattern pattern, List<Book> books) {
        pattern.setTotalBooksRead(books.size());
        
        // 별점이 있는 도서들 필터링 (0보다 큰 별점)
        List<Book> ratedBooks = books.stream()
                .filter(book -> book.getRating() > 0)
                .collect(Collectors.toList());
        
        pattern.setTotalBooksRated(ratedBooks.size());
        
        if (!ratedBooks.isEmpty()) {
            // 평균 별점 계산
            double avgRating = ratedBooks.stream()
                    .mapToDouble(Book::getRating)
                    .average()
                    .orElse(0.0);
            pattern.setAverageRating(avgRating);
            
            // 4점 이상 고평점 도서 수 계산
            int highRatingCount = (int) ratedBooks.stream()
                    .filter(book -> book.getRating() >= 4.0)
                    .count();
            pattern.setHighRatingBooks(highRatingCount);
        }
        
        Log.d(TAG, "Basic stats - Books: " + books.size() + ", Rated: " + ratedBooks.size() + ", Avg Rating: " + pattern.getAverageRating());
    }
    
    /**
     * 장르 선호도 분석 (별점 없이 도서 수 기반)
     */
    private void analyzeGenrePreferences(UserReadingPattern pattern, List<Book> books) {
        Map<String, List<Book>> genreBooks = books.stream()
                .filter(book -> book.getCategory() != null && !book.getCategory().isEmpty())
                .collect(Collectors.groupingBy(Book::getCategory));
        
        Map<String, Double> genrePreferences = new HashMap<>();
        
        for (Map.Entry<String, List<Book>> entry : genreBooks.entrySet()) {
            String genre = entry.getKey();
            List<Book> genreBookList = entry.getValue();
            
            // 별점이 있으면 별점 기반, 없으면 도서 수 기반으로 선호도 계산
            double genreScore;
            List<Book> ratedGenreBooks = genreBookList.stream()
                    .filter(book -> book.getRating() > 0)
                    .collect(Collectors.toList());
            
            if (!ratedGenreBooks.isEmpty()) {
                // 별점이 있는 경우 평균 별점 사용
                genreScore = ratedGenreBooks.stream()
                        .mapToDouble(Book::getRating)
                        .average()
                        .orElse(3.0);
            } else {
                // 별점이 없는 경우 도서 수 기반 점수 (3.0 기본 + 도서수 보너스)
                genreScore = 3.0 + Math.min(2.0, genreBookList.size() * 0.3);
            }
            
            // 도서 수에 따른 가중치 적용
            double bookCountWeight = Math.min(1.0, genreBookList.size() / 3.0);
            double normalizedScore = (genreScore / 5.0) * bookCountWeight;
            
            genrePreferences.put(genre, normalizedScore);
        }
        
        pattern.setGenrePreferences(genrePreferences);
        Log.d(TAG, "Genre preferences calculated: " + genrePreferences.size() + " genres - " + genrePreferences);
    }
    
    /**
     * 작가 선호도 분석 (별점 없이 도서 수 기반)
     */
    private void analyzeAuthorPreferences(UserReadingPattern pattern, List<Book> books) {
        Map<String, List<Book>> authorBooks = books.stream()
                .filter(book -> book.getAuthor() != null && !book.getAuthor().isEmpty())
                .collect(Collectors.groupingBy(Book::getAuthor));
        
        Map<String, Double> authorPreferences = new HashMap<>();
        
        for (Map.Entry<String, List<Book>> entry : authorBooks.entrySet()) {
            String author = entry.getKey();
            List<Book> authorBookList = entry.getValue();
            
            // 별점이 있으면 별점 기반, 없으면 도서 수 기반으로 선호도 계산
            double authorScore;
            List<Book> ratedAuthorBooks = authorBookList.stream()
                    .filter(book -> book.getRating() > 0)
                    .collect(Collectors.toList());
            
            if (!ratedAuthorBooks.isEmpty()) {
                authorScore = ratedAuthorBooks.stream()
                        .mapToDouble(Book::getRating)
                        .average()
                        .orElse(3.0);
            } else {
                // 같은 작가의 책을 여러 권 읽었으면 선호도 높게
                authorScore = 3.0 + Math.min(2.0, authorBookList.size() * 0.5);
            }
            
            // 작가별 도서 수에 따른 가중치
            double bookCountWeight = Math.min(1.0, authorBookList.size() / 2.0);
            double normalizedScore = (authorScore / 5.0) * bookCountWeight;
            
            authorPreferences.put(author, normalizedScore);
        }
        
        pattern.setAuthorPreferences(authorPreferences);
        Log.d(TAG, "Author preferences calculated: " + authorPreferences.size() + " authors - " + authorPreferences);
    }
    
    /**
     * 출판사 선호도 분석 (별점 없이 도서 수 기반)
     */
    private void analyzePublisherPreferences(UserReadingPattern pattern, List<Book> books) {
        Map<String, List<Book>> publisherBooks = books.stream()
                .filter(book -> book.getPublisher() != null && !book.getPublisher().isEmpty())
                .collect(Collectors.groupingBy(Book::getPublisher));
        
        Map<String, Double> publisherPreferences = new HashMap<>();
        
        for (Map.Entry<String, List<Book>> entry : publisherBooks.entrySet()) {
            String publisher = entry.getKey();
            List<Book> publisherBookList = entry.getValue();
            
            double publisherScore;
            List<Book> ratedPublisherBooks = publisherBookList.stream()
                    .filter(book -> book.getRating() > 0)
                    .collect(Collectors.toList());
            
            if (!ratedPublisherBooks.isEmpty()) {
                publisherScore = ratedPublisherBooks.stream()
                        .mapToDouble(Book::getRating)
                        .average()
                        .orElse(3.0);
            } else {
                publisherScore = 3.0 + Math.min(1.5, publisherBookList.size() * 0.3);
            }
            
            double bookCountWeight = Math.min(1.0, publisherBookList.size() / 3.0);
            double normalizedScore = (publisherScore / 5.0) * bookCountWeight;
            
            publisherPreferences.put(publisher, normalizedScore);
        }
        
        pattern.setPublisherPreferences(publisherPreferences);
        Log.d(TAG, "Publisher preferences calculated: " + publisherPreferences.size() + " publishers - " + publisherPreferences);
    }
    
    /**
     * TOP 선호 정보 업데이트 (상위 N개 추출)
     */
    private void updateTopPreferences(UserReadingPattern pattern) {
        // TOP 3 선호 장르
        List<String> topGenres = pattern.getGenrePreferences().entrySet().stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        pattern.setFavoriteGenres(topGenres);
        
        // TOP 5 선호 작가
        List<String> topAuthors = pattern.getAuthorPreferences().entrySet().stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .limit(5)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        pattern.setFavoriteAuthors(topAuthors);
        
        // TOP 3 선호 출판사
        List<String> topPublishers = pattern.getPublisherPreferences().entrySet().stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        pattern.setFavoritePublishers(topPublishers);
        
        Log.d(TAG, "Top preferences - Genres: " + topGenres + ", Authors: " + topAuthors + ", Publishers: " + topPublishers);
    }
}
