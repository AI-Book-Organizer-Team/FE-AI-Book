package com.example.fe_ai_book.service;

import android.util.Log;

import com.example.fe_ai_book.model.BookRecommendation;
import com.example.fe_ai_book.model.UserReadingPattern;
import com.example.fe_ai_book.service.strategy.AuthorRecommendationStrategy;
import com.example.fe_ai_book.service.strategy.GenreRecommendationStrategy;
import com.example.fe_ai_book.service.strategy.PopularRecommendationStrategy;
import com.example.fe_ai_book.service.strategy.PublisherRecommendationStrategy;
import com.example.fe_ai_book.service.strategy.RecommendationStrategy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Firebase 기반 도서 추천 서비스
 * Firestore 데이터를 기반으로 사용자 맞춤 추천 생성
 */
public class FirebaseBookRecommendationService {
    private static final String TAG = "FirebaseBookRecommendation";

    private final FirebaseBookQueryService bookQueryService;
    private final FirebasePatternAnalysisService patternAnalysisService;

    public FirebaseBookRecommendationService() {
        this.bookQueryService = new FirebaseBookQueryService();
        this.patternAnalysisService = new FirebasePatternAnalysisService();
    }

    public interface RecommendationCallback {
        void onRecommendationsReady(List<BookRecommendation> recommendations);
        void onRecommendationError(String error);
    }

    public void generateRecommendations(String userId, RecommendationCallback callback) {
        Log.d(TAG, "Generating recommendations (strategy-based) for user: " + userId);
        patternAnalysisService.analyzeUserPattern(userId, new FirebasePatternAnalysisService.PatternAnalysisCallback() {
            @Override public void onAnalysisComplete(UserReadingPattern pattern) {
                try {
                    if (!pattern.hasEnoughDataForRecommendation()) {
                        Log.d(TAG, "Data insufficient, returning popular strategy only");
                        runStrategies(userId, pattern, buildMinimalStrategies(), callback);
                    } else {
                        runStrategies(userId, pattern, buildFullStrategies(), callback);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Strategy pipeline error", e);
                    callback.onRecommendationError("Strategy error: " + e.getMessage());
                }
            }
            @Override public void onAnalysisError(String error) {
                Log.e(TAG, "Pattern analysis failed: " + error);
                callback.onRecommendationError("Pattern analysis failed: " + error);
            }
        });
    }

    private List<RecommendationStrategy> buildFullStrategies() {
        List<RecommendationStrategy> list = new ArrayList<>();
        list.add(new GenreRecommendationStrategy());
        list.add(new AuthorRecommendationStrategy());
        list.add(new PublisherRecommendationStrategy());
        list.add(new PopularRecommendationStrategy());
        return list;
    }

    private List<RecommendationStrategy> buildMinimalStrategies() {
        List<RecommendationStrategy> list = new ArrayList<>();
        list.add(new PopularRecommendationStrategy());
        return list;
    }

    private void runStrategies(String userId,
                               UserReadingPattern pattern,
                               List<RecommendationStrategy> strategies,
                               RecommendationCallback callback) {
        List<BookRecommendation> all = new ArrayList<>();
        final int total = strategies.size();
        final int[] done = {0};

        for (RecommendationStrategy s : strategies) {
            s.execute(userId, pattern, bookQueryService, patternAnalysisService, new RecommendationStrategy.Callback() {
                @Override public void onResult(List<BookRecommendation> recs) {
                    synchronized (all) {
                        all.addAll(recs);
                        done[0]++;
                        if (done[0] == total) finalizeAndReturn(all, callback);
                    }
                }
                @Override public void onError(String error) {
                    Log.w(TAG, "Strategy " + s.getType() + " error: " + error);
                    synchronized (all) {
                        done[0]++;
                        if (done[0] == total) finalizeAndReturn(all, callback);
                    }
                }
            });
        }
    }

    private void finalizeAndReturn(List<BookRecommendation> recs, RecommendationCallback callback) {
        // 중복 제거: ISBN 기준
        Set<String> seen = new HashSet<>();
        List<BookRecommendation> deduped = new ArrayList<>();
        for (BookRecommendation r : recs) {
            String key = r.getRecommendedBook() != null ? r.getRecommendedBook().getIsbn() : r.getRecommendationReason() + r.getRecommendationType();
            if (key == null) key = r.getRecommendationType() + ":" + r.getRecommendationReason();
            if (seen.add(key)) deduped.add(r);
        }
        // 타입별 카운트 로깅 (디버그용)
        java.util.Map<String, Integer> typeCount = new java.util.HashMap<>();
        for (BookRecommendation r : deduped) {
            String t = r.getRecommendationType() == null ? "unknown" : r.getRecommendationType();
            typeCount.put(t, typeCount.getOrDefault(t, 0) + 1);
        }
        Log.d(TAG, "Final recommendations: total=" + deduped.size() + ", byType=" + typeCount);
        callback.onRecommendationsReady(deduped);
    }
}
