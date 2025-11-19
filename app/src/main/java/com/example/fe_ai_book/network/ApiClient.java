package com.example.fe_ai_book.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static Retrofit retrofit = null;

    // 예: http://10.0.2.2:5000  (에뮬레이터에서 로컬 Flask 접속)
    //     또는 배포된 서버 주소
    private static final String BASE_URL = "http://10.0.2.2:5000";

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static RecommendApiService getRecommendApiService() {
        return getClient().create(RecommendApiService.class);
    }
}

