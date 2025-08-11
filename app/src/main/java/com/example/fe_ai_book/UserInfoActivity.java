package com.example.fe_ai_book;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.TextView;
import android.content.SharedPreferences;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserInfoActivity extends AppCompatActivity {

    private TextView tvNickname, tvBookCount;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_info);

        // UI 연결
        tvNickname = findViewById(R.id.tv_nickname);
        tvBookCount = findViewById(R.id.tv_book_count);

        // Firebase 초기화
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        String uid = mAuth.getCurrentUser().getUid();

        // Firestore에서 데이터 읽기
        db.collection("users").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String nickname = documentSnapshot.getString("nickname");
                        Long bookCountValue = documentSnapshot.getLong("bookCount");
                        long bookCount = (bookCountValue != null) ? bookCountValue : 0;

                        tvNickname.setText(nickname);
                        tvBookCount.setText("나의 모든 도서\n" + bookCount + "권");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("UserInfo", "데이터 불러오기 실패", e);
                });
    }
}
