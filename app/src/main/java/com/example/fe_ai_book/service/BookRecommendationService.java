package com.example.fe_ai_book.service;

import android.util.Log;

import com.example.fe_ai_book.dao.BookDao;
import com.example.fe_ai_book.entity.BookEntity;
import com.example.fe_ai_book.model.BookRecommendation;
import com.example.fe_ai_book.model.UserReadingPattern;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;



public class BookRecommendationService {
    private static final String TAG = "BookRecommendationService";
    
    private BookDao bookDao;
    private PatternAnalysisService patternAnalysisService;
    private ExecutorService executorService;
    
    public BookRecommendationService(BookDao bookDao, PatternAnalysisService patternAnalysisService) {
        this.bookDao = bookDao;
        this.patternAnalysisService = patternAnalysisService;
        this.executorService = Executors.newSingleThreadExecutor();
    }
    
    public interface RecommendationCallback {
        void onRecommendationsReady(List<BookRecommendation> recommendations);
        void onRecommendationError(String error);
    }
    
    /**
     * 사용자 패턴 기반 도서 추천 실행
     */
    public void generateRecommendations(String userId, RecommendationCallback callback) {
        executorService.execute(() -> {
            try {
                Log.d(TAG, "Generating recommendations for user: " + userId);
                
                // 1. 사용자 패턴 분석
                patternAnalysisService.analyzeUserPattern(userId, new PatternAnalysisService.PatternAnalysisCallback() {
                    @Override
                    public void onAnalysisComplete(UserReadingPattern pattern) {
                        try {
                            List<BookRecommendation> recommendations = new ArrayList<>();
                            
                            // 추천에 충분한 데이터가 있는지 확인
                            if (!pattern.hasEnoughDataForRecommendation()) {
                                Log.d(TAG, "Insufficient data for personalized recommendations, generating popular books");
                                generatePopularBookRecommendations(userId, recommendations);
                            } else {
                                // 2. 장르 기반 추천
                                generateGenreBasedRecommendations(userId, pattern, recommendations);
                                
                                // 3. 작가 기반 추천
                                generateAuthorBasedRecommendations(userId, pattern, recommendations);
                                
                                // 4. 출판사 기반 추천
                                generatePublisherBasedRecommendations(userId, pattern, recommendations);
                                
                                // 5. 협업 필터링 추천 (비슷한 취향 사용자 기반)
                                generateCollaborativeRecommendations(userId, pattern, recommendations);
                            }
                            
                            // 추천 결과 정렬 (신뢰도 높은 순)
                            recommendations.sort((r1, r2) -> Double.compare(r2.getConfidenceScore(), r1.getConfidenceScore()));
                            
                            // 최대 10개까지만 반환
                            List<BookRecommendation> finalRecommendations = recommendations.stream()
                                    .limit(10)
                                    .collect(Collectors.toList());
                            
                            Log.d(TAG, "Generated " + finalRecommendations.size() + " recommendations for user: " + userId);
                            callback.onRecommendationsReady(finalRecommendations);
                            
                        } catch (Exception e) {
                            Log.e(TAG, "Error generating recommendations", e);
                            callback.onRecommendationError("Failed to generate recommendations: " + e.getMessage());
                        }
                    }
                    
                    @Override
                    public void onAnalysisError(String error) {
                        Log.e(TAG, "Pattern analysis failed: " + error);
                        callback.onRecommendationError("Pattern analysis failed: " + error);
                    }
                });
                
            } catch (Exception e) {
                Log.e(TAG, "Error in recommendation process", e);
                callback.onRecommendationError("Recommendation process failed: " + e.getMessage());
            }
        });
    }
    
    /**
     * 장르 기반 추천 (컨텐츠 기반 필터링)
     */
    private void generateGenreBasedRecommendations(String userId, UserReadingPattern pattern, List<BookRecommendation> recommendations) {
        List<BookEntity> userBooks = bookDao.getAllBooks(); // 실제로는 사용자별 필터링 필요
        Set<String> userBookIds = userBooks.stream().map(BookEntity::getId).collect(Collectors.toSet());
        
        for (String favoriteGenre : pattern.getFavoriteGenres()) {
            List<BookEntity> genreBooks = bookDao.getBooksByCategory(favoriteGenre)
                    .stream()
                    .filter(book -> !userBookIds.contains(book.getId())) // 이미 읽은 책 제외
                    .filter(book -> book.getRating() == null || book.getRating() >= 3.5) // 낮은 평점 책 제외
                    .limit(3)
                    .collect(Collectors.toList());
            
            for (BookEntity book : genreBooks) {
                double score = pattern.getGenreScore(favoriteGenre) * 0.8; // 장르 기반은 0.8 가중치
                BookRecommendation recommendation = new BookRecommendation(
                        userId, book, score, favoriteGenre, "genre"
                );
                recommendations.add(recommendation);
            }
        }
        
        Log.d(TAG, "Generated genre-based recommendations: " + recommendations.size());
    }
    
    /**
     * 작가 기반 추천
     */
    private void generateAuthorBasedRecommendations(String userId, UserReadingPattern pattern, List<BookRecommendation> recommendations) {
        List<BookEntity> userBooks = bookDao.getAllBooks();
        Set<String> userBookIds = userBooks.stream().map(BookEntity::getId).collect(Collectors.toSet());
        
        for (String favoriteAuthor : pattern.getFavoriteAuthors()) {
            List<BookEntity> authorBooks = bookDao.searchBooksByAuthor(favoriteAuthor)
                    .stream()
                    .filter(book -> !userBookIds.contains(book.getId()))
                    .filter(book -> book.getRating() == null || book.getRating() >= 3.5)
                    .limit(2)
                    .collect(Collectors.toList());
            
            for (BookEntity book : authorBooks) {
                double score = pattern.getAuthorScore(favoriteAuthor) * 0.9; // 작가 기반은 0.9 가중치
                BookRecommendation recommendation = new BookRecommendation(
                        userId, book, score, favoriteAuthor, "author"
                );
                recommendations.add(recommendation);
            }
        }
        
        Log.d(TAG, "Generated author-based recommendations");
    }
    
    /**
     * 출판사 기반 추천
     */
    private void generatePublisherBasedRecommendations(String userId, UserReadingPattern pattern, List<BookRecommendation> recommendations) {
        List<BookEntity> userBooks = bookDao.getAllBooks();
        Set<String> userBookIds = userBooks.stream().map(BookEntity::getId).collect(Collectors.toSet());
        
        for (String favoritePublisher : pattern.getFavoritePublishers()) {
            List<BookEntity> publisherBooks = bookDao.getBooksByPublisher(favoritePublisher)
                    .stream()
                    .filter(book -> !userBookIds.contains(book.getId()))
                    .filter(book -> book.getRating() == null || book.getRating() >= 4.0) // 출판사 기반은 더 높은 기준
                    .limit(2)
                    .collect(Collectors.toList());
            
            for (BookEntity book : publisherBooks) {
                double score = pattern.getPublisherScore(favoritePublisher) * 0.6; // 출판사 기반은 0.6 가중치
                BookRecommendation recommendation = new BookRecommendation(
                        userId, book, score, favoritePublisher, "publisher"
                );
                recommendations.add(recommendation);
            }
        }
        
        Log.d(TAG, "Generated publisher-based recommendations");
    }
    
    /**
     * 협업 필터링 추천 (비슷한 취향의 사용자 기반)
     * 간단한 형태: 평점이 높은 인기 도서 추천
     */
    private void generateCollaborativeRecommendations(String userId, UserReadingPattern pattern, List<BookRecommendation> recommendations) {
        List<BookEntity> userBooks = bookDao.getAllBooks();
        Set<String> userBookIds = userBooks.stream().map(BookEntity::getId).collect(Collectors.toSet());
        
        // 평점 4.0 이상인 인기 도서들 추천
        List<BookEntity> popularBooks = bookDao.getBooksByRating(4.0)
                .stream()
                .filter(book -> !userBookIds.contains(book.getId()))
                .limit(3)
                .collect(Collectors.toList());
        
        for (BookEntity book : popularBooks) {
            double score = 0.7; // 협업 필터링 기본 점수
            // 사용자가 까다로운 독자라면 점수 조정
            if (pattern.isSelectiveReader()) {
                score = 0.8;
            }
            
            BookRecommendation recommendation = new BookRecommendation(
                    userId, book, score, "높은 평점", "collaborative"
            );
            recommendations.add(recommendation);
        }
        
        Log.d(TAG, "Generated collaborative filtering recommendations");
    }
    
    /**
     * 데이터가 부족한 신규 사용자를 위한 인기 도서 추천
     */
    private void generatePopularBookRecommendations(String userId, List<BookRecommendation> recommendations) {
        // 최근 추가된 도서 중 인기있는 것들 추천
        List<BookEntity> recentBooks = bookDao.getRecentBooks(10);
        
        for (BookEntity book : recentBooks) {
            double score = 0.5; // 신규 사용자용 기본 점수
            BookRecommendation recommendation = new BookRecommendation(
                    userId, book, score, "인기 도서", "popular"
            );
            recommendations.add(recommendation);
        }
        
        Log.d(TAG, "Generated popular book recommendations for new user");
    }
    
    /**
     * 추천 다양성 보장 (같은 작가/장르의 추천이 너무 많지 않도록)
     */
    private List<BookRecommendation> ensureDiversity(List<BookRecommendation> recommendations) {
        Map<String, Integer> authorCount = new HashMap<>();
        Map<String, Integer> genreCount = new HashMap<>();
        List<BookRecommendation> diverseRecommendations = new ArrayList<>();
        
        for (BookRecommendation rec : recommendations) {
            String author = rec.getRecommendedBook().getAuthor();
            String genre = rec.getRecommendedBook().getCategory();
            
            // 같은 작가는 최대 2권, 같은 장르는 최대 3권까지만
            if (authorCount.getOrDefault(author, 0) < 2 && genreCount.getOrDefault(genre, 0) < 3) {
                diverseRecommendations.add(rec);
                authorCount.put(author, authorCount.getOrDefault(author, 0) + 1);
                genreCount.put(genre, genreCount.getOrDefault(genre, 0) + 1);
            }
        }
        
        return diverseRecommendations;
    }
    
    /**
     * 리소스 정리
     */
    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
        if (patternAnalysisService != null) {
            patternAnalysisService.shutdown();
        }
    }
}
