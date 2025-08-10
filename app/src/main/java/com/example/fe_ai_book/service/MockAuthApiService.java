package com.example.fe_ai_book.service;

import android.os.Handler;
import android.os.Looper;

import com.example.fe_ai_book.model.User;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * 테스트용 목업 AuthApiService 클래스
 * 실제 서버 연결 없이 회원가입 기능을 테스트할 수 있습니다.
 */
public class MockAuthApiService extends AuthApiService {
    private static final String TAG = "MockAuthApiService";
    private Handler mainHandler;
    
    // 메모리에 저장된 가상의 데이터
    private Set<String> registeredEmails;
    private Set<String> usedNicknames;
    private Set<String> verifiedEmails;
    private Random random;

    public MockAuthApiService() {
        mainHandler = new Handler(Looper.getMainLooper());
        registeredEmails = new HashSet<>();
        usedNicknames = new HashSet<>();
        verifiedEmails = new HashSet<>();
        random = new Random();
        
        // 테스트용 더미 데이터
        usedNicknames.add("테스트유저");
        usedNicknames.add("관리자");
        usedNicknames.add("admin");
        registeredEmails.add("test@example.com");
    }

    @Override
    public void sendEmailVerification(String email, EmailVerificationCallback callback) {
        // 1-2초 지연 시뮬레이션
        mainHandler.postDelayed(() -> {
            if (registeredEmails.contains(email)) {
                callback.onFailure("이미 사용 중인 이메일입니다.");
            } else {
                // 인증번호 발송 성공 시뮬레이션
                callback.onSuccess();
            }
        }, 1000 + random.nextInt(1000));
    }

    @Override
    public void verifyEmail(String email, String verificationCode, EmailVerificationCallback callback) {
        // 1-2초 지연 시뮬레이션
        mainHandler.postDelayed(() -> {
            // 테스트용으로 "123456" 또는 "000000"을 유효한 인증번호로 간주
            if ("123456".equals(verificationCode) || "000000".equals(verificationCode)) {
                verifiedEmails.add(email);
                callback.onSuccess();
            } else {
                callback.onFailure("인증번호가 올바르지 않습니다. (테스트용: 123456 또는 000000)");
            }
        }, 1000 + random.nextInt(1000));
    }

    @Override
    public void checkNicknameDuplicate(String nickname, ApiCallback<Boolean> callback) {
        // 0.5-1초 지연 시뮬레이션
        mainHandler.postDelayed(() -> {
            boolean isAvailable = !usedNicknames.contains(nickname);
            callback.onSuccess(isAvailable);
        }, 500 + random.nextInt(500));
    }

    @Override
    public void signUp(User user, SignUpCallback callback) {
        // 1-3초 지연 시뮬레이션
        mainHandler.postDelayed(() -> {
            // 이메일 인증 확인
            if (!verifiedEmails.contains(user.getEmail())) {
                callback.onFailure("이메일 인증이 완료되지 않았습니다.");
                return;
            }
            
            // 이미 가입된 이메일 확인
            if (registeredEmails.contains(user.getEmail())) {
                callback.onFailure("이미 사용 중인 이메일입니다.");
                return;
            }
            
            // 닉네임 중복 확인
            if (usedNicknames.contains(user.getNickname())) {
                callback.onFailure("이미 사용 중인 닉네임입니다.");
                return;
            }
            
            // 회원가입 성공 시뮬레이션
            registeredEmails.add(user.getEmail());
            usedNicknames.add(user.getNickname());
            
            // 새 사용자 객체 생성 (비밀번호는 제외하고 반환)
            User newUser = new User();
            newUser.setEmail(user.getEmail());
            newUser.setNickname(user.getNickname());
            newUser.setGender(user.getGender());
            newUser.setBirthDate(user.getBirthDate());
            newUser.setEmailVerified(true);
            
            callback.onSuccess(newUser);
        }, 1000 + random.nextInt(2000));
    }

    @Override
    public void login(String email, String password, SignUpCallback callback) {
        // 1-2초 지연 시뮬레이션
        mainHandler.postDelayed(() -> {
            if (registeredEmails.contains(email)) {
                // 테스트용으로 모든 비밀번호를 유효한 것으로 간주
                User user = new User();
                user.setEmail(email);
                user.setNickname("테스트사용자");
                user.setEmailVerified(true);
                
                callback.onSuccess(user);
            } else {
                callback.onFailure("등록되지 않은 이메일이거나 비밀번호가 올바르지 않습니다.");
            }
        }, 1000 + random.nextInt(1000));
    }
}
