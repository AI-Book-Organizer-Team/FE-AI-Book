package com.example.fe_ai_book;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;

public class MainActivity extends AppCompatActivity {

    private Button btnShowBottomSheet; // 카메라 스캔 버튼
    private static final int BARCODE_SCAN_REQUEST_CODE = 2001;       // 📷 카메라 모드 요청 코드
    private static final int BARCODE_IMAGE_TEST_REQUEST_CODE = 2002; // 🖼 이미지 테스트 모드 요청 코드

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
        Button btnapi = findViewById(R.id.btnapi);
        Button categoryViewBtn = findViewById(R.id.btn_category_view);
        Button btnBookDetail = findViewById(R.id.btn_book_detail);
        btnShowBottomSheet = findViewById(R.id.btnShowBottomSheet);

        // ====================== 📌 테스트 모드 버튼 (Drawable 이미지로 바코드 인식) ======================
        // 나중에 실제 배포 시 이 버튼과 관련 코드는 삭제하면 됨
        Button btnImageTest = findViewById(R.id.btn_image_test);
        btnImageTest.setOnClickListener(v -> {
            Intent intent = new Intent(this, BarcodeImageTestActivity.class);
            startActivityForResult(intent, BARCODE_IMAGE_TEST_REQUEST_CODE);
        });
        // ============================================================================================

        // 로그인/회원가입/기타 이동 버튼
        btnLogin.setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)));
        signup.setOnClickListener(v -> startActivity(new Intent(this, MemberSignUpActivity.class)));
        userInfo.setOnClickListener(v -> startActivity(new Intent(this, UserInfoActivity.class)));
        mybookRecent.setOnClickListener(v -> startActivity(new Intent(this, MyBookRecentActivity.class)));
        btnSearch.setOnClickListener(v -> startActivity(new Intent(this, SearchActivity.class)));
        btnDirectSearch.setOnClickListener(v -> startActivity(new Intent(this, DirectSearchActivity.class)));
        btn_ai.setOnClickListener(v -> startActivity(new Intent(this, AiActivity.class)));
        categoryViewBtn.setOnClickListener(v -> startActivity(new Intent(this, MyBookCategoryActivity.class)));
        btnapi.setOnClickListener(v -> startActivity(new Intent(this, BookDetailEXActivity.class)));

        btnBookDetail.setOnClickListener(v -> {
            Intent intent = new Intent(this, BookDetailActivity.class);
            intent.putExtra("book_title", "자유론");
            intent.putExtra("book_author", "존 스튜어트 밀");
            intent.putExtra("book_image", R.drawable.sample_cover_backducksu);
            startActivity(intent);
        });

        // ====================== 📷 실제 카메라 스캔 모드 ======================
        // → 테스트 끝나면 이 코드만 남기면 됨
        btnShowBottomSheet.setOnClickListener(v -> {
            Intent intent = new Intent(this, BarcodeScanActivity.class);
            startActivityForResult(intent, BARCODE_SCAN_REQUEST_CODE);
        });
        // ================================================================
    }

    // 카메라/이미지 테스트 결과 받아서 바텀시트 표시
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            String isbn = data.getStringExtra("isbn");

            if (requestCode == BARCODE_SCAN_REQUEST_CODE) {
                // 카메라 모드 결과
                showBookConfirmBottomSheet(isbn);
            } else if (requestCode == BARCODE_IMAGE_TEST_REQUEST_CODE) {
                // 이미지 테스트 모드 결과
                showBookConfirmBottomSheet(isbn);
            }
        }
    }

    private void showBookConfirmBottomSheet(String isbn) {
        if (isFinishing() || isDestroyed()) return;

        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View sheet = getLayoutInflater().inflate(R.layout.sheet_book_confirm, null);

        ImageView ivCover = sheet.findViewById(R.id.ivCover);
        TextView tvTitle = sheet.findViewById(R.id.tvTitle);
        TextView tvAuthor = sheet.findViewById(R.id.tvAuthor);
        TextView tvPubDate = sheet.findViewById(R.id.tvPubDate);
        TextView tvPub = sheet.findViewById(R.id.tvPub);
        TextView tvIsbn = sheet.findViewById(R.id.tvIsbn);
        Button btnConfirm = sheet.findViewById(R.id.btnConfirm); // ← Button
        Button btnCancel  = sheet.findViewById(R.id.btnCancel);  // ← Button

        if (ivCover == null || tvTitle == null || tvAuthor == null || tvPub == null || tvIsbn == null
                || btnConfirm == null || btnCancel == null) {
            android.widget.Toast.makeText(this, "sheet_book_confirm.xml의 뷰 ID를 확인", android.widget.Toast.LENGTH_LONG).show();
            return;
        }

        // 샘플 데이터 (API 연동 전)
        ivCover.setImageResource(R.drawable.sample_cover_backducksu);
        tvTitle.setText("불온한 검은 피");
        tvAuthor.setText("저자: 허연");
        if (tvPubDate != null) tvPubDate.setText("발행 연도: 14.04.28");
        tvPub.setText("출판사: 민음사");
        tvIsbn.setText("ISBN: " + (isbn == null ? "N/A" : isbn));

        btnConfirm.setOnClickListener(v -> {
            // TODO: 등록 처리
            android.widget.Toast.makeText(this, "등록 완료(샘플)", android.widget.Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.setContentView(sheet);
        dialog.show();
    }

}