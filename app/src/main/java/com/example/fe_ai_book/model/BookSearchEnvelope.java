package com.example.fe_ai_book.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BookSearchEnvelope {
    @SerializedName("response") public Inner response;

    public static class Inner {
        public Request request;
        public int numFound;
        public List<DocItem> docs;
    }

    public static class Request {
        public String keyword;
        public String title;
        public int pageNo;
        public int pageSize;
    }

    public static class DocItem {
        @SerializedName("doc")
        public Book doc;
    }

    public static class Book {
        @SerializedName("bookname") public String bookname;
        @SerializedName("authors") public String authors;
        @SerializedName("publisher") public String publisher;
        @SerializedName("isbn13") public String isbn13;
        @SerializedName("class_no") public String class_no;
        @SerializedName("class_nm") public String class_nm;
        @SerializedName("publication_year") public String publication_year;
        @SerializedName("bookImageURL") public String bookImageURL;
    }
}
