package com.example.fe_ai_book.model;

import com.google.firebase.Timestamp;

public class UserBook {

    private String userId;      // 저장한 사용자
    private String isbn;        // Books 컬렉션의 문서 ID
    private String memo;        // 사용자 메모
    private String tags;        // 사용자 태그 (#예시)
    private String location;    // 책 보관 위치
    private boolean bookmark;   // ⭐ 책갈피 여부
    private Timestamp dateSaved;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public UserBook() {}

    public UserBook(String userId, String isbn, String memo,
                    String tags, String location, boolean bookmark) {

        this.userId = userId;
        this.isbn = isbn;
        this.memo = memo;
        this.tags = tags;
        this.location = location;
        this.bookmark = bookmark;

        this.dateSaved = Timestamp.now();
        this.createdAt = Timestamp.now();
        this.updatedAt = Timestamp.now();
    }

    // Getters
    public String getUserId() { return userId; }
    public String getIsbn() { return isbn; }
    public String getMemo() { return memo; }
    public String getTags() { return tags; }
    public String getLocation() { return location; }
    public boolean isBookmark() { return bookmark; }
    public Timestamp getDateSaved() { return dateSaved; }
    public Timestamp getCreatedAt() { return createdAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }

    // Setters
    public void setUserId(String userId) { this.userId = userId; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public void setMemo(String memo) { this.memo = memo; }
    public void setTags(String tags) { this.tags = tags; }
    public void setLocation(String location) { this.location = location; }
    public void setBookmark(boolean bookmark) { this.bookmark = bookmark; }
    public void setDateSaved(Timestamp dateSaved) { this.dateSaved = dateSaved; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}
