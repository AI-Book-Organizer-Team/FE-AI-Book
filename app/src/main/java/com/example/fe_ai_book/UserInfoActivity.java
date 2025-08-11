package com.example.fe_ai_book;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
        Button btnLogout = findViewById(R.id.btn_logout);
        Button btnMyLibrary = findViewById(R.id.btn_my_library);
        Button btnSettings = findViewById(R.id.btn_settings);
        ImageView btnBack = findViewById(R.id.btn_back);

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

        // 버튼 클릭 시 글씨색 변경 로직 적용
        setButtonTextColorChange(btnMyLibrary);
        setButtonTextColorChange(btnSettings);
        setButtonTextColorChange(btnLogout);

        // 로그아웃 버튼 클릭
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            SharedPreferences prefs = getSharedPreferences("autoLogin", MODE_PRIVATE);
            prefs.edit().clear().apply();

            Intent intent = new Intent(UserInfoActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // 내 서재 버튼 클릭
        btnMyLibrary.setOnClickListener(v -> {
            Intent intent = new Intent(UserInfoActivity.this, MyBookRecentActivity.class);
            startActivity(intent);
        });

        // 설정 버튼 클릭
        btnSettings.setOnClickListener(v -> {
            // 설정 화면으로 이동하는 코드 넣으면 됨
        });

        // 뒤로가기 버튼 클릭
        btnBack.setOnClickListener(v -> finish());
    }

    // 버튼 상태에 따라 글씨색 변경
    private void setButtonTextColorChange(Button button) {
        button.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    button.setTextColor(Color.WHITE);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    button.setTextColor(Color.BLACK);
                    break;
            }
            return false;
        });
    }
}
