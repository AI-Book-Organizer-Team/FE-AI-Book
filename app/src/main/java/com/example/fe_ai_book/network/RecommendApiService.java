package com.example.fe_ai_book.network;

import com.example.fe_ai_book.network.dto.RecommendRequest;
import com.example.fe_ai_book.network.dto.RecommendResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RecommendApiService {

    @POST("/api/recommend")
    Call<RecommendResponse> getRecommendations(@Body RecommendRequest request);
}

