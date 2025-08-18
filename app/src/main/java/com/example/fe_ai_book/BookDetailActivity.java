package com.example.fe_ai_book;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.fe_ai_book.model.BookDetailEnvelope;
import com.example.fe_ai_book.service.ApiClient;
import com.example.fe_ai_book.service.DataLibraryApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookDetailActivity extends AppCompatActivity {

    private ImageView backButton;
    private ImageView bookCoverImageView;
    private TextView bookTitleTextView;
    private TextView bookAuthorTextView;
    private ImageView addButton;
    private ImageView removeButton;
    private TextView bookDescriptionTextView;
    private TextView bookPublisherTextView;
    private TextView bookGenreTextView;
    private TextView bookPagesTextView;
    private TextView bookReleaseDateTextView;
    private TextView isbnTextView;
    private TextView tagsTextView;
    private ImageView editLocationIcon;
    private TextView locationTextView;
    private ImageView bookmarkButton;
    private Call<BookDetailEnvelope> inFlight;

    // 빈 문자열을 "-"로 표시
    private String emptyToDash(String s) {
        return (s == null || s.trim().isEmpty()) ? "-" : s.trim();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        initViews();
        setupClickListeners();
        loadBookData();
    }

    private void initViews() {
        backButton = findViewById(R.id.backButton);
        bookCoverImageView = findViewById(R.id.bookCoverImageView);
        bookTitleTextView = findViewById(R.id.bookTitleTextView);
        bookAuthorTextView = findViewById(R.id.bookAuthorTextView);
        addButton = findViewById(R.id.addButton);
        removeButton = findViewById(R.id.removeButton);
        bookDescriptionTextView = findViewById(R.id.bookDescriptionTextView);
        bookPublisherTextView = findViewById(R.id.bookPublisherTextView);
        bookGenreTextView = findViewById(R.id.bookGenreTextView);
        bookPagesTextView = findViewById(R.id.bookPagesTextView);
        bookReleaseDateTextView = findViewById(R.id.bookReleaseDateTextView);
        isbnTextView = findViewById(R.id.isbnTextView);
        tagsTextView = findViewById(R.id.tagsTextView);
        editLocationIcon = findViewById(R.id.editLocationIcon);
        locationTextView = findViewById(R.id.locationTextView);
        bookmarkButton = findViewById(R.id.bookmarkButton);
    }

    private void setupClickListeners() {
        // 뒤로가기 버튼
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 책장에 추가 버튼
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(BookDetailActivity.this, "책장에 추가되었습니다!", Toast.LENGTH_SHORT).show();
            }
        });

        // 책장에서 제거 버튼
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(BookDetailActivity.this, "책장에서 제거되었습니다!", Toast.LENGTH_SHORT).show();
            }
        });

        // 위치 수정 버튼
        editLocationIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(BookDetailActivity.this, "위치 수정 기능", Toast.LENGTH_SHORT).show();
            }
        });

        // 책갈피 버튼
        bookmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(BookDetailActivity.this, "책갈피에 추가되었습니다!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadBookData() {
        // isbn 넘겨받기
        String isbn13 = getIntent().getStringExtra("isbn13");
        if (isbn13 == null || isbn13.trim().isEmpty()) {
            Toast.makeText(this, "ISBN13이 없어 상세 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 3) API 호출
        DataLibraryApi api = ApiClient.get();
        inFlight = api.getBookDetail(BuildConfig.DATA4LIB_AUTH_KEY, isbn13, "Y", "age", "json");

        inFlight.enqueue(new Callback<BookDetailEnvelope>() {
            @Override
            public void onResponse(Call<BookDetailEnvelope> call, Response<BookDetailEnvelope> res) {

                if (!res.isSuccessful() || res.body() == null || res.body().response == null) {
                    Toast.makeText(BookDetailActivity.this, "응답 오류: " + res.code(), Toast.LENGTH_SHORT).show();
                    return;
                }

                BookDetailEnvelope.Inner r = res.body().response;

                if (r.error != null && !r.error.isEmpty()) {
                    Toast.makeText(BookDetailActivity.this, r.error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (r.detail == null || r.detail.isEmpty() || r.detail.get(0).book == null) {
                    Toast.makeText(BookDetailActivity.this, "도서 상세가 없습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 4) 서버 모델 -> UI 모델 변환(Mapper)
                BookDetailEnvelope.Book apiBook = r.detail.get(0).book;
                com.example.fe_ai_book.model.Book ui = com.example.fe_ai_book.mapper.BookApiMapper.toUi(apiBook);

                // 5) View에 바인딩 (tags, location 제외 모두 API값으로)
                bookTitleTextView.setText(emptyToDash(ui.getTitle()));          // 제목
                bookAuthorTextView.setText(emptyToDash(ui.getAuthor()));        // 저자
                bookPublisherTextView.setText(emptyToDash(ui.getPublisher()));  // 출판사
                bookReleaseDateTextView.setText(emptyToDash(ui.getPublishDate())); // 발행일(YYYY-MM-DD 우선)

                // 장르(분류명): 서버 모델의 class_nm을 쓰려면 mapper에 추가해도 됨.
                // 일단 apiBook.class_nm 직접 사용(원하면 mapper에 필드 추가)
                String genre = apiBook.class_nm; // 예: "철학"
                bookGenreTextView.setText(emptyToDash(genre));

                // 페이지 수: data4library 상세에 페이지 정보가 없으면 "-" 처리
                bookPagesTextView.setText("-");

                // 설명
                String desc = apiBook.description;
                bookDescriptionTextView.setText(emptyToDash(desc));

                // ISBN
                isbnTextView.setText(emptyToDash(ui.getIsbn()));

                // 표지 이미지
                if (ui.getImageUrl() != null && !ui.getImageUrl().trim().isEmpty()) {
                    // Glide 사용(추천)
                    // implementation 'com.github.bumptech.glide:glide:<version>'
                    Glide.with(BookDetailActivity.this)
                            .load(ui.getImageUrl())
                            .placeholder(R.drawable.ic_launcher_background)
                            .error(R.drawable.ic_launcher_background)
                            .into(bookCoverImageView);
                } else {
                    // 이미지 URL 없으면 기본 리소스
                    bookCoverImageView.setImageResource(R.drawable.ic_launcher_background);
                }

                // 6) 태그/보관 위치는 그대로 유지(네 요청)
                // tagsTextView.setText("#국내도서 #인문 #철학 #자유 개념 #자유론");
                // locationTextView.setText("책장 3번째 칸 왼쪽에 있는 책");
            }

            @Override
            public void onFailure(Call<BookDetailEnvelope> call, Throwable t) {
                if (call.isCanceled()) return;
                Toast.makeText(BookDetailActivity.this, "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });



//        // Intent에서 전달받은 데이터로 책 정보를 설정
//        String title = getIntent().getStringExtra("book_title");
//        String author = getIntent().getStringExtra("book_author");
//        int imageResId = getIntent().getIntExtra("book_image", R.drawable.ic_launcher_background);
//
//        if (title != null && author != null) {
//            bookTitleTextView.setText(title);
//            bookAuthorTextView.setText(author);
//            bookCoverImageView.setImageResource(imageResId);
//        }
//
//        // 나머지 정보는 기본값으로 설정 (실제 앱에서는 API나 데이터베이스에서 가져와야 함)
//        bookDescriptionTextView.setText("이 책은 시민과 국가의 관계, 즉 시민의 자유가 어디까지 보장되고, 국가의 간섭은 어디까지 미칠 수 있는지를 다루고 있다.");
//        bookPublisherTextView.setText("현대지성");
//        bookGenreTextView.setText("인문");
//        bookPagesTextView.setText("256P");
//        bookReleaseDateTextView.setText("18.06.01");
//        isbnTextView.setText("9791187142447");
//        tagsTextView.setText("#국내도서 #인문 #철학 #자유 개념 #자유론");
//        locationTextView.setText("책장 3번째 칸 왼쪽에 있는 책");
    }
}
