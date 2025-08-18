package com.example.fe_ai_book;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fe_ai_book.model.Book;
import com.example.fe_ai_book.service.BookFirebaseService;
import com.google.firebase.auth.FirebaseAuth;

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
