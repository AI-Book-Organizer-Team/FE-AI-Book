package com.example.fe_ai_book;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.fe_ai_book.mapper.BookApiMapper;
import com.example.fe_ai_book.model.Book;
import com.example.fe_ai_book.model.BookDetailEnvelope;
import com.example.fe_ai_book.service.ApiClient;
import com.example.fe_ai_book.service.BookFirebaseService;
import com.example.fe_ai_book.service.DataLibraryApi;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

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

    private String currentLocation = "";      // 🔥 변경: 위치는 Book이 아니라 UserBook용 별도 필드
    private boolean currentBookmark = true;   // 🔥 기본값: 책장에 넣으면 bookmark=true 로 저장(설계 선택)

    private Call<BookDetailEnvelope> inFlight;

    // 빈 문자열을 "-"로 표시
    private String emptyToDash(String s) {
        return (s == null || s.trim().isEmpty()) ? "-" : s.trim();
    }

    // 중복 도서 체크 (UserBooks 테이블 기준)
    private void ifAlreadyBook(String isbn, String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // 🔥 변경: users/{userId}/books/{isbn} → UserBooks/userId_isbn
        String userBookDocId = userId + "_" + isbn;

        db.collection("UserBooks")
                .document(userBookDocId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        // 이미 저장된 도서 → 추가 버튼 숨기고 제거 버튼 보이기
                        addButton.setVisibility(View.INVISIBLE);
                        removeButton.setVisibility(View.VISIBLE);
                    } else {
                        // 저장되지 않은 도서 → 추가 버튼 보이기, 제거 버튼 숨기기
                        addButton.setVisibility(View.VISIBLE);
                        removeButton.setVisibility(View.INVISIBLE);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("BookDetailActivity", "Error checking book existence", e);
                    // 실패 시 기본값: 추가 버튼 보이기
                    addButton.setVisibility(View.VISIBLE);
                });
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

        // 책 추가 → Books + UserBooks 저장
        addButton.setOnClickListener(v -> {
            if (currentBook != null) {
                // 1) 전체 책 정보 저장
                bookService.saveOrUpdateBook(currentBook, userId);  // 🔥 변경: userId 제거

                // 2) 사용자 책 정보 저장 (위치 + 북마크)
                String isbn = currentBook.getIsbn();
                String location = locationTextView.getText().toString().trim();
                if (location.isEmpty()) {
                    location = "위치를 설정해주세요";
                }
                currentLocation = location;

                bookService.saveOrUpdateUserBook(
                        userId,
                        isbn,
                        currentLocation,
                        currentBookmark
                );

                Toast.makeText(this, "책장에 추가되었습니다!", Toast.LENGTH_SHORT).show();
                ifAlreadyBook(isbn, userId);
            }
        });

        // 책 제거 → UserBooks에서만 삭제
        removeButton.setOnClickListener(v -> {
            if (currentBook != null) {
                String isbn = currentBook.getIsbn();
                bookService.deleteUserBook(userId, isbn);  // 🔥 변경: deleteBook → deleteUserBook

                Toast.makeText(this, "책장에서 제거되었습니다!", Toast.LENGTH_SHORT).show();
                ifAlreadyBook(isbn, userId);
            }
        });

        // 위치 수정 다이얼로그
        editLocationIcon.setOnClickListener(v -> showLocationEditDialog());

        // 책갈피 (여기서는 UI + UserBooks 저장까지 같이 처리)
        bookmarkButton.setOnClickListener(v -> {
            currentBookmark = !currentBookmark; // 토글

            // 아이콘 바꾸고 싶으면 여기서 변경 (예시는 아이콘 유지 + 토스트만)
            String msg = currentBookmark ? "책갈피에 추가되었습니다!" : "책갈피가 해제되었습니다!";
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

            if (currentBook != null && currentBook.getIsbn() != null) {
                String isbn = currentBook.getIsbn();
                String location = locationTextView.getText().toString().trim();
                if (location.isEmpty()) {
                    location = "위치를 설정해주세요";
                }
                currentLocation = location;

                // 위치/책갈피 둘 다 UserBooks에 반영
                bookService.saveOrUpdateUserBook(
                        userId,
                        isbn,
                        currentLocation,
                        currentBookmark
                );
            }
        });
    }

    private void loadBookData() {
        // Intent에서 전달받은 데이터 먼저 표시
        String title = getIntent().getStringExtra("book_title");
        String author = getIntent().getStringExtra("book_author");
        String publisher = getIntent().getStringExtra("book_publisher");
        String publishDate = getIntent().getStringExtra("book_publishDate");
        String isbn = getIntent().getStringExtra("book_isbn");
        String description = getIntent().getStringExtra("book_description");
        String imageUrl = getIntent().getStringExtra("book_imageUrl");
        String category = getIntent().getStringExtra("book_category");
        int imageResId = getIntent().getIntExtra("book_image_res", 0);

        // Book 객체 생성 (Firestore의 Books 테이블에 저장할 용도)
        currentBook = new Book();
        currentBook.setTitle(title);
        currentBook.setAuthor(author);
        currentBook.setPublisher(publisher);
        currentBook.setPublishDate(publishDate);
        currentBook.setIsbn(isbn);
        currentBook.setDescription(description);
        currentBook.setImageUrl(imageUrl);
        currentBook.setCategory(category);
        // 🔥 Book에는 userId/location 없음

        // UI에 먼저 반영
        bookTitleTextView.setText(emptyToDash(title));
        bookAuthorTextView.setText(emptyToDash(author));
        bookPublisherTextView.setText(emptyToDash(publisher));
        bookReleaseDateTextView.setText(emptyToDash(publishDate));
        bookGenreTextView.setText(emptyToDash(category));
        isbnTextView.setText(emptyToDash(isbn));

        // 설명
        if (description != null && !description.isEmpty()) {
            bookDescriptionTextView.setText(description);
        } else {
            bookDescriptionTextView.setText("책 소개가 없습니다.");
        }

        int pageCount = getIntent().getIntExtra("book_pageCount", 0);
        String tags = getIntent().getStringExtra("book_tags");
        String location = getIntent().getStringExtra("book_location");

        // 페이지 수
        if (pageCount > 0) {
            bookPagesTextView.setText(pageCount + "P");
        } else {
            bookPagesTextView.setText("-");
        }

        // 태그 표시 (지금은 UI에만 사용, DB에는 저장 안 함)
        if (tags != null && !tags.isEmpty()) {
            tagsTextView.setText(tags);
        } else if (category != null && !category.isEmpty()) {
            String autoTags = generateTagsFromCategory(category, title);
            tagsTextView.setText(autoTags);
            // currentBook.setTags(autoTags);  // 🔥 Book에는 tags 필드 없다고 가정
        } else {
            tagsTextView.setText("");
        }

        // 위치 표시 (UserBook용 필드 - Activity에서만 임시 보관)
        if (location != null && !location.isEmpty()) {
            locationTextView.setText(location);
            currentLocation = location;  // 🔥 Book이 아니라 Activity 필드에 저장
        } else {
            locationTextView.setText("위치를 설정해주세요");
            currentLocation = "위치를 설정해주세요";
        }

        // currentBook에 페이지 수 저장
        currentBook.setPageCount(pageCount);

        // 이미지 로딩
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.book_placeholder_background)
                    .error(R.drawable.book_placeholder_background)
                    .into(bookCoverImageView);
        } else if (imageResId != 0) {
            bookCoverImageView.setImageResource(imageResId);
        } else {
            bookCoverImageView.setImageResource(R.drawable.book_placeholder_background);
        }

        // ISBN13으로 API 호출
        String isbn13 = getIntent().getStringExtra("isbn13");
        if (isbn13 == null || isbn13.trim().isEmpty()) {
            // API 호출 없이, 현재 데이터로 중복 체크
            if (isbn != null && !isbn.isEmpty()) {
                ifAlreadyBook(isbn, userId);
            }
            return;
        }
        currentBook.setIsbn(isbn13);

        DataLibraryApi api = ApiClient.get();
        inFlight = api.getBookDetail(BuildConfig.DATA4LIB_AUTH_KEY, isbn13, "Y", "age");

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

                BookDetailEnvelope.Book apiBook = r.detail.get(0).book;
                Book ui = BookApiMapper.toUi(apiBook);

                if (ui.getIsbn() != null) {
                    currentBook.setIsbn(ui.getIsbn());
                    currentBook.setTitle(ui.getTitle());
                    currentBook.setAuthor(ui.getAuthor());
                    currentBook.setPublisher(ui.getPublisher());
                    currentBook.setPublishDate(ui.getPublishDate());
                    currentBook.setDescription(ui.getDescription());
                    currentBook.setImageUrl(ui.getImageUrl());
                    currentBook.setCategory(ui.getCategory());
                }

                // View 바인딩
                bookTitleTextView.setText(emptyToDash(ui.getTitle()));
                bookAuthorTextView.setText(emptyToDash(ui.getAuthor()));
                bookPublisherTextView.setText(emptyToDash(ui.getPublisher()));
                bookReleaseDateTextView.setText(emptyToDash(ui.getPublishDate()));
                bookGenreTextView.setText(emptyToDash(ui.getCategory()));

                bookPagesTextView.setText(emptyToDash("-"));

                String rawDescription = apiBook.description;
                if (rawDescription != null && !rawDescription.isEmpty()) {
                    bookDescriptionTextView.setText(Html.fromHtml(rawDescription, Html.FROM_HTML_MODE_LEGACY));
                } else {
                    bookDescriptionTextView.setText("책 소개가 없습니다.");
                }

                isbnTextView.setText(emptyToDash(ui.getIsbn()));

                if (ui.getImageUrl() != null && !ui.getImageUrl().trim().isEmpty()) {
                    Glide.with(BookDetailActivity.this)
                            .load(ui.getImageUrl())
                            .placeholder(R.drawable.book_placeholder_background)
                            .error(R.drawable.book_placeholder_background)
                            .into(bookCoverImageView);
                }

                // API 데이터로 업데이트된 ISBN 기준으로 중복 도서 체크
                ifAlreadyBook(currentBook.getIsbn(), userId);
            }

            @Override
            public void onFailure(Call<BookDetailEnvelope> call, Throwable t) {
                if (call.isCanceled()) return;
                Log.e("BookDetailActivity", "API 호출 실패: " + t.getMessage());
            }
        });
    }

    // 카테고리와 제목을 기반으로 태그 자동 생성
    private String generateTagsFromCategory(String category, String title) {
        StringBuilder tags = new StringBuilder();

        if (category != null && !category.isEmpty()) {
            String[] categories = category.split("[>/]");
            for (String cat : categories) {
                String trimmedCat = cat.trim();
                if (!trimmedCat.isEmpty()) {
                    if (tags.length() > 0) tags.append(" ");
                    tags.append("#").append(trimmedCat);
                }
            }
        }

        if (title != null && !title.isEmpty()) {
            if (title.length() <= 10) {
                if (tags.length() > 0) tags.append(" ");
                tags.append("#").append(title.replaceAll("\\s+", ""));
            }
        }

        if (tags.length() == 0) {
            tags.append("#도서");
        }

        return tags.toString();
    }

    // 위치 수정 다이얼로그 표시
    private void showLocationEditDialog() {
        EditText editText = new EditText(this);
        editText.setHint("예: 책장 A-3, 2층 서재 등");

        // 🔥 변경: Book.getLocation() 대신 Activity의 currentLocation 사용
        if (currentLocation != null && !currentLocation.equals("위치를 설정해주세요")) {
            editText.setText(currentLocation);
        }

        new AlertDialog.Builder(this)
                .setTitle("도서 위치 입력")
                .setMessage("이 책의 위치를 입력해주세요.")
                .setView(editText)
                .setPositiveButton("저장", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newLocation = editText.getText().toString().trim();
                        if (!newLocation.isEmpty()) {
                            locationTextView.setText(newLocation);
                            currentLocation = newLocation;  // 🔥 Book이 아니라 Activity 필드에 저장

                            if (currentBook != null && currentBook.getIsbn() != null) {
                                // 위치 변경도 UserBooks에 반영
                                bookService.saveOrUpdateUserBook(
                                        userId,
                                        currentBook.getIsbn(),
                                        currentLocation,
                                        currentBookmark
                                );
                                Toast.makeText(BookDetailActivity.this,
                                        "위치가 저장되었습니다: " + newLocation,
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(BookDetailActivity.this,
                                    "위치를 입력해주세요.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("취소", null)
                .show();
    }
}
