package com.example.fe_ai_book.utils;

import java.util.regex.Pattern;

public class ValidationUtils {
    
    // 이메일 정규식 패턴
    private static final Pattern EMAIL_PATTERN = 
            Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    
    // 비밀번호 정규식 패턴 (영문, 숫자, 특수문자 포함 8-20자)
    private static final Pattern PASSWORD_PATTERN = 
            Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?])\\S{8,20}$");
    
    // 닉네임 정규식 패턴 (한글, 영문, 숫자 2-10자)
    private static final Pattern NICKNAME_PATTERN = 
            Pattern.compile("^[가-힣a-zA-Z0-9]{2,10}$");

    /**
     * 이메일 형식이 유효한지 검증
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * 비밀번호 형식이 유효한지 검증
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    /**
     * 비밀번호가 일치하는지 검증
     */
    public static boolean doPasswordsMatch(String password1, String password2) {
        if (password1 == null || password2 == null) {
            return false;
        }
        return password1.equals(password2);
    }

    /**
     * 닉네임 형식이 유효한지 검증
     */
    public static boolean isValidNickname(String nickname) {
        if (nickname == null || nickname.trim().isEmpty()) {
            return false;
        }
        return NICKNAME_PATTERN.matcher(nickname.trim()).matches();
    }

    /**
     * 인증번호 형식이 유효한지 검증 (6자리 숫자)
     */
    public static boolean isValidVerificationCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return false;
        }
        return code.trim().matches("^\\d{6}$");
    }

    /**
     * 문자열이 비어있는지 검증
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * 이메일 유효성 검사 결과 메시지 반환
     */
    public static String getEmailValidationMessage(String email) {
        if (isEmpty(email)) {
            return "이메일을 입력해주세요.";
        }
        if (!isValidEmail(email)) {
            return "올바른 이메일 형식을 입력해주세요.";
        }
        return null; // 유효한 경우
    }

    /**
     * 비밀번호 유효성 검사 결과 메시지 반환
     */
    public static String getPasswordValidationMessage(String password) {
        if (isEmpty(password)) {
            return "비밀번호를 입력해주세요.";
        }
        if (!isValidPassword(password)) {
            return "비밀번호는 영문, 숫자, 특수문자를 모두 포함해 공백 없이 8~20자로 입력해주세요.";
        }
        return null; // 유효한 경우
    }

    /**
     * 닉네임 유효성 검사 결과 메시지 반환
     */
    public static String getNicknameValidationMessage(String nickname) {
        if (isEmpty(nickname)) {
            return "닉네임을 입력해주세요.";
        }
        if (!isValidNickname(nickname)) {
            return "닉네임은 한글, 영문, 숫자를 포함해 2~10자로 입력해주세요.";
        }
        return null; // 유효한 경우
    }
}
