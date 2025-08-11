package com.example.fe_ai_book.model;

import java.util.Date;

public class User {
    private String uid; // Firebase UID
    private String email;
    private String password;
    private String nickname;
    private String gender;
    private Date birthDate;
    private boolean emailVerified;
    private String verificationCode;

    // 기본 생성자
    public User() {}

    // 회원가입용 생성자
    public User(String email, String password, String nickname, String gender, Date birthDate) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.gender = gender;
        this.birthDate = birthDate;
        this.emailVerified = false;
    }

    // Getter 메서드들
    public String getUid() {
        return uid;
    }
    
    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getNickname() {
        return nickname;
    }

    public String getGender() {
        return gender;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    // Setter 메서드들
    public void setUid(String uid) {
        this.uid = uid;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", nickname='" + nickname + '\'' +
                ", gender='" + gender + '\'' +
                ", birthDate=" + birthDate +
                ", emailVerified=" + emailVerified +
                '}';
    }
}
