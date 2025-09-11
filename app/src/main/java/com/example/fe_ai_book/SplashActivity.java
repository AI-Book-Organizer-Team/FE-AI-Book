package com.example.fe_ai_book;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                // 이미 로그인된 상태
                startActivity(new Intent(this, MainHomebarActivity.class));
            } else {
                // 로그인 필요
                startActivity(new Intent(this, LoginActivity.class));
            }
            finish();
        }, 1500); // 1.5초 후 화면 전환
    }
}
