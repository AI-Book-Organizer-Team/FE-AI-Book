package com.example.fe_ai_book;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.fe_ai_book.adapter.DirectSearchBookAdapter;
import com.example.fe_ai_book.mapper.BookApiMapper;
import com.example.fe_ai_book.model.Book;
import com.example.fe_ai_book.model.BookDetailEnvelope;
import com.example.fe_ai_book.service.ApiClient;
import com.example.fe_ai_book.service.DataLibraryApi;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private Button btnShowBottomSheet; // 카메라 스캔 버튼
    private static final int BARCODE_SCAN_REQUEST_CODE = 2001;       // 📷 카메라 모드 요청 코드
    private static final int BARCODE_IMAGE_TEST_REQUEST_CODE = 2002; // 🖼 이미지 테스트 모드 요청 코드

    private Call<BookDetailEnvelope> call; //API 호출

    private Book currentBook;

    private DirectSearchBookAdapter adapter;

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
        btnShowBottomSheet = findViewById(R.id.btnShowBottomSheet);
        Button btnMyBook = findViewById(R.id.btn_mybook);
        Button btn_home = findViewById(R.id.btn_home);
        Button btn_book_shelf = findViewById(R.id.btn_book_shelf);

        // ====================== 📌 테스트 모드 버튼 (Drawable 이미지로 바코드 인식) ======================
        // 나중에 실제 배포 시 이 버튼과 관련 코드는 삭제하면 됨
        Button btnImageTest = findViewById(R.id.btn_image_test);
        btnImageTest.setOnClickListener(v -> {
            Intent intent = new Intent(this, BarcodeImageTestActivity.class);
            startActivityForResult(intent, BARCODE_IMAGE_TEST_REQUEST_CODE);
        });
        
        // 도서 저장 테스트 버튼
        Button btnBookSaveTest = findViewById(R.id.btn_book_save_test);
        btnBookSaveTest.setOnClickListener(v -> {
            Intent intent = new Intent(this, BookSaveTestActivity.class);
            startActivity(intent);
        });

        Button btnGoHomebar = findViewById(R.id.btn_home); // 이미 있는 버튼 재활용
        btnGoHomebar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MainHomebarActivity.class);
            startActivity(intent);
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
        btn_home.setOnClickListener(v -> startActivity(new Intent(this, HomeActivity.class)));
        btn_book_shelf.setOnClickListener(v -> startActivity(new Intent(this, BookShelfActivity.class)));

        // 내 서재 (Fragment 3개 탭 포함된 Activity)
        btnMyBook.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MyBookActivity.class);
            startActivity(intent);
        });

        btnBookDetail.setOnClickListener(v -> {
            Intent intent = new Intent(this, BookDetailActivity.class);
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

        ImageView cover = sheet.findViewById(R.id.ivCover);
        TextView title = sheet.findViewById(R.id.tvTitle);
        TextView author = sheet.findViewById(R.id.tvAuthor);
        TextView Date = sheet.findViewById(R.id.tvPubDate);
        TextView Pub = sheet.findViewById(R.id.tvPub);
        TextView isbn13 = sheet.findViewById(R.id.tvIsbn);
        Button btnConfirm = sheet.findViewById(R.id.btnConfirm); // ← Button
        Button btnCancel  = sheet.findViewById(R.id.btnCancel);  // ← Button

        if (cover == null || title == null || author == null || Pub == null || isbn == null
                || btnConfirm == null || btnCancel == null) {
            android.widget.Toast.makeText(this, "sheet_book_confirm.xml의 뷰 ID를 확인", android.widget.Toast.LENGTH_LONG).show();
            return;
        }

        currentBook = new Book();

        DataLibraryApi api = ApiClient.get();

        call = api.getBookDetail(BuildConfig.DATA4LIB_AUTH_KEY, isbn, "N", "age", "json");

        call.enqueue(new Callback<BookDetailEnvelope>() {
            @Override
            public void onResponse(Call<BookDetailEnvelope> call, Response<BookDetailEnvelope> response) {
                if (!response.isSuccessful() || response.body() == null || response.body().response == null) {
                    Toast.makeText(MainActivity.this, "응답 오류" + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }

                BookDetailEnvelope.Inner r = response.body().response;

                if (r.error != null || r.detail.isEmpty() || r.detail.get(0).book == null) {
                    Toast.makeText(MainActivity.this, "도서 상세가 없습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (r.error != null || r.detail.isEmpty()) {
                    Toast.makeText(MainActivity.this, r.error, Toast.LENGTH_SHORT).show();
                    return;
                }

                // 서버 모델 -> UI 모델 변환(Mapper)
                BookDetailEnvelope.Book apiBook = r.detail.get(0).book;

                Book ui = BookApiMapper.toUi(apiBook);
                currentBook = ui;

                if (ui.getIsbn() != null) {
                    title.setText(currentBook.getTitle());
                    author.setText("저자: " + currentBook.getAuthor());
                    Pub.setText("출판사: " + currentBook.getPublisher());
                    Date.setText("발행 연도: " + currentBook.getPublishDate());
                    isbn13.setText("ISBN: " + currentBook.getIsbn());

                }

                // 표지 이미지
                if (ui.getImageUrl() != null && !ui.getImageUrl().trim().isEmpty()) {
                    // implementation 'com.github.bumptech.glide:glide:<version>'
                    Glide.with(MainActivity.this)
                            .load(ui.getImageUrl())
                            .placeholder(R.drawable.ic_launcher_background)
                            .error(R.drawable.ic_launcher_background)
                            .into(cover);
                } else {
                    // 이미지 URL 없으면 기본 리소스
                    cover.setImageResource(R.drawable.ic_launcher_background);
                }

                btnConfirm.setOnClickListener(v -> {
                    // TODO: 등록 처리
                    android.widget.Toast.makeText(MainActivity.this, "등록 완료(샘플)", android.widget.Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                });

                btnCancel.setOnClickListener(v -> dialog.dismiss());

                dialog.setContentView(sheet);
                dialog.show();
            }

            @Override
            public void onFailure(Call<BookDetailEnvelope> call, Throwable throwable) {
                if (call.isCanceled()) return;
                Toast.makeText(MainActivity.this, "오류: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

}