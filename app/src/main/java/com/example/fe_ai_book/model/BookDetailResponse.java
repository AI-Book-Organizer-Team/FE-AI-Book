package com.example.fe_ai_book.model;

import java.util.List;

public class BookDetailResponse {
    public Detail detail;
    public BookDetailResponse response;

    public static class Detail {
        public Book book;
        public LoanInfo loanInfo;
    }
    public static class Book {
        public String bookname;
        public String authors;
        public String publisher;
        public String publication_year;
        public String isbn13;
        public String class_no;
        public String class_nm;
        public String description;
        public String bookImageURL;
    }
    public static class LoanInfo {
        public LoanGroup Total;
        public List<RegionItem> regionResult;
        public List<AgeItem> ageResult;
        public List<GenderItem> genderResult;
    }
    public static class LoanGroup { public Integer ranking; public String name; public Integer loanCnt; }
    public static class RegionItem { public String region; public Integer ranking; public String name; public Integer loanCnt; }
    public static class AgeItem { public String age; public Integer ranking; public String name; public Integer loanCnt; }
    public static class GenderItem { public String gender; public Integer ranking; public String name; public Integer loanCnt; }
}