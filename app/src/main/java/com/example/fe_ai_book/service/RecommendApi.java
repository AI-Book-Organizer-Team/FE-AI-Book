package com.example.fe_ai_book.service;

import com.example.fe_ai_book.dto.RecommendRequest;
import com.example.fe_ai_book.dto.RecommendResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface RecommendApi {
    @Headers("Content-Type: application/json")
    @POST("/recommend")
    Call<RecommendResponse> getRecommendations(@Body RecommendRequest body);
}