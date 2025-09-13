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

    private Call<BookDetailEnvelope> inFlight;

    // 빈 문자열을 "-"로 표시
    private String emptyToDash(String s) {
        return (s == null || s.trim().isEmpty()) ? "-" : s.trim();
    }

    // 중복 도서
    private void ifAlreadyBook(String isbn, String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .document(userId)
                .collection("books")
                .document(isbn)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        // 이미 저장된 도서 → 버튼 숨기기
                        addButton.setVisibility(View.INVISIBLE);
                    } else {
                        // 저장되지 않은 도서 → 버튼 보이기
                        addButton.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("BookDetailActivity", "Error checking book existence", e);
                    // 실패 시 버튼 유지
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

        // 위치 수정 다이얼로그
        editLocationIcon.setOnClickListener(v -> showLocationEditDialog());

        // 책갈피
        bookmarkButton.setOnClickListener(v ->
                Toast.makeText(this, "책갈피에 추가되었습니다!", Toast.LENGTH_SHORT).show()
        );
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

        // Book 객체 생성 (Firestore에 저장할 용도)
        currentBook = new Book();
        currentBook.setTitle(title);
        currentBook.setAuthor(author);
        currentBook.setPublisher(publisher);
        currentBook.setPublishDate(publishDate);
        currentBook.setIsbn(isbn);
        currentBook.setDescription(description);
        currentBook.setImageUrl(imageUrl);
        currentBook.setCategory(category);
        currentBook.setUserId(userId);

        // UI에 먼저 반영 (하드코딩된 값 대신 실제 데이터 표시)
        bookTitleTextView.setText(emptyToDash(title));
        bookAuthorTextView.setText(emptyToDash(author));
        bookPublisherTextView.setText(emptyToDash(publisher));
        bookReleaseDateTextView.setText(emptyToDash(publishDate));
        bookGenreTextView.setText(emptyToDash(category));
        isbnTextView.setText(emptyToDash(isbn));
        
        // 설명은 비어있으면 메시지 표시
        if (description != null && !description.isEmpty()) {
            bookDescriptionTextView.setText(description);
        } else {
            bookDescriptionTextView.setText("책 소개가 없습니다.");
        }
        
        // Intent에서 추가 데이터 가져오기
        int pageCount = getIntent().getIntExtra("book_pageCount", 0);
        String tags = getIntent().getStringExtra("book_tags");
        String location = getIntent().getStringExtra("book_location");
        
        // 페이지 수 표시
        if (pageCount > 0) {
            bookPagesTextView.setText(pageCount + "P");
        } else {
            bookPagesTextView.setText("-");
        }
        
        // 태그 표시 (카테고리 기반 자동 생성)
        if (tags != null && !tags.isEmpty()) {
            tagsTextView.setText(tags);
        } else if (category != null && !category.isEmpty()) {
            // 카테고리를 기반으로 태그 자동 생성
            String autoTags = generateTagsFromCategory(category, title);
            tagsTextView.setText(autoTags);
            currentBook.setTags(autoTags);
        } else {
            tagsTextView.setText("");
        }
        
        // 위치 표시
        if (location != null && !location.isEmpty()) {
            locationTextView.setText(location);
            currentBook.setLocation(location);
        } else {
            locationTextView.setText("위치를 설정해주세요");
        }
        
        // currentBook에 페이지 수 저장
        currentBook.setPageCount(pageCount);
        
        // 이미지 로딩 (URL 우선, 없으면 리소스 ID)
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

        // ISBN13으로 API 호출하여 추가 정보 가져오기
        String isbn13 = getIntent().getStringExtra("isbn13");
        if (isbn13 == null || isbn13.trim().isEmpty()) {
            // ISBN이 없으면 API 호출 생략
            return;
        }
        currentBook.setIsbn(isbn13);

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

                Book ui = BookApiMapper.toUi(apiBook);

                if (ui.getIsbn() != null) {
                    currentBook.setIsbn(ui.getIsbn());
                    currentBook.setTitle(ui.getTitle());
                    currentBook.setAuthor(ui.getAuthor());
                    currentBook.setPublisher(ui.getPublisher());
                    currentBook.setPublishDate(ui.getPublishDate());
                    currentBook.setIsbn(ui.getIsbn());
                    currentBook.setDescription(ui.getDescription());
                    currentBook.setImageUrl(ui.getImageUrl());
                }

                // View에 바인딩 (tags, location 제외 모두 API값으로)
                bookTitleTextView.setText(emptyToDash(ui.getTitle()));          // 제목
                bookAuthorTextView.setText(emptyToDash(ui.getAuthor()));        // 저자
                bookPublisherTextView.setText(emptyToDash(ui.getPublisher()));  // 출판사
                bookReleaseDateTextView.setText(emptyToDash(ui.getPublishDate()));
                bookGenreTextView.setText(emptyToDash(ui.getCategory()));

                // 페이지 수: data4library 상세에 페이지 정보가 없으면 "-" 처리 미작성.
                bookPagesTextView.setText(emptyToDash("-"));

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
                    // API에서 가져온 이미지 URL로 업데이트
                    Glide.with(BookDetailActivity.this)
                            .load(ui.getImageUrl())
                            .placeholder(R.drawable.book_placeholder_background)
                            .error(R.drawable.book_placeholder_background)
                            .into(bookCoverImageView);
                }
                
                // currentBook 객체도 API 데이터로 업데이트
                currentBook.setTitle(ui.getTitle());
                currentBook.setAuthor(ui.getAuthor());
                currentBook.setPublisher(ui.getPublisher());
                currentBook.setPublishDate(ui.getPublishDate());
                currentBook.setCategory(ui.getCategory());
                currentBook.setDescription(rawDescription);
                if (ui.getImageUrl() != null) {
                    currentBook.setImageUrl(ui.getImageUrl());
                }

                ifAlreadyBook(currentBook.getIsbn(), userId);
            } // onResponse

            @Override
            public void onFailure(Call<BookDetailEnvelope> call, Throwable t) {
                if (call.isCanceled()) return;
                // API 호출 실패시 메시지만 표시, Intent 데이터는 그대로 유지
                Log.e("BookDetailActivity", "API 호출 실패: " + t.getMessage());
            }
        });
    }
    
    // 카테고리와 제목을 기반으로 태그 자동 생성
    private String generateTagsFromCategory(String category, String title) {
        StringBuilder tags = new StringBuilder();
        
        // 카테고리가 있으면 추가
        if (category != null && !category.isEmpty()) {
            // 카테고리를 "/" 또는 ">" 로 분리 (예: "국내도서>인문" 또는 "국내도서/인문")
            String[] categories = category.split("[>/]");
            for (String cat : categories) {
                String trimmedCat = cat.trim();
                if (!trimmedCat.isEmpty()) {
                    if (tags.length() > 0) tags.append(" ");
                    tags.append("#").append(trimmedCat);
                }
            }
        }
        
        // 제목에서 키워드 추출 (간단한 버전)
        if (title != null && !title.isEmpty()) {
            // 제목이 짧으면 그대로 태그로
            if (title.length() <= 10) {
                if (tags.length() > 0) tags.append(" ");
                tags.append("#").append(title.replaceAll("\\s+", ""));
            }
        }
        
        // 기본 태그 추가
        if (tags.length() == 0) {
            tags.append("#도서");
        }
        
        return tags.toString();
    }
    
    // 위치 수정 다이얼로그 표시
    private void showLocationEditDialog() {
        // EditText 생성
        EditText editText = new EditText(this);
        editText.setHint("예: 책장 A-3, 2층 서재 등");
        
        // 현재 위치가 있으면 미리 표시
        if (currentBook != null && currentBook.getLocation() != null 
                && !currentBook.getLocation().equals("위치를 설정해주세요")) {
            editText.setText(currentBook.getLocation());
        }
        
        // AlertDialog 생성
        new AlertDialog.Builder(this)
                .setTitle("도서 위치 입력")
                .setMessage("이 책의 위치를 입력해주세요.")
                .setView(editText)
                .setPositiveButton("저장", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newLocation = editText.getText().toString().trim();
                        if (!newLocation.isEmpty()) {
                            // UI 업데이트
                            locationTextView.setText(newLocation);
                            
                            // currentBook 객체 업데이트
                            if (currentBook != null) {
                                currentBook.setLocation(newLocation);
                                
                                // Firestore에 저장 (선택적)
                                bookService.saveOrUpdateBook(currentBook, userId);
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
