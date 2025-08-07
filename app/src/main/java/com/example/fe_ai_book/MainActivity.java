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

        Button btnLogin = findViewById(R.id.btn_login);
        Button signup = findViewById(R.id.signup_view);
        Button userInfo = findViewById(R.id.btn_user_info);
        Button mybookRecent = findViewById(R.id.btn_mybook_recent);
        Button btnSearch = findViewById(R.id.btn_search);
        Button btnDirectSearch = findViewById(R.id.btn_direct_search);
        Button btn_ai = findViewById(R.id.btn_ai);
        Button categoryViewBtn = findViewById(R.id.btn_category_view);
        Button btnBookDetail = findViewById(R.id.btn_book_detail);
        Button btn_book_shelf = findViewById(R.id.btn_book_shelf);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MemberSignUpActivity.class);
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

        btn_ai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AiActivity.class);
                startActivity(intent);
            }
        });

        btn_book_shelf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BookShelfActivity.class);
                startActivity(intent);
            }
        });

        categoryViewBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MyBookCategoryActivity.class);
            startActivity(intent);
        });

        btnBookDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BookDetailActivity.class);
                intent.putExtra("book_title", "자유론");
                intent.putExtra("book_author", "존 스튜어트 밀");
                intent.putExtra("book_image", R.drawable.sample_cover_backducksu);
                startActivity(intent);
            }
        });

    }
}
