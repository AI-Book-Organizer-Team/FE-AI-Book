package com.example.fe_ai_book.service;

import android.util.Log;

import com.example.fe_ai_book.dao.BookDao;
import com.example.fe_ai_book.dao.UserActivityDao;
import com.example.fe_ai_book.entity.BookEntity;
import com.example.fe_ai_book.entity.UserActivity;
import com.example.fe_ai_book.model.UserReadingPattern;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;



public class PatternAnalysisService {
    private static final String TAG = "PatternAnalysisService";
    
    private BookDao bookDao;
    private UserActivityDao userActivityDao;
    private ExecutorService executorService;
    
    public PatternAnalysisService(BookDao bookDao, UserActivityDao userActivityDao) {
        this.bookDao = bookDao;
        this.userActivityDao = userActivityDao;
        this.executorService = Executors.newSingleThreadExecutor();
    }
    
    public interface PatternAnalysisCallback {
        void onAnalysisComplete(UserReadingPattern pattern);
        void onAnalysisError(String error);
    }
    
    /**
     * 사용자 독서 패턴 분석 실행
     */
    public void analyzeUserPattern(String userId, PatternAnalysisCallback callback) {
        executorService.execute(() -> {
            try {
                Log.d(TAG, "Starting pattern analysis for user: " + userId);
                
                UserReadingPattern pattern = new UserReadingPattern(userId);
                
                // 1. 사용자의 모든 도서 데이터 조회
                List<BookEntity> userBooks = bookDao.getAllBooks(); // 실제로는 사용자별 필터링 필요
                List<UserActivity> userActivities = userActivityDao.getUserActivities(userId);
                
                if (userBooks.isEmpty()) {
                    Log.d(TAG, "No books found for user: " + userId);
                    callback.onAnalysisComplete(pattern);
                    return;
                }
                
                // 2. 기본 통계 계산
                calculateBasicStats(pattern, userBooks, userActivities);
                
                // 3. 장르 선호도 분석
                analyzeGenrePreferences(pattern, userBooks);
                
                // 4. 작가 선호도 분석
                analyzeAuthorPreferences(pattern, userBooks);
                
                // 5. 출판사 선호도 분석
                analyzePublisherPreferences(pattern, userBooks);
                
                // 6. TOP 선호 정보 업데이트
                updateTopPreferences(pattern);
                
                // 7. 분석 완료 시간 업데이트
                pattern.updateAnalysisTime();
                
                Log.d(TAG, "Pattern analysis completed for user: " + userId);
                Log.d(TAG, "Analysis result: " + pattern.toString());
                
                callback.onAnalysisComplete(pattern);
                
            } catch (Exception e) {
                Log.e(TAG, "Error analyzing user pattern", e);
                callback.onAnalysisError("Analysis failed: " + e.getMessage());
            }
        });
    }
    
    /**
     * 기본 통계 계산 (총 도서 수, 평균 별점 등)
     */
    private void calculateBasicStats(UserReadingPattern pattern, List<BookEntity> books, List<UserActivity> activities) {
        pattern.setTotalBooksRead(books.size());
        
        // 별점이 있는 도서들 필터링
        List<BookEntity> ratedBooks = books.stream()
                .filter(book -> book.getRating() != null && book.getRating() > 0)
                .collect(Collectors.toList());
        
        pattern.setTotalBooksRated(ratedBooks.size());
        
        if (!ratedBooks.isEmpty()) {
            // 평균 별점 계산
            double avgRating = ratedBooks.stream()
                    .mapToDouble(BookEntity::getRating)
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
     * 장르 선호도 분석
     */
    private void analyzeGenrePreferences(UserReadingPattern pattern, List<BookEntity> books) {
        Map<String, List<BookEntity>> genreBooks = books.stream()
                .filter(book -> book.getCategory() != null && !book.getCategory().isEmpty())
                .collect(Collectors.groupingBy(BookEntity::getCategory));
        
        Map<String, Double> genrePreferences = new HashMap<>();
        
        for (Map.Entry<String, List<BookEntity>> entry : genreBooks.entrySet()) {
            String genre = entry.getKey();
            List<BookEntity> genreBookList = entry.getValue();
            
            // 해당 장르 도서들의 평균 별점 계산
            double genreScore = genreBookList.stream()
                    .filter(book -> book.getRating() != null && book.getRating() > 0)
                    .mapToDouble(BookEntity::getRating)
                    .average()
                    .orElse(3.0); // 별점이 없으면 중간값 3.0
            
            // 도서 수에 따른 가중치 적용 (더 많이 읽은 장르에 높은 신뢰도)
            double bookCountWeight = Math.min(1.0, genreBookList.size() / 5.0);
            double normalizedScore = (genreScore / 5.0) * bookCountWeight;
            
            genrePreferences.put(genre, normalizedScore);
        }
        
        pattern.setGenrePreferences(genrePreferences);
        Log.d(TAG, "Genre preferences calculated: " + genrePreferences.size() + " genres");
    }
    
    /**
     * 작가 선호도 분석
     */
    private void analyzeAuthorPreferences(UserReadingPattern pattern, List<BookEntity> books) {
        Map<String, List<BookEntity>> authorBooks = books.stream()
                .filter(book -> book.getAuthor() != null && !book.getAuthor().isEmpty())
                .collect(Collectors.groupingBy(BookEntity::getAuthor));
        
        Map<String, Double> authorPreferences = new HashMap<>();
        
        for (Map.Entry<String, List<BookEntity>> entry : authorBooks.entrySet()) {
            String author = entry.getKey();
            List<BookEntity> authorBookList = entry.getValue();
            
            // 해당 작가 도서들의 평균 별점 계산
            double authorScore = authorBookList.stream()
                    .filter(book -> book.getRating() != null && book.getRating() > 0)
                    .mapToDouble(BookEntity::getRating)
                    .average()
                    .orElse(3.0);
            
            // 작가별 도서 수에 따른 가중치 (같은 작가의 책을 여러 권 읽었으면 선호도 증가)
            double bookCountWeight = Math.min(1.0, authorBookList.size() / 3.0);
            double normalizedScore = (authorScore / 5.0) * bookCountWeight;
            
            authorPreferences.put(author, normalizedScore);
        }
        
        pattern.setAuthorPreferences(authorPreferences);
        Log.d(TAG, "Author preferences calculated: " + authorPreferences.size() + " authors");
    }
    
    /**
     * 출판사 선호도 분석
     */
    private void analyzePublisherPreferences(UserReadingPattern pattern, List<BookEntity> books) {
        Map<String, List<BookEntity>> publisherBooks = books.stream()
                .filter(book -> book.getPublisher() != null && !book.getPublisher().isEmpty())
                .collect(Collectors.groupingBy(BookEntity::getPublisher));
        
        Map<String, Double> publisherPreferences = new HashMap<>();
        
        for (Map.Entry<String, List<BookEntity>> entry : publisherBooks.entrySet()) {
            String publisher = entry.getKey();
            List<BookEntity> publisherBookList = entry.getValue();
            
            double publisherScore = publisherBookList.stream()
                    .filter(book -> book.getRating() != null && book.getRating() > 0)
                    .mapToDouble(BookEntity::getRating)
                    .average()
                    .orElse(3.0);
            
            double bookCountWeight = Math.min(1.0, publisherBookList.size() / 4.0);
            double normalizedScore = (publisherScore / 5.0) * bookCountWeight;
            
            publisherPreferences.put(publisher, normalizedScore);
        }
        
        pattern.setPublisherPreferences(publisherPreferences);
        Log.d(TAG, "Publisher preferences calculated: " + publisherPreferences.size() + " publishers");
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
    
    /**
     * 리소스 정리
     */
    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
