package com.example.fe_ai_book.model;

public class User {
    private String uid;
    private String nickname;
    private String email;
    private long bookCount; // Firestore에서 number 타입으로 저장됨

    public User() {}

    public User(String uid, String nickname, String email, long bookCount) {
        this.uid = uid;
        this.nickname = nickname;
        this.email = email;
        this.bookCount = bookCount;
    }

    // Getter & Setter
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getBookCount() {
        return bookCount;
    }

    public void setBookCount(long bookCount) {
        this.bookCount = bookCount;
    }
}
