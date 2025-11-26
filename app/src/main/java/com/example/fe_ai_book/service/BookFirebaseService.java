package com.example.fe_ai_book.service;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.fe_ai_book.model.Book;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class BookFirebaseService {

    private static final String TAG = "BookFirebaseService";

    // Firestore에서 "테이블" 역할을 하는 컬렉션 이름
    private static final String COLLECTION_BOOKS = "Books";          // 전체 도서 정보 테이블
    private static final String COLLECTION_USER_BOOKS = "UserBooks"; // 사용자 도서 정보 테이블

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // =====================================================================
    // 1. 전체 도서 정보 저장/수정 (Books 테이블 = Books 컬렉션)
    //    - Book: title, author, publisher, isbn, publishDate,
    //            description, pageCount, imageUrl, category
    // =====================================================================
    public void saveOrUpdateBook(@NonNull Book book, String userId) {

        if (book.getIsbn() == null || book.getIsbn().isEmpty()) {
            Log.w(TAG, "[saveOrUpdateBook] ISBN 이 비어 있어서 저장 불가");
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("isbn", book.getIsbn());
        data.put("title", book.getTitle());
        data.put("author", book.getAuthor());
        data.put("publisher", book.getPublisher());
        data.put("publishDate", book.getPublishDate());

        // category null / 빈 문자열 방지
        data.put(
                "category",
                (book.getCategory() != null && !book.getCategory().isEmpty())
                        ? book.getCategory()
                        : "기타"
        );

        data.put("description", book.getDescription());
        data.put("imageUrl", book.getImageUrl());
        data.put("pageCount", book.getPageCount());

        // createdAt / updatedAt 서버 시간 저장
        // Book 모델에 createdAt 필드가 있고, 처음 저장 시 null 이라고 가정 (설계 선택)
        if (book.getCreatedAt() == null) {
            data.put("createdAt", FieldValue.serverTimestamp());
        }
        data.put("updatedAt", FieldValue.serverTimestamp());

        db.collection(COLLECTION_BOOKS)
                .document(book.getIsbn())              // PK = isbn
                .set(data, SetOptions.merge())         // 있으면 수정, 없으면 생성
                .addOnSuccessListener(aVoid ->
                        Log.d(TAG, "Global Book saved/updated: " + book.getTitle()))
                .addOnFailureListener(e ->
                        Log.w(TAG, "Error saving/updating global book", e));
    }

    // =====================================================================
    // 2. 사용자 책장 저장/수정 (UserBooks 테이블 = UserBooks 컬렉션)
    //    - UserBook: userId, isbn, location, bookmark, createdAt, updatedAt
    // =====================================================================
    public void saveOrUpdateUserBook(
            @NonNull String userId,
            @NonNull String isbn,
            @NonNull String location,
            boolean bookmark
    ) {

        if (isbn.isEmpty()) {
            Log.w(TAG, "[saveOrUpdateUserBook] ISBN 이 비어 있어서 저장 불가");
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("isbn", isbn);
        data.put("location", location);
        data.put("bookmark", bookmark);

        // createdAt / updatedAt - 서버에서 시간 기록
        // merge 를 쓰기 때문에, 최초 호출 시 createdAt이 생성되고
        // 이후 업데이트에도 다시 덮어써질 수 있음 (설계 선택)
        data.put("createdAt", FieldValue.serverTimestamp());
        data.put("updatedAt", FieldValue.serverTimestamp());

        // 문서 ID = userId_isbn (동일 유저 + 동일 책 중복 저장 방지용 설계)
        String userBookDocId = userId + "_" + isbn;

        db.collection(COLLECTION_USER_BOOKS)
                .document(userBookDocId)
                .set(data, SetOptions.merge())
                .addOnSuccessListener(aVoid ->
                        Log.d(TAG, "UserBook saved/updated: " + userBookDocId))
                .addOnFailureListener(e ->
                        Log.w(TAG, "Error saving/updating UserBook", e));
    }

    // 도서 대출 정보 가져오기
    public void saveLoanStats(String isbn, String gender, String ageRange, int loanCount) {
        if (isbn == null || isbn.isEmpty()) {
            Log.w(TAG, "ISBN is empty, cannot save loan stats.");
            return;
        }

        String docId = gender + "_" + ageRange;

        Map<String, Object> data = new HashMap<>();
        data.put("gender", gender);
        data.put("ageRange", ageRange);
        data.put("loanCount", loanCount);
        data.put("updatedAt", FieldValue.serverTimestamp());

        db.collection("Books")
                .document(isbn)
                .collection("loanStats")
                .document(docId)
                .set(data, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "LoanStats saved: " + isbn + " / " + docId);
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error saving LoanStats", e);
                });
    }


    // =====================================================================
    // 3. 사용자 책장에서 책 삭제
    //    - 전체 책 정보(Books)는 그대로 두고,
    //      특정 사용자의 UserBooks 엔트리만 삭제
    // =====================================================================
    public void deleteUserBook(@NonNull String userId, @NonNull String isbn) {

        if (isbn.isEmpty()) {
            Log.w(TAG, "[deleteUserBook] ISBN 이 비어 있어서 삭제 불가");
            return;
        }

        String userBookDocId = userId + "_" + isbn;

        db.collection(COLLECTION_USER_BOOKS)
                .document(userBookDocId)
                .delete()
                .addOnSuccessListener(aVoid ->
                        Log.d(TAG, "UserBook deleted: " + userBookDocId))
                .addOnFailureListener(e ->
                        Log.w(TAG, "Error deleting UserBook", e));
    }
}
