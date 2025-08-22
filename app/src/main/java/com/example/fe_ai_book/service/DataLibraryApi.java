package com.example.fe_ai_book.service;

import com.example.fe_ai_book.model.BookDetailEnvelope;
import com.example.fe_ai_book.model.BookSearchEnvelope;

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

    // 도서 제목, 키워드, 페이지 수 등 상세정보
    @GET("srchBooks?format=json")
    Call<BookSearchEnvelope> searchBooks(
            @Query("authKey") String authKey,
            @Query("keyword") String keyword,
            @Query("searchType") String searchType, // "title" / "author" / "all"
            @Query("pageNo") int pageNo,
            @Query("pageSize") int pageSize
    );


    // 디버깅용
    @GET("srchDtlList?format=json")
    Call<okhttp3.ResponseBody> debugBookDetail(
            @Query("authKey") String authKey,
            @Query("isbn13") String isbn13,
            @Query("loaninfoYN") String loaninfoYN,
            @Query("displayInfo") String displayInfo
    );
}
