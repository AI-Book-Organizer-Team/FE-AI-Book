package com.example.fe_ai_book.service;

import android.util.Log;

import com.example.fe_ai_book.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;

import java.util.HashMap;
import java.util.Map;

/**
 * Firebase Authentication을 사용한 실제 인증 서비스
 */
public class FirebaseAuthService implements AuthApiService {
    
    private static final String TAG = "FirebaseAuthService";
    
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseFunctions mFunctions;
    
    public FirebaseAuthService() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mFunctions = FirebaseFunctions.getInstance();
    }

    @Override
    public void sendEmailVerification(String email, EmailVerificationCallback callback) {
        // Firebase Functions를 통한 실제 이메일 인증번호 전송
        if (!isValidEmail(email)) {
            callback.onFailure("올바른 이메일 형식이 아닙니다.");
            return;
        }
        
        // Functions 호출을 위한 데이터 준비
        Map<String, Object> data = new HashMap<>();
        data.put("email", email);
        
        // sendEmailVerification 함수 호출
        mFunctions.getHttpsCallable("sendEmailVerification")
            .call(data)
            .addOnSuccessListener(httpsCallableResult -> {
                // 성공 응답 처리
                @SuppressWarnings("unchecked")
                Map<String, Object> result = (Map<String, Object>) httpsCallableResult.getData();
                if (result != null && Boolean.TRUE.equals(result.get("success"))) {
                    callback.onSuccess();
                } else {
                    String message = result != null ? (String) result.get("message") : "알 수 없는 오류가 발생했습니다.";
                    callback.onFailure(message);
                }
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "이메일 인증번호 전송 실패", e);
                callback.onFailure("이메일 전송에 실패했습니다: " + e.getMessage());
            });
    }

    @Override
    public void verifyEmail(String email, String code, EmailVerificationCallback callback) {
        // Firebase Functions를 통한 실제 인증번호 검증
        if (email == null || code == null) {
            callback.onFailure("이메일과 인증번호가 필요합니다.");
            return;
        }
        
        // Functions 호출을 위한 데이터 준비
        Map<String, Object> data = new HashMap<>();
        data.put("email", email);
        data.put("code", code);
        
        // verifyEmailCode 함수 호출
        mFunctions.getHttpsCallable("verifyEmailCode")
            .call(data)
            .addOnSuccessListener(httpsCallableResult -> {
                // 성공 응답 처리
                @SuppressWarnings("unchecked")
                Map<String, Object> result = (Map<String, Object>) httpsCallableResult.getData();
                if (result != null && Boolean.TRUE.equals(result.get("success"))) {
                    callback.onSuccess();
                } else {
                    String message = result != null ? (String) result.get("message") : "인증번호 확인에 실패했습니다.";
                    callback.onFailure(message);
                }
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "인증번호 확인 실패", e);
                callback.onFailure("인증번호 확인 중 오류가 발생했습니다: " + e.getMessage());
            });
    }

    @Override
    public void checkNicknameDuplicate(String nickname, ApiCallback<Boolean> callback) {
        // Firestore에서 닉네임 중복 확인
        db.collection("users")
                .whereEqualTo("nickname", nickname)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    boolean isAvailable = queryDocumentSnapshots.isEmpty();
                    callback.onSuccess(isAvailable);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "닉네임 중복 확인 실패", e);
                    callback.onFailure("닉네임 중복 확인 중 오류가 발생했습니다.");
                });
    }

    @Override
    public void signUp(User user, SignUpCallback callback) {
        // Firebase Auth로 이메일/비밀번호 계정 생성
        mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnSuccessListener(authResult -> {
                    // 회원가입 성공 시 사용자 정보를 Firestore에 저장
                    String uid = authResult.getUser().getUid();
                    saveUserToFirestore(uid, user, callback);
                })
                .addOnFailureListener(e -> {
                    String errorMessage = getAuthErrorMessage(e);
                    Log.e(TAG, "회원가입 실패", e);
                    callback.onFailure(errorMessage);
                });
    }
    
    /**
     * 사용자 정보를 Firestore에 저장
     */
    private void saveUserToFirestore(String uid, User user, SignUpCallback callback) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("uid", uid);
        userData.put("email", user.getEmail());
        userData.put("nickname", user.getNickname());
        userData.put("gender", user.getGender());
        userData.put("birthDate", user.getBirthDate());
        userData.put("createdAt", System.currentTimeMillis());
        
        db.collection("users").document(uid)
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "사용자 정보 저장 성공");
                    user.setUid(uid); // User 객체에 UID 설정
                    callback.onSuccess(user);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "사용자 정보 저장 실패", e);
                    // 인증은 성공했지만 사용자 정보 저장에 실패한 경우
                    // 생성된 계정을 삭제할 수도 있습니다
                    callback.onFailure("사용자 정보 저장 중 오류가 발생했습니다.");
                });
    }
    
    /**
     * Firebase Auth 오류 메시지를 한국어로 변환
     */
    private String getAuthErrorMessage(Exception e) {
        if (e instanceof FirebaseAuthWeakPasswordException) {
            return "비밀번호가 너무 약합니다. 6자 이상으로 설정해주세요.";
        } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
            return "올바르지 않은 이메일 형식입니다.";
        } else if (e instanceof FirebaseAuthUserCollisionException) {
            return "이미 존재하는 이메일입니다.";
        } else {
            return "회원가입 중 오류가 발생했습니다: " + e.getMessage();
        }
    }
    
    /**
     * 이메일 형식 검증
     */
    private boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }

    @Override
    public void signIn(String email, String password, SignInCallback callback) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String uid = authResult.getUser().getUid();
                    // Firestore에서 사용자 정보 가져오기
                    getUserFromFirestore(uid, callback);
                })
                .addOnFailureListener(e -> {
                    String errorMessage = getSignInErrorMessage(e);
                    Log.e(TAG, "로그인 실패", e);
                    callback.onFailure(errorMessage);
                });
    }
    
    /**
     * Firestore에서 사용자 정보 가져오기
     */
    private void getUserFromFirestore(String uid, SignInCallback callback) {
        db.collection("users").document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // User 객체 생성
                        User user = new User();
                        user.setUid(uid);
                        user.setEmail(documentSnapshot.getString("email"));
                        user.setNickname(documentSnapshot.getString("nickname"));
                        user.setGender(documentSnapshot.getString("gender"));
                        // birthDate는 필요에 따라 변환
                        
                        callback.onSuccess(user);
                    } else {
                        callback.onFailure("사용자 정보를 찾을 수 없습니다.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "사용자 정보 조회 실패", e);
                    callback.onFailure("사용자 정보 조회 중 오류가 발생했습니다.");
                });
    }
    
    /**
     * 로그인 오류 메시지를 한국어로 변환
     */
    private String getSignInErrorMessage(Exception e) {
        if (e instanceof FirebaseAuthInvalidCredentialsException) {
            return "이메일 또는 비밀번호가 올바르지 않습니다.";
        } else {
            return "로그인 중 오류가 발생했습니다: " + e.getMessage();
        }
    }
}
