package com.example.fe_ai_book.service;

import com.example.fe_ai_book.model.User;

/**
 * 인증 서비스 인터페이스
 */
public interface AuthApiService {
    
    // 콜백 인터페이스들
    interface ApiCallback<T> {
        void onSuccess(T result);
        void onFailure(String errorMessage);
    }

    interface EmailVerificationCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    interface SignUpCallback {
        void onSuccess(User user);
        void onFailure(String errorMessage);
    }
    
    interface SignInCallback {
        void onSuccess(User user);
        void onFailure(String errorMessage);
    }

    // 매소드 선언
    void sendEmailVerification(String email, EmailVerificationCallback callback);
    void verifyEmail(String email, String verificationCode, EmailVerificationCallback callback);
    void checkNicknameDuplicate(String nickname, ApiCallback<Boolean> callback);
    void signUp(User user, SignUpCallback callback);
    void signIn(String email, String password, SignInCallback callback);
}
