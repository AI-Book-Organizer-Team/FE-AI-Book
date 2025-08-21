package com.example.fe_ai_book.service.strategy;

import com.example.fe_ai_book.model.BookRecommendation;
import com.example.fe_ai_book.service.FirebaseBookQueryService;
import com.example.fe_ai_book.service.FirebasePatternAnalysisService;
import com.example.fe_ai_book.model.UserReadingPattern;

import java.util.List;


public interface RecommendationStrategy {
    interface Callback {
        void onResult(List<BookRecommendation> recs);
        void onError(String error);
    }

    String getType();

    void execute(String userId,
                 UserReadingPattern pattern,
                 FirebaseBookQueryService bookQueryService,
                 FirebasePatternAnalysisService patternAnalysisService,
                 Callback callback);
}
