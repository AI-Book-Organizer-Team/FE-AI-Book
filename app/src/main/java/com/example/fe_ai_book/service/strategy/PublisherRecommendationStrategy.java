package com.example.fe_ai_book.service.strategy;

import android.util.Log;

import com.example.fe_ai_book.model.Book;
import com.example.fe_ai_book.model.BookRecommendation;
import com.example.fe_ai_book.model.UserReadingPattern;
import com.example.fe_ai_book.service.FirebaseBookQueryService;
import com.example.fe_ai_book.service.FirebasePatternAnalysisService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PublisherRecommendationStrategy implements RecommendationStrategy {
    private static final String TAG = "PublisherRecStrategy";

    @Override
    public String getType() { return "publisher"; }

    @Override
    public void execute(String userId, UserReadingPattern pattern, FirebaseBookQueryService bookQueryService, FirebasePatternAnalysisService patternAnalysisService, Callback callback) {
        if (pattern.getFavoritePublishers() == null || pattern.getFavoritePublishers().isEmpty()) {
            callback.onResult(new ArrayList<>());
            return;
        }
        String raw = pattern.getFavoritePublishers().get(0);
        String key = TextNormalizer.normalizeForContains(raw);
        double score = pattern.getPublisherScore(raw);

        bookQueryService.searchBooksByPublisher(raw, new FirebaseBookQueryService.BookQueryCallback() {
            @Override public void onBooksLoaded(List<Book> books) {
                // 부분 일치 허용: 응답 목록에서 출판사명이 포함되는 항목 우선 선택
                List<Book> filtered = books.stream()
                        .filter(b -> TextNormalizer.normalizeForContains(b.getPublisher()).contains(key))
                        .limit(5)
                        .collect(Collectors.toList());
                if (filtered.isEmpty()) {
                    filtered = books.stream().limit(5).collect(Collectors.toList());
                }
                List<BookRecommendation> recs = new ArrayList<>();
                for (Book b : filtered) recs.add(RecommendationUtils.fromBook(userId, b, score, raw, getType()));
                callback.onResult(recs);
            }
            @Override public void onError(String error) {
                Log.w(TAG, "publisher search error: " + error);
                callback.onResult(new ArrayList<>());
            }
        });
    }
}
