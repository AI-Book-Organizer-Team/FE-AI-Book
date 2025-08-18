package com.example.fe_ai_book;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fe_ai_book.model.User;
import com.example.fe_ai_book.service.AuthApiService;
import com.example.fe_ai_book.service.FirebaseAuthService;
import com.example.fe_ai_book.utils.ValidationUtils;
import com.niwattep.materialslidedatepicker.SlideDatePickerDialog;
import com.niwattep.materialslidedatepicker.SlideDatePickerDialogCallback;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MemberSignUpActivity extends AppCompatActivity implements SlideDatePickerDialogCallback {

    EditText email, emailcode, password1, password2, nickname;
    Button emailsend_btn, emailverify_btn, setdate_btn, signup_btn;
    RadioGroup gender;
    RadioButton man, woman;
    LinearLayout emailcodelayout;
    TextView date_view, emailverify_str, nicknameT, nicknameF;

    String str_email, str_password1, str_password2, str_nickname, str_gender;
    Date selectedBirthDate;
    boolean isEmailVerified = false;
    boolean isNicknameChecked = false;

    // API 서비스
    private AuthApiService authApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initViews();
        initApiService();
        setupListeners();
    }

    private void initViews() {
        email = findViewById(R.id.email);
        emailcode = findViewById(R.id.emailcode);
        password1 = findViewById(R.id.password1);
        password2 = findViewById(R.id.password2);
        nickname = findViewById(R.id.nickname);
        emailsend_btn = findViewById(R.id.emailsend_btn);
        emailverify_btn = findViewById(R.id.emailverify_btn);
        setdate_btn = findViewById(R.id.setdate_btn);
        signup_btn = findViewById(R.id.signup_btn);
        gender = findViewById(R.id.gender);
        man = findViewById(R.id.man);
        woman = findViewById(R.id.woman);
        emailcodelayout = findViewById(R.id.emailcodelayout);
        date_view = findViewById(R.id.date_view);
        emailverify_str = findViewById(R.id.emailverify_str);
        nicknameT = findViewById(R.id.nicknameT);
        nicknameF = findViewById(R.id.nicknameF);
    }

    private void initApiService() {
        authApiService = new FirebaseAuthService();
    }

    private void setupListeners() {
        emailsend_btn.setOnClickListener(v -> sendEmailVerification());
        emailverify_btn.setOnClickListener(v -> verifyEmail());
        setdate_btn.setOnClickListener(v -> showDatePicker());
        signup_btn.setOnClickListener(v -> performSignUp());

        nickname.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                String nicknameText = s.toString().trim();
                if (!ValidationUtils.isEmpty(nicknameText)) {
                    checkNicknameDuplicate(nicknameText);
                } else {
                    resetNicknameStatus();
                }
            }
        });
    }

    private void sendEmailVerification() {
        String emailText = email.getText().toString().trim();
        String emailError = ValidationUtils.getEmailValidationMessage(emailText);
        if (emailError != null) {
            Toast.makeText(this, emailError, Toast.LENGTH_SHORT).show();
            return;
        }

        emailsend_btn.setEnabled(false);
        emailsend_btn.setText("전송 중...");

        authApiService.sendEmailVerification(emailText, new AuthApiService.EmailVerificationCallback() {
            @Override
            public void onSuccess() {
                emailcodelayout.setVisibility(View.VISIBLE);
                emailsend_btn.setVisibility(View.GONE);
                Toast.makeText(MemberSignUpActivity.this, "인증번호가 발송되었습니다.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String errorMessage) {
                emailsend_btn.setEnabled(true);
                emailsend_btn.setText("인증번호 전송하기");
                Toast.makeText(MemberSignUpActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void verifyEmail() {
        String emailText = email.getText().toString().trim();
        String codeText = emailcode.getText().toString().trim();

        if (!ValidationUtils.isValidVerificationCode(codeText)) {
            Toast.makeText(this, "6자리 인증번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        emailverify_btn.setEnabled(false);
        emailverify_btn.setText("확인");

        authApiService.verifyEmail(emailText, codeText, new AuthApiService.EmailVerificationCallback() {
            @Override
            public void onSuccess() {
                isEmailVerified = true;
                emailverify_str.setText("인증이 완료되었습니다.");
                emailverify_str.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                emailverify_str.setVisibility(View.VISIBLE);
                emailverify_btn.setText("완료");
                emailverify_btn.setEnabled(false);
                emailcode.setEnabled(false);
            }

            @Override
            public void onFailure(String errorMessage) {
                emailverify_btn.setEnabled(true);
                emailverify_btn.setText("인증");
                Toast.makeText(MemberSignUpActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkNicknameDuplicate(String nicknameText) {
        String nicknameError = ValidationUtils.getNicknameValidationMessage(nicknameText);
        if (nicknameError != null) {
            showNicknameError(nicknameError);
            return;
        }

        authApiService.checkNicknameDuplicate(nicknameText, new AuthApiService.ApiCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean isAvailable) {
                if (isAvailable) {
                    showNicknameAvailable();
                    isNicknameChecked = true;
                } else {
                    showNicknameUnavailable();
                    isNicknameChecked = false;
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                resetNicknameStatus();
                isNicknameChecked = false;
            }
        });
    }

    private void showNicknameAvailable() {
        nicknameT.setVisibility(View.VISIBLE);
        nicknameF.setVisibility(View.GONE);
        nicknameT.setText("사용 가능한 닉네임이에요");
        nicknameT.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
    }

    private void showNicknameUnavailable() {
        nicknameT.setVisibility(View.GONE);
        nicknameF.setVisibility(View.VISIBLE);
        nicknameF.setText("이미 사용 중인 닉네임이에요");
        nicknameF.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
    }

    private void showNicknameError(String error) {
        nicknameT.setVisibility(View.GONE);
        nicknameF.setVisibility(View.VISIBLE);
        nicknameF.setText(error);
        nicknameF.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
    }

    private void resetNicknameStatus() {
        nicknameT.setVisibility(View.GONE);
        nicknameF.setVisibility(View.GONE);
    }

    private void showDatePicker() {
        SlideDatePickerDialog.Builder builder = new SlideDatePickerDialog.Builder();
        builder.setEndDate(Calendar.getInstance());
        builder.setLocale(Locale.KOREAN);
        builder.setThemeColor(Color.rgb(187,211,241));
        builder.setShowYear(true);
        builder.setCancelText("취소");
        builder.setConfirmText("확인");

        SlideDatePickerDialog dialog = builder.build();
        dialog.show(getSupportFragmentManager(), "Dialog");
    }

    private void performSignUp() {
        str_email = email.getText().toString().trim();
        str_password1 = password1.getText().toString();
        str_password2 = password2.getText().toString();
        str_nickname = nickname.getText().toString().trim();

        if (man.isChecked()) {
            str_gender = "남성";
        } else if (woman.isChecked()) {
            str_gender = "여성";
        } else {
            str_gender = null;
        }

        if (!validateSignUpForm()) {
            return;
        }

        User user = new User(str_email, str_password1, str_nickname, str_gender, selectedBirthDate);

        signup_btn.setEnabled(false);
        signup_btn.setText("가입 중...");

        authApiService.signUp(user, new AuthApiService.SignUpCallback() {
            @Override
            public void onSuccess(User newUser) {
                Toast.makeText(MemberSignUpActivity.this, "회원가입이 완료되었습니다!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MemberSignUpActivity.this, LoginActivity.class);
                intent.putExtra("email", newUser.getEmail());
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(String errorMessage) {
                signup_btn.setEnabled(true);
                signup_btn.setText("회원가입");
                Toast.makeText(MemberSignUpActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateSignUpForm() {
        String emailError = ValidationUtils.getEmailValidationMessage(str_email);
        if (emailError != null) {
            Toast.makeText(this, emailError, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!isEmailVerified) {
            Toast.makeText(this, "이메일 인증을 완료해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }
        String passwordError = ValidationUtils.getPasswordValidationMessage(str_password1);
        if (passwordError != null) {
            Toast.makeText(this, passwordError, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!ValidationUtils.doPasswordsMatch(str_password1, str_password2)) {
            Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            return false;
        }
        String nicknameError = ValidationUtils.getNicknameValidationMessage(str_nickname);
        if (nicknameError != null) {
            Toast.makeText(this, nicknameError, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!isNicknameChecked) {
            Toast.makeText(this, "닉네임 중복확인을 완료해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onPositiveClick(int day, int month, int year, Calendar calendar){
        SimpleDateFormat format = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREAN);
        selectedBirthDate = calendar.getTime();
        date_view.setText(format.format(calendar.getTime()));
    }
}
