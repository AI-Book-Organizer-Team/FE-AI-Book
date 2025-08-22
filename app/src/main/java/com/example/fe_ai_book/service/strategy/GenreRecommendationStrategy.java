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

public class GenreRecommendationStrategy implements RecommendationStrategy {
    private static final String TAG = "GenreRecStrategy";

    @Override
    public String getType() { return "genre"; }

    @Override
    public void execute(String userId, UserReadingPattern pattern, FirebaseBookQueryService bookQueryService, FirebasePatternAnalysisService patternAnalysisService, Callback callback) {
        if (pattern.getFavoriteGenres() == null || pattern.getFavoriteGenres().isEmpty()) {
            callback.onResult(new ArrayList<>());
            return;
        }
        String favorite = pattern.getFavoriteGenres().get(0);
        // 장르명으로 직접 검색하여 결과 확보
        bookQueryService.searchBooksByGenre(favorite, new FirebaseBookQueryService.BookQueryCallback() {
            @Override public void onBooksLoaded(List<Book> books) {
                List<Book> filtered = books.stream()
                        .limit(5)
                        .collect(Collectors.toList());
                List<BookRecommendation> recs = new ArrayList<>();
                double score = pattern.getGenreScore(favorite);
                for (Book b : filtered) {
                    recs.add(RecommendationUtils.fromBook(userId, b, score, favorite, getType()));
                }
                // 장르 검색 결과가 비었으면 인기 도서에서 장르 매칭 시도
                if (recs.isEmpty()) {
                    bookQueryService.getPopularBooks(new FirebaseBookQueryService.BookQueryCallback() {
                        @Override public void onBooksLoaded(List<Book> pop) {
                            List<BookRecommendation> popRecs = pop.stream()
                                    .filter(b -> favorite.equals(b.getCategory()))
                                    .limit(5)
                                    .map(b -> RecommendationUtils.fromBook(userId, b, score, favorite, getType()))
                                    .collect(Collectors.toList());
                            callback.onResult(popRecs);
                        }
                        @Override public void onError(String error) {
                            Log.w(TAG, "popular fallback error: " + error);
                            callback.onResult(new ArrayList<>());
                        }
                    });
                } else {
                    callback.onResult(recs);
                }
            }
            @Override public void onError(String error) {
                Log.w(TAG, "genre search error: " + error);
                callback.onResult(new ArrayList<>());
            }
        });
    }
}
