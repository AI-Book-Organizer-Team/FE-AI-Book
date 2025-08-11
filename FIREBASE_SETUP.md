# Firebase 연동 설정 가이드

## 1. Firebase 프로젝트 생성

1. [Firebase Console](https://console.firebase.google.com/) 방문
2. "프로젝트 추가" 클릭
3. 프로젝트 이름: `FE-AI-Book` (또는 원하는 이름)
4. Google Analytics 설정 (선택사항)

## 2. Android 앱 추가

1. Firebase 프로젝트에서 "Android" 아이콘 클릭
2. 패키지 이름: `com.example.fe_ai_book` 입력
3. 앱 닉네임: `FE AI Book` (선택사항)
4. 디버그 서명 인증서 SHA-1 (선택사항, 나중에 추가 가능)

## 3. google-services.json 다운로드

1. Firebase Console에서 `google-services.json` 파일 다운로드
2. 다운로드한 파일을 `app/` 폴더에 복사 (기존 placeholder 파일 대체)

## 4. Firebase Authentication 설정

1. Firebase Console에서 "Authentication" 선택
2. "시작하기" 클릭
3. "Sign-in method" 탭으로 이동
4. "이메일/비밀번호" 제공업체 선택하고 "사용 설정"

## 5. Firestore Database 설정

1. Firebase Console에서 "Firestore Database" 선택
2. "데이터베이스 만들기" 클릭
3. 보안 규칙: 테스트 모드에서 시작 (개발 중)
4. 위치: 가까운 지역 선택 (예: asia-northeast3)

## 6. 빌드 및 테스트

1. Android Studio에서 프로젝트 동기화
2. 앱 빌드 및 실행
3. 회원가입/로그인 기능 테스트

## 7. 보안 규칙 설정 (프로덕션용)

Firestore 보안 규칙을 다음과 같이 설정:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // 사용자는 자신의 문서만 읽기/쓰기 가능
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
    
    // 닉네임 중복 확인을 위한 읽기 권한
    match /users/{userId} {
      allow read: if request.auth != null;
    }
  }
}
```

## 현재 구현된 기능

✅ **회원가입**
- 이메일/비밀번호 회원가입
- 닉네임 중복 확인 (Firestore 기반)
- 사용자 정보 Firestore 저장

✅ **로그인**
- 이메일/비밀번호 로그인
- Firebase Authentication 연동

✅ **데이터 구조**
- Users 컬렉션에 사용자 정보 저장
- uid, email, nickname, gender, birthDate 필드

## 추가 개발 가능 사항

🔲 **이메일 인증**
- Firebase Functions를 통한 실제 이메일 인증
- 인증 코드 발송 및 검증

🔲 **비밀번호 재설정**
- Firebase Auth의 패스워드 리셋 기능

🔲 **소셜 로그인**
- Google, Facebook 로그인 추가

🔲 **프로필 관리**
- 사용자 프로필 수정 기능

## 주의사항

- `google-services.json` 파일은 실제 Firebase 프로젝트에서 다운로드해야 합니다
- 현재 이메일 인증은 간단한 형태로만 구현되어 있습니다
- 프로덕션 환경에서는 보안 규칙을 반드시 설정해야 합니다
