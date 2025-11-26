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
            @Query("displayInfo") String displayInfo
    );
    
    // 도서 검색 (제목/저자/출판사 등으로 검색)
    @GET("srchBooks?format=json")
    Call<BookDetailEnvelope> getSearchBooks(
            @Query("authKey") String authKey,
            @Query("title") String title,
            @Query("author") String author,
            @Query("publisher") String publisher,
            @Query("pageNo") int pageNo,
            @Query("pageSize") int pageSize,
            @Query("format") String format
    );
    
    // 인기도서 조회
    @GET("loanItemSrch?format=json")
    Call<BookDetailEnvelope> getPopularBooks(
            @Query("authKey") String authKey,
            @Query("startDt") String startDate,
            @Query("endDt") String endDate,
            @Query("pageNo") int pageNo,
            @Query("pageSize") int pageSize,
            @Query("format") String format
    );

    // 도서 제목, 키워드, 페이지 수 등 상세정보
    @GET("srchBooks?format=json")
    Call<BookSearchEnvelope> searchBooksByKeyword(
            @Query("authKey") String authKey,
            @Query("keyword") String keyword,
            @Query("searchType") String searchType, // all/title/author
            @Query("pageNo") int pageNo,
            @Query("pageSize") int pageSize
    );

    @GET("srchBooks?format=json")
    Call<BookSearchEnvelope> searchBooksByTitle(
            @Query("authKey") String authKey,
            @Query("title") String title,
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
