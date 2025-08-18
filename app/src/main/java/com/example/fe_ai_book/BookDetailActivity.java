package com.example.fe_ai_book;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.fe_ai_book.model.Book;
import com.example.fe_ai_book.model.BookDetailEnvelope;
import com.example.fe_ai_book.service.ApiClient;
import com.example.fe_ai_book.service.BookFirebaseService;
import com.example.fe_ai_book.service.DataLibraryApi;
import com.google.firebase.auth.FirebaseAuth;

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

    private BookFirebaseService bookService;
    private String userId;
    private Book currentBook;

    private Call<BookDetailEnvelope> inFlight;

    // 빈 문자열을 "-"로 표시
    private String emptyToDash(String s) {
        return (s == null || s.trim().isEmpty()) ? "-" : s.trim();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        bookService = new BookFirebaseService();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

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
        // 뒤로가기
        backButton.setOnClickListener(v -> finish());

        // 책 추가 → Firestore 저장
        addButton.setOnClickListener(v -> {
            if (currentBook != null) {
                bookService.saveOrUpdateBook(currentBook, userId);
                Toast.makeText(this, "책장에 추가되었습니다!", Toast.LENGTH_SHORT).show();
            }
        });

        // 책 제거 → Firestore 삭제
        removeButton.setOnClickListener(v -> {
            if (currentBook != null) {
                bookService.deleteBook(currentBook.getIsbn(), userId);
                Toast.makeText(this, "책장에서 제거되었습니다!", Toast.LENGTH_SHORT).show();
            }
        });

        // 위치 수정
        editLocationIcon.setOnClickListener(v ->
                Toast.makeText(this, "위치 수정 기능", Toast.LENGTH_SHORT).show()
        );

        // 책갈피
        bookmarkButton.setOnClickListener(v ->
                Toast.makeText(this, "책갈피에 추가되었습니다!", Toast.LENGTH_SHORT).show()
        );
    }

    private void loadBookData() {
        // isbn 넘겨받기
        String isbn13 = getIntent().getStringExtra("isbn13");
        if (isbn13 == null || isbn13.trim().isEmpty()) {
            Toast.makeText(this, "ISBN13이 없어 상세 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // API 호출
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

                // 서버 모델 -> UI 모델 변환(Mapper)
                BookDetailEnvelope.Book apiBook = r.detail.get(0).book;
                com.example.fe_ai_book.model.Book ui = com.example.fe_ai_book.mapper.BookApiMapper.toUi(apiBook);

                // View에 바인딩 (tags, location 제외 모두 API값으로)
                bookTitleTextView.setText(emptyToDash(ui.getTitle()));          // 제목
                bookAuthorTextView.setText(emptyToDash(ui.getAuthor()));        // 저자
                bookPublisherTextView.setText(emptyToDash(ui.getPublisher()));  // 출판사
                bookReleaseDateTextView.setText(emptyToDash(ui.getPublishDate()));
                bookGenreTextView.setText(emptyToDash(ui.getCategory()));

                // 페이지 수: data4library 상세에 페이지 정보가 없으면 "-" 처리
                bookPagesTextView.setText("-");

                // 설명
                String rawDescription = apiBook.description;
                if (rawDescription != null && !rawDescription.isEmpty()) {
                    // HTML 엔티티(&lt; &gt;) 문자로 변환
                    bookDescriptionTextView.setText(Html.fromHtml(rawDescription, Html.FROM_HTML_MODE_LEGACY));
                } else {
                    bookDescriptionTextView.setText("책 소개가 없습니다.");
                }

                // ISBN
                isbnTextView.setText(emptyToDash(ui.getIsbn()));

                // 표지 이미지
                if (ui.getImageUrl() != null && !ui.getImageUrl().trim().isEmpty()) {
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


            } // onResponse

            @Override
            public void onFailure(Call<BookDetailEnvelope> call, Throwable t) {
                if (call.isCanceled()) return;
                Toast.makeText(BookDetailActivity.this, "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        // Intent에서 전달받은 데이터
        String title = getIntent().getStringExtra("book_title");
        String author = getIntent().getStringExtra("book_author");
        String publisher = getIntent().getStringExtra("book_publisher");
        String publishDate = getIntent().getStringExtra("book_publishDate");
        String isbn = getIntent().getStringExtra("book_isbn");
        String description = getIntent().getStringExtra("book_description");
        String imageUrl = getIntent().getStringExtra("book_imageUrl");

        // Book 객체 생성 (Firestore에 저장할 용도)
        currentBook = new Book();
        currentBook.setTitle(title);
        currentBook.setAuthor(author);
        currentBook.setPublisher(publisher);
        currentBook.setPublishDate(publishDate);
        currentBook.setIsbn(isbn);
        currentBook.setDescription(description);
        currentBook.setImageUrl(imageUrl);
        currentBook.setUserId(userId);

        // UI 반영
        if (title != null) bookTitleTextView.setText(title);
        if (author != null) bookAuthorTextView.setText(author);
        if (publisher != null) bookPublisherTextView.setText(publisher);
        if (publishDate != null) bookReleaseDateTextView.setText(publishDate);
        if (isbn != null) isbnTextView.setText(isbn);
        if (description != null) bookDescriptionTextView.setText(description);

        // 이미지 로딩 (Glide 같은 라이브러리 쓰면 편함)
        // Glide.with(this).load(imageUrl).into(bookCoverImageView);
    }
}
