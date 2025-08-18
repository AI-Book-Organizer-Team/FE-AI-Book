package com.example.fe_ai_book.service;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class ApiClient {
    private static final String BASE_URL = "http://data4library.kr/api/";

    private static final OkHttpClient OK_HTTP = new OkHttpClient.Builder()
            .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)).build();

    private static final Retrofit RETROFIT = new Retrofit.Builder().baseUrl(BASE_URL).client(OK_HTTP).addConverterFactory(GsonConverterFactory.create()).build();

    public static DataLibraryApi get() {
        return RETROFIT.create(DataLibraryApi.class);
    }
}
