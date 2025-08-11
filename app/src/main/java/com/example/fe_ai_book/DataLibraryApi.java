package com.example.fe_ai_book;

import com.example.fe_ai_book.model.BookDetailEnvelope;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DataLibraryApi {
    // 실제 파싱용: json을 어노테이션에 고정해 실수 방지
    @GET("srchDtlList?format=json")
    Call<BookDetailEnvelope> getBookDetail(
            @Query("authKey") String authKey,
            @Query("isbn13") String isbn13,
            @Query("loaninfoYN") String loaninfoYN,
            @Query("displayInfo") String displayInfo,
            @Query("format") String format
    );

    @GET("srchDtlList?format=json")
    Call<okhttp3.ResponseBody> debugBookDetail(
            @Query("authKey") String authKey,
            @Query("isbn13") String isbn13,
            @Query("loaninfoYN") String loaninfoYN,
            @Query("displayInfo") String displayInfo
    );
}
