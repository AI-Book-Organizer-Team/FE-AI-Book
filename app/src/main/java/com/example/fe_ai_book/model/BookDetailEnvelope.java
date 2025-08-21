package com.example.fe_ai_book.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BookDetailEnvelope {
    @SerializedName("response")
    public Inner response;

    public static class Inner {
        @SerializedName("error")   public String error;   // 에러 메시지 대응
        @SerializedName("request") public Request request;
        @SerializedName("detail")  public List<Detail> detail; // srchDtlList 엔드포인트용
        @SerializedName("docs")    public List<Doc> docs;     // srchBooks/loanItemSrch 엔드포인트용
        @SerializedName("loanInfo") public Object loanInfo;    // 당장 안 쓰면 임시 Object
        @SerializedName("numFound") public Integer numFound;   // 총 결과 개수
    }

    public static class Request {
        @SerializedName("isbn13")      public String isbn13;
        @SerializedName("loaninfoYN")  public String loaninfoYN;
        @SerializedName("displayInfo") public String displayInfo;
    }

    public static class Detail {
        @SerializedName("book") public Book book;
    }
    
    public static class Doc {
        @SerializedName("doc") public Book doc;
    }

    public static class Book {
        @SerializedName("bookname")         public String bookname;
        @SerializedName("authors")          public String authors;
        @SerializedName("publisher")        public String publisher;
        @SerializedName("publication_date") public String publication_date;
        @SerializedName("publication_year") public String publication_year;
        @SerializedName("isbn")             public String isbn;
        @SerializedName("isbn13")           public String isbn13;
        @SerializedName("addition_symbol")  public String addition_symbol;
        @SerializedName("vol")              public String vol;
        @SerializedName("class_no")         public String class_no;
        @SerializedName("class_nm")         public String class_nm;
        @SerializedName("description")      public String description;
        @SerializedName("bookImageURL")     public String bookImageURL;
        @SerializedName("bookDtlUrl")       public String bookDtlUrl;
        @SerializedName("loan_count")       public String loan_count;
        @SerializedName("ranking")          public String ranking;
        @SerializedName("no")               public String no;
    }
}
