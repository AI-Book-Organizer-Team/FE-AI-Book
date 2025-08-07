package com.example.fe_ai_book;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
    private EditText locationEditText;

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
        locationEditText = findViewById(R.id.locationEditText);
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
                locationEditText.requestFocus();
                Toast.makeText(BookDetailActivity.this, "위치를 수정할 수 있습니다", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadBookData() {
        // Intent에서 전달받은 데이터로 책 정보를 설정
        String title = getIntent().getStringExtra("book_title");
        String author = getIntent().getStringExtra("book_author");
        int imageResId = getIntent().getIntExtra("book_image", R.drawable.ic_launcher_background);

        if (title != null && author != null) {
            bookTitleTextView.setText(title);
            bookAuthorTextView.setText(author);
            bookCoverImageView.setImageResource(imageResId);
        }

        // 나머지 정보는 기본값으로 설정 (실제 앱에서는 API나 데이터베이스에서 가져와야 함)
        bookDescriptionTextView.setText("이 책은 시민과 국가의 관계, 즉 시민의 자유가 어디까지 보장되고, 국가의 간섭은 어디까지 미칠 수 있는지를 다루고 있다.");
        bookPublisherTextView.setText("현대지성");
        bookGenreTextView.setText("인문");
        bookPagesTextView.setText("256P");
        bookReleaseDateTextView.setText("18.06.01");
        isbnTextView.setText("9791187142447");
        tagsTextView.setText("#국내도서 #인문 #철학 #자유 개념 #자유론");
        locationEditText.setHint("책장 3번째 칸 왼쪽에 있는 책");
    }
}
