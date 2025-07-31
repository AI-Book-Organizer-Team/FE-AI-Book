package com.example.fe_ai_book;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.content.SharedPreferences;
import android.content.Intent;


public class UserInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_info);

        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

//        Button logout = findViewById(R.id.btn_logout);
//        logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // 자동 로그인 정보 삭제
//                SharedPreferences prefs = getSharedPreferences("autoLogin", MODE_PRIVATE);
//                SharedPreferences.Editor editor = prefs.edit();
//                editor.clear();  // or editor.remove("key")
//                editor.apply();
//
//                // 로그인 화면으로 이동
//                Intent intent = new Intent(UserInfoActivity.this, LoginActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);
//
//                // 현재 액티비티 종료
//                finish();
//            }
//        });
    }
}
