package com.example.fe_ai_book.dao;

import android.util.Log;

import com.example.fe_ai_book.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserDao {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final String TAG = "UserDao";

    /**
     * 유저 데이터 불러오기
     */
    public void getUserData(FirestoreCallback callback) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("users").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        callback.onCallback(user);
                    } else {
                        Log.w(TAG, "유저 문서가 존재하지 않음");
                        callback.onCallback(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "유저 데이터 가져오기 실패", e);
                    callback.onCallback(null);
                });
    }

    /**
     * bookCount 값 증가
     */
    public void incrementBookCount() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("users").document(uid)
                .update("bookCount", FieldValue.increment(1))
                .addOnFailureListener(e -> Log.e(TAG, "bookCount 증가 실패", e));
    }

    /**
     * bookCount 값 감소
     */
    public void decrementBookCount() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("users").document(uid)
                .update("bookCount", FieldValue.increment(-1))
                .addOnFailureListener(e -> Log.e(TAG, "bookCount 감소 실패", e));
    }

    /**
     * Firestore 비동기 콜백 인터페이스
     */
    public interface FirestoreCallback {
        void onCallback(User user);
    }
}
