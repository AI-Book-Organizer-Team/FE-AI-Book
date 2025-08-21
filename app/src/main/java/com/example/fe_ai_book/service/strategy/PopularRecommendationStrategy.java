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

public class PopularRecommendationStrategy implements RecommendationStrategy {
    private static final String TAG = "PopularRecStrategy";

    @Override
    public String getType() { return "popular"; }

    @Override
    public void execute(String userId, UserReadingPattern pattern, FirebaseBookQueryService bookQueryService, FirebasePatternAnalysisService patternAnalysisService, Callback callback) {
        bookQueryService.getPopularBooks(new FirebaseBookQueryService.BookQueryCallback() {
            @Override public void onBooksLoaded(List<com.example.fe_ai_book.model.Book> books) {
                List<Book> limited = books.stream().limit(5).collect(Collectors.toList());
                List<BookRecommendation> recs = new ArrayList<>();
                for (Book b : limited) recs.add(RecommendationUtils.fromBook(userId, b, 0.5, "인기 도서", getType()));
                callback.onResult(recs);
            }
            @Override public void onError(String error) {
                Log.w(TAG, "popular fetch error: " + error);
                callback.onResult(new ArrayList<>());
            }
        });
    }
}
