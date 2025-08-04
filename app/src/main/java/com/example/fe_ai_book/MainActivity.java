package com.example.fe_ai_book;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button signup = findViewById(R.id.signup_view);
        Button userInfo = findViewById(R.id.btn_user_info);
        Button mybookRecent = findViewById(R.id.btn_mybook_recent);
        Button btnSearch = findViewById(R.id.btn_search);
        Button btnDirectSearch = findViewById(R.id.btn_direct_search);
        // Button btn_home = findViewById(R.id.btn_home); // 제거됨

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MemberSignUp.class);
                startActivity(intent);
            }
        });

        userInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserInfoActivity.class);
                startActivity(intent);
            }
        });

        mybookRecent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyBookRecentActivity.class);
                startActivity(intent);
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);
            }
        });

        btnDirectSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DirectSearchActivity.class);
                startActivity(intent);
            }
        });

        // Home 버튼 기능 제거됨 - HomeActivity가 존재하지 않음
    }
}
