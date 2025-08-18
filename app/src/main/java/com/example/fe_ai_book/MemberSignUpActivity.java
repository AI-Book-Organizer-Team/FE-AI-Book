package com.example.fe_ai_book;

<<<<<<< HEAD
import android.graphics.Color;
import android.os.Bundle;
=======
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
>>>>>>> 5e7144d9bf40bbc30208eed98208c31d0daffa14
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
<<<<<<< HEAD

import androidx.appcompat.app.AppCompatActivity;

=======
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fe_ai_book.model.User;
import com.example.fe_ai_book.service.AuthApiService;
import com.example.fe_ai_book.service.MockAuthApiService;
import com.example.fe_ai_book.service.FirebaseAuthService;
import com.example.fe_ai_book.utils.ValidationUtils;
>>>>>>> 5e7144d9bf40bbc30208eed98208c31d0daffa14
import com.niwattep.materialslidedatepicker.SlideDatePickerDialog;
import com.niwattep.materialslidedatepicker.SlideDatePickerDialogCallback;

import java.text.SimpleDateFormat;
import java.util.Calendar;
<<<<<<< HEAD
=======
import java.util.Date;
>>>>>>> 5e7144d9bf40bbc30208eed98208c31d0daffa14
import java.util.Locale;

public class MemberSignUpActivity extends AppCompatActivity implements SlideDatePickerDialogCallback {

    EditText email, emailcode, password1, password2, nickname;
    Button emailsend_btn, emailverify_btn, setdate_btn, signup_btn;
    RadioGroup gender;
    RadioButton man, woman;
    LinearLayout emailcodelayout;
<<<<<<< HEAD
    TextView date_view, emailverify_str;

    String str_email, str_emailcode, str_password1, str_password2, str_nickname, str_gender;
=======
    TextView date_view, emailverify_str, nicknameT, nicknameF;

    String str_email, str_emailcode, str_password1, str_password2, str_nickname, str_gender;
    Date selectedBirthDate;
    boolean isEmailVerified = false;
    boolean isNicknameChecked = false;
    
    // API 서비스
    private AuthApiService authApiService;
>>>>>>> 5e7144d9bf40bbc30208eed98208c31d0daffa14
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
<<<<<<< HEAD

        email = (EditText) findViewById(R.id.email);
        emailcode = (EditText) findViewById(R.id.emailcode);
        password1 = (EditText) findViewById(R.id.password1);
        password2 = (EditText) findViewById(R.id.password2);
        nickname = (EditText) findViewById(R.id.nickname);
        emailsend_btn = (Button) findViewById(R.id.emailsend_btn);
        emailverify_btn = (Button) findViewById(R.id.emailverify_btn);
        setdate_btn = (Button) findViewById(R.id.setdate_btn);
        signup_btn = (Button) findViewById(R.id.signup_btn);
        gender = (RadioGroup) findViewById(R.id.gender);
        man = (RadioButton) findViewById(R.id.man);
        woman = (RadioButton) findViewById(R.id.woman);
        emailcodelayout = (LinearLayout) findViewById(R.id.emailcodelayout);
        date_view = (TextView) findViewById(R.id.date_view);
        emailverify_str = (TextView) findViewById(R.id.emailverify_str);

        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                str_email = email.getText().toString();
                str_emailcode = emailcode.getText().toString();
                str_password1 = password1.getText().toString();
                str_password2 = password2.getText().toString();
                str_nickname = nickname.getText().toString();

                if (man.isChecked()) {
                    str_gender = man.getText().toString();
                } else {
                    str_gender = woman.getText().toString();
                }
            }
        }); // 정보 입력

        emailsend_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailcodelayout.setVisibility(View.VISIBLE);
                emailsend_btn.setVisibility(View.GONE);

                emailverify_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        emailverify_str.setVisibility(View.VISIBLE);
                    }
                }); // 이메일 인증
            }
        }); // 이메일 인증

        setdate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar endDate = Calendar.getInstance();
                endDate.set(Calendar.YEAR, 2100);

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
        }); // 생년월일 다이얼로그
    }

    public void onPositiveClick(int day, int month, int year, Calendar calendar){
        SimpleDateFormat format = new SimpleDateFormat("yyyy년 MMM dd일", Locale.KOREAN);

=======
        
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
        // Firebase Authentication + Firestore 사용 
        // 이메일 발송은 Mock (123456 입력하면 인증 통과)
        authApiService = new FirebaseAuthService();
    }
    
    private void setupListeners() {
        // 이메일 인증번호 전송
        emailsend_btn.setOnClickListener(v -> sendEmailVerification());
        
        // 이메일 인증번호 확인
        emailverify_btn.setOnClickListener(v -> verifyEmail());
        
        // 생년월일 선택
        setdate_btn.setOnClickListener(v -> showDatePicker());
        
        // 회원가입
        signup_btn.setOnClickListener(v -> performSignUp());
        
        // 닉네임 실시간 중복 확인
        nickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            
            @Override
            public void afterTextChanged(Editable s) {
                String nicknameText = s.toString().trim();
                if (!ValidationUtils.isEmpty(nicknameText)) {
                    checkNicknameDuplicate(nicknameText);
                } else {
                    resetNicknameStatus();
                }
            }
        });
    }
    
    /**
     * 이메일 인증번호 전송
     */
    private void sendEmailVerification() {
        String emailText = email.getText().toString().trim();
        
        // 유효성 검사
        String emailError = ValidationUtils.getEmailValidationMessage(emailText);
        if (emailError != null) {
            Toast.makeText(this, emailError, Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 버튼 비활성화 및 로딩 상태
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
    
    /**
     * 이메일 인증번호 확인
     */
    private void verifyEmail() {
        String emailText = email.getText().toString().trim();
        String codeText = emailcode.getText().toString().trim();
        
        // 유효성 검사
        if (!ValidationUtils.isValidVerificationCode(codeText)) {
            Toast.makeText(this, "6자리 인증번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 버튼 비활성화 및 로딩 상태
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
                Toast.makeText(MemberSignUpActivity.this, "이메일 인증이 완료되었습니다.", Toast.LENGTH_SHORT).show();
            }
            
            @Override
            public void onFailure(String errorMessage) {
                emailverify_btn.setEnabled(true);
                emailverify_btn.setText("인증");
                Toast.makeText(MemberSignUpActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    /**
     * 닉네임 중복 확인
     */
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
                // 네트워크 오류 등의 경우 중복확인 상태를 리셋
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
    
    /**
     * 생년월일 선택 다이얼로그
     */
    private void showDatePicker() {
        Calendar endDate = Calendar.getInstance();
        endDate.set(Calendar.YEAR, 2100);
        
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
    
    /**
     * 회원가입 수행
     */
    private void performSignUp() {
        // 입력값 수집
        str_email = email.getText().toString().trim();
        str_password1 = password1.getText().toString();
        str_password2 = password2.getText().toString();
        str_nickname = nickname.getText().toString().trim();
        
        // 성별 선택 확인
        if (man.isChecked()) {
            str_gender = "남성";
        } else if (woman.isChecked()) {
            str_gender = "여성";
        } else {
            str_gender = null;
        }
        
        // 유효성 검사
        if (!validateSignUpForm()) {
            return;
        }
        
        // User 객체 생성
        User user = new User(str_email, str_password1, str_nickname, str_gender, selectedBirthDate);
        
        // 회원가입 버튼 비활성화 및 로딩 상태
        signup_btn.setEnabled(false);
        signup_btn.setText("가입 중...");
        
        authApiService.signUp(user, new AuthApiService.SignUpCallback() {
            @Override
            public void onSuccess(User newUser) {
                Toast.makeText(MemberSignUpActivity.this, "회원가입이 완료되었습니다!", Toast.LENGTH_SHORT).show();
                
                // 로그인 화면으로 이동
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
    
    /**
     * 회원가입 폼 유효성 검사
     */
    private boolean validateSignUpForm() {
        // 이메일 검사
        String emailError = ValidationUtils.getEmailValidationMessage(str_email);
        if (emailError != null) {
            Toast.makeText(this, emailError, Toast.LENGTH_SHORT).show();
            return false;
        }
        
        // 이메일 인증 확인
        if (!isEmailVerified) {
            Toast.makeText(this, "이메일 인증을 완료해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        // 비밀번호 검사
        String passwordError = ValidationUtils.getPasswordValidationMessage(str_password1);
        if (passwordError != null) {
            Toast.makeText(this, passwordError, Toast.LENGTH_SHORT).show();
            return false;
        }
        
        // 비밀번호 확인 검사
        if (!ValidationUtils.doPasswordsMatch(str_password1, str_password2)) {
            Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        // 닉네임 검사
        String nicknameError = ValidationUtils.getNicknameValidationMessage(str_nickname);
        if (nicknameError != null) {
            Toast.makeText(this, nicknameError, Toast.LENGTH_SHORT).show();
            return false;
        }
        
        // 닉네임 중복확인 체크
        if (!isNicknameChecked) {
            Toast.makeText(this, "닉네임 중복확인을 완료해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        return true;
    }

    public void onPositiveClick(int day, int month, int year, Calendar calendar){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd", Locale.KOREAN);
        
        // 선택된 날짜를 변수에 저장
        selectedBirthDate = calendar.getTime();
        
        // UI에 표시
>>>>>>> 5e7144d9bf40bbc30208eed98208c31d0daffa14
        date_view.setText(format.format(calendar.getTime()));
    }

} //mainActivity