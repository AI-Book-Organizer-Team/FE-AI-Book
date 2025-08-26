package com.example.fe_ai_book;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fe_ai_book.adapter.DirectSearchBookAdapter;
import com.example.fe_ai_book.entity.BookEntity;
import com.example.fe_ai_book.mapper.BookApiMapper;
import com.example.fe_ai_book.model.Book;
import com.example.fe_ai_book.model.BookDetailEnvelope;
import com.example.fe_ai_book.model.BookSearchEnvelope;
import com.example.fe_ai_book.service.ApiClient;
import com.example.fe_ai_book.service.DataLibraryApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.fe_ai_book.service.BookFirebaseService;
import com.google.firebase.auth.FirebaseAuth;

public class DirectSearchActivity extends AppCompatActivity {

    private EditText editTextDirectSearch;
    private ImageButton buttonDirectSearch;
    private RecyclerView recyclerViewDirectSearchResults;
    private Button btnAddBook;
    private ImageView btnBack;

    private DirectSearchBookAdapter adapter;
    private List<Book> searchResults;
    private List<Book> selectedBooks;
    private Call<BookDetailEnvelope> detailInFlight;
    private Call<BookSearchEnvelope> searchInFlight;
    private BookFirebaseService bookService;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_search);

        initializeViews();
        setupRecyclerView();
        setupListeners();
        bookService = new BookFirebaseService();
        auth = FirebaseAuth.getInstance();

        // 초기 빈 리스트
        loadSampleData();
    }

    private void initializeViews() {
        editTextDirectSearch = findViewById(R.id.editTextDirectSearch);
        buttonDirectSearch = findViewById(R.id.buttonDirectSearch);
        recyclerViewDirectSearchResults = findViewById(R.id.recyclerViewDirectSearchResults);
        btnAddBook = findViewById(R.id.btn_add_book);
        btnBack = findViewById(R.id.btn_back);

        searchResults = new ArrayList<>();
        selectedBooks = new ArrayList<>();
    }

    private void setupRecyclerView() {
        adapter = new DirectSearchBookAdapter(searchResults, new DirectSearchBookAdapter.OnBookSelectListener() {
            @Override
            public void onBookSelected(Book book, boolean isSelected) {
                if (isSelected) {
                    if (!selectedBooks.contains(book)) {
                        selectedBooks.add(book);
                    }
                } else {
                    selectedBooks.remove(book);
                }
                updateAddButtonState();
            }
        });

        recyclerViewDirectSearchResults.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewDirectSearchResults.setAdapter(adapter);

        adapter.setOnItemClickListener(new DirectSearchBookAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Book book, int position, View imageView) {
                Intent i = new Intent(DirectSearchActivity.this, BookDetailActivity.class);

                // 책 데이터 전달
                i.putExtra("book_isbn", book.getIsbn());

                startActivity(i);
            }
        });
    }

    private void setupListeners() {
        // 뒤로가기 버튼
        btnBack.setOnClickListener(v -> finish());

        // 검색 버튼
        buttonDirectSearch.setOnClickListener(v -> performSearch());

        // 검색창 텍스트 변경 리스너
        editTextDirectSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                if (s.toString().trim().isEmpty()) {
                    clearSearchResults();
                }
            }
        });

        // 추가하기 버튼
        btnAddBook.setOnClickListener(v -> addSelectedBooks());
    }

    private void performSearch() {
        String query = editTextDirectSearch.getText().toString().trim();

        // API 키 체크
        if (BuildConfig.DATA4LIB_AUTH_KEY == null || BuildConfig.DATA4LIB_AUTH_KEY.isEmpty()) {
            Toast.makeText(this, "API 키가 설정되지 않았습니다. gradle.properties에 DATA4LIB_AUTH_KEY를 설정해주세요.", Toast.LENGTH_LONG).show();
            return;
        }

        loadBook(BuildConfig.DATA4LIB_AUTH_KEY, query);
        Toast.makeText(this, "\"" + query + "\" 검색 중...", Toast.LENGTH_SHORT).show();
    }

    //검색 로직

    private void loadBook(String authKey, String search ) {
        DataLibraryApi api = ApiClient.get();

        Call<BookDetailEnvelope> detailInFlight;
        if (search.matches("\\d{13}")) {
            detailInFlight = api.getBookDetail(authKey, search, "Y", "age", "json");
            detailInFlight.enqueue(new Callback<BookDetailEnvelope>() {
                @Override
                public void onResponse(Call<BookDetailEnvelope> call, Response<BookDetailEnvelope> res) {
                    if (!res.isSuccessful() || res.body()==null || res.body().response==null) {
                        Toast.makeText(DirectSearchActivity.this, "응답 오류: " + res.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    BookDetailEnvelope.Inner r = res.body().response;

                    // 서버 에러 문자열 처리
                    if (r.error != null && !r.error.isEmpty()) {
                        Toast.makeText(DirectSearchActivity.this, r.error, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // detail 배열 확인
                    if (r.detail == null || r.detail.isEmpty() || r.detail.get(0).book == null) {
                        Toast.makeText(DirectSearchActivity.this, "도서 상세가 없습니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // API → UI용 Book 변환
                    searchResults.clear();
                    for (BookDetailEnvelope.Detail d : r.detail) {
                        if (d.book != null) {
                            Book ui = com.example.fe_ai_book.mapper.BookApiMapper.toUi(d.book);
                            searchResults.add(ui);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    selectedBooks.clear();
                    updateAddButtonState();
                }

                @Override
                public void onFailure(Call<BookDetailEnvelope> call, Throwable t) {
                    if (call.isCanceled()) return;
                    Toast.makeText(DirectSearchActivity.this, "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            searchInFlight = api.searchBooks(authKey, search, "all", 1, 20);
            searchInFlight.enqueue(new Callback<BookSearchEnvelope>() {
                @Override
                public void onResponse(Call <BookSearchEnvelope> call, Response<BookSearchEnvelope> res) {
                    if (!res.isSuccessful() || res.body() == null || res.body().response == null) {
                        Toast.makeText(DirectSearchActivity.this, "응답 오류: " + res.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    BookSearchEnvelope.Inner r = res.body().response;
                    if (r.docs == null || r.docs.isEmpty()) {
                        Toast.makeText(DirectSearchActivity.this, "검색 결과 없음", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    searchResults.clear();
                    for (BookSearchEnvelope.DocItem d : r.docs) {
                        Book ui = BookApiMapper.toUi(d.doc);  // 변환
                        searchResults.add(ui);
                    }
                    adapter.notifyDataSetChanged();
                    selectedBooks.clear();
                    updateAddButtonState();
                }

                @Override
                public void onFailure(Call<BookSearchEnvelope> call, Throwable t) {
                    if (call.isCanceled()) return;
                    Toast.makeText(DirectSearchActivity.this, "오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override protected void onDestroy() {
        if (detailInFlight != null) detailInFlight.cancel(); // 생명주기에서 취소
        if (searchInFlight != null) searchInFlight.cancel(); // 생명주기에서 취소
        super.onDestroy();
    }

    private void clearSearchResults() {
        searchResults.clear();
        selectedBooks.clear();
        adapter.notifyDataSetChanged();
        updateAddButtonState();
    }

    private void addSelectedBooks() {
        if (selectedBooks.isEmpty()) {
            Toast.makeText(this, "추가할 책을 선택해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "guest";

        for (Book book : selectedBooks) {
            bookService.saveOrUpdateBook(book, userId);
        }

        Toast.makeText(this, selectedBooks.size() + "권이 내 서재에 추가되었습니다.", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void updateAddButtonState() {
        btnAddBook.setEnabled(!selectedBooks.isEmpty());
        btnAddBook.setText("추가하기 (" + selectedBooks.size() + ")");
    }

    // 초기 샘플 데이터 (현재는 빈 리스트로 시작)
    private void loadSampleData() {
        searchResults.clear();
        adapter.notifyDataSetChanged();
        updateAddButtonState();
    }
}
