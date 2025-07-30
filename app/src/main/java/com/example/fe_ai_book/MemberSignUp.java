package com.example.fe_ai_book;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.niwattep.materialslidedatepicker.SlideDatePickerDialog;
import com.niwattep.materialslidedatepicker.SlideDatePickerDialogCallback;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MemberSignUp extends AppCompatActivity implements SlideDatePickerDialogCallback {

    EditText email, emailcode, password1, password2, nickname;
    Button emailsend_btn, emailverify_btn, setdate_btn, signup_btn;
    RadioGroup gender;
    RadioButton man, woman;
    LinearLayout emailcodelayout;
    TextView date_view;

    String str_email, str_emailcode, str_password1, str_password2, str_nickname, str_gender;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

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

        date_view.setText(format.format(calendar.getTime()));
    }

} //mainActivity