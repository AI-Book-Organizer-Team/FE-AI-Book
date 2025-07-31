package com.example.fe_ai_book;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText editEmail, editPassword;
    private Button buttonLogin;
    private TextView textViewRegister, textViewFindPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editEmail = findViewById(R.id.editTextEmail);
        editPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewRegister = findViewById(R.id.textViewRegister);
        textViewFindPassword = findViewById(R.id.textViewFindPassword);


        buttonLogin.setOnClickListener(v -> {
            String email = editEmail.getText().toString().trim();
            String password = editPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "모든 항목을 입력해주세요", Toast.LENGTH_SHORT).show();
                return;
            }


            Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });


        textViewRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, MemberSignUp.class);
            startActivity(intent);
        });


        // 비밀번호 찾기 이동 - 찾기 화면으로 변경해야함.
        textViewFindPassword.setOnClickListener(v -> {
            Toast.makeText(this, "비밀번호 찾기 이동", Toast.LENGTH_SHORT).show();
        });
    }
}
