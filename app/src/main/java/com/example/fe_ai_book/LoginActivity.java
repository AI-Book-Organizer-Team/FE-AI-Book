package com.example.fe_ai_book;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fe_ai_book.model.User;
import com.example.fe_ai_book.service.AuthApiService;
import com.example.fe_ai_book.service.FirebaseAuthService;
import com.example.fe_ai_book.service.MockAuthApiService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText editEmail, editPassword;
    private Button buttonLogin;
    private TextView textViewRegister, textViewFindPassword;

    private FirebaseAuth mAuth;
    private AuthApiService authApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authApiService = new FirebaseAuthService();

        // FirebaseAuth 객체의 공유 인스턴스를 가져옴
        mAuth = FirebaseAuth.getInstance();


        editEmail = findViewById(R.id.editTextEmail);
        editPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewRegister = findViewById(R.id.textViewRegister);
        textViewFindPassword = findViewById(R.id.textViewFindPassword);

        // 회원가입에서 전달받은 이메일이 있다면 자동 입력
        String emailFromSignUp = getIntent().getStringExtra("email");
        if (emailFromSignUp != null) {
            editEmail.setText(emailFromSignUp);
        }


        buttonLogin.setOnClickListener(v -> {
            String email = editEmail.getText().toString().trim();
            String password = editPassword.getText().toString().trim();


            authApiService.signIn(email, password, new AuthApiService.SignInCallback() {
                @Override
                public void onSuccess(User user) {
                    Toast.makeText(LoginActivity.this, user.getNickname() + "님, 환영합니다!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    // 사용자 정보를 전달할 수 있음
                    intent.putExtra("userNickname", user.getNickname());
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFailure(String errorMessage) {
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "모든 항목을 입력해주세요", Toast.LENGTH_SHORT).show();
                return;


            }

            // 로그인 버튼 비활성화 및 로딩 상태
            buttonLogin.setEnabled(false);
            buttonLogin.setText("로그인 중...");
            
            // Firebase 로그인 시도
            authApiService.signIn(email, password, new AuthApiService.SignInCallback() {
                @Override
                public void onSuccess(User user) {
                    Toast.makeText(LoginActivity.this, user.getNickname() + "님, 환영합니다!", Toast.LENGTH_SHORT).show();
                    
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    // 사용자 정보를 전달할 수 있음
                    intent.putExtra("userNickname", user.getNickname());
                    startActivity(intent);
                    finish();
                }
                
                @Override
                public void onFailure(String errorMessage) {
                    buttonLogin.setEnabled(true);
                    buttonLogin.setText("로그인");
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        });


        textViewRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, MemberSignUpActivity.class);
            startActivity(intent);
        });


        // 비밀번호 찾기 이동 - 찾기 화면으로 변경해야함.
        textViewFindPassword.setOnClickListener(v -> {
            Toast.makeText(this, "비밀번호 찾기 이동", Toast.LENGTH_SHORT).show();
        });
    }
}
