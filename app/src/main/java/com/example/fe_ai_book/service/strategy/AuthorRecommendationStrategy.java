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

public class AuthorRecommendationStrategy implements RecommendationStrategy {
    private static final String TAG = "AuthorRecStrategy";

    @Override
    public String getType() { return "author"; }

    @Override
    public void execute(String userId, UserReadingPattern pattern, FirebaseBookQueryService bookQueryService, FirebasePatternAnalysisService patternAnalysisService, Callback callback) {
        if (pattern.getFavoriteAuthors() == null || pattern.getFavoriteAuthors().isEmpty()) {
            callback.onResult(new ArrayList<>());
            return;
        }
        String raw = pattern.getFavoriteAuthors().get(0);
        String primary = TextNormalizer.extractPrimaryName(raw);
        double baseScore = pattern.getAuthorScore(raw);

        // 1차: 정규화된 주요 이름으로 검색
        bookQueryService.searchBooksByAuthor(primary, new FirebaseBookQueryService.BookQueryCallback() {
            @Override public void onBooksLoaded(List<Book> books) {
                List<Book> filtered = books.stream().limit(5).collect(Collectors.toList());
                if (!filtered.isEmpty()) {
                    List<BookRecommendation> recs = new ArrayList<>();
                    for (Book b : filtered) recs.add(RecommendationUtils.fromBook(userId, b, baseScore, primary, getType()));
                    callback.onResult(recs);
                    return;
                }
                // 2차: 보조 토큰들로 재검색 시도
                List<String> tokens = TextNormalizer.extractNameTokens(raw);
                if (tokens.size() > 1) {
                    tryNextToken(tokens, 1);
                } else {
                    callback.onResult(new ArrayList<>());
                }
            }
            @Override public void onError(String error) {
                Log.w(TAG, "author search error: " + error);
                callback.onResult(new ArrayList<>());
            }

            private void tryNextToken(List<String> tokens, int idx) {
                if (idx >= tokens.size()) { callback.onResult(new ArrayList<>()); return; }
                String token = tokens.get(idx);
                bookQueryService.searchBooksByAuthor(token, new FirebaseBookQueryService.BookQueryCallback() {
                    @Override public void onBooksLoaded(List<Book> books) {
                        List<Book> filtered = books.stream().limit(5).collect(Collectors.toList());
                        if (!filtered.isEmpty()) {
                            List<BookRecommendation> recs = new ArrayList<>();
                            for (Book b : filtered) recs.add(RecommendationUtils.fromBook(userId, b, baseScore * 0.9, token, getType()));
                            callback.onResult(recs);
                        } else {
                            tryNextToken(tokens, idx + 1);
                        }
                    }
                    @Override public void onError(String error) {
                        Log.w(TAG, "author token search error: " + error);
                        tryNextToken(tokens, idx + 1);
                    }
                });
            }
        });
    }
}
