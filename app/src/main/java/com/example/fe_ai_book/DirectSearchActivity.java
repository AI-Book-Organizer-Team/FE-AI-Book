package com.example.fe_ai_book;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
<<<<<<< HEAD
import android.widget.CheckBox;
=======
>>>>>>> 5e7144d9bf40bbc30208eed98208c31d0daffa14
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fe_ai_book.adapter.DirectSearchBookAdapter;
import com.example.fe_ai_book.model.Book;
<<<<<<< HEAD
=======
import com.example.fe_ai_book.model.BookDetailEnvelope;
import com.example.fe_ai_book.service.ApiClient;
import com.example.fe_ai_book.service.DataLibraryApi;
>>>>>>> 5e7144d9bf40bbc30208eed98208c31d0daffa14

import java.util.ArrayList;
import java.util.List;

<<<<<<< HEAD
=======
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

>>>>>>> 5e7144d9bf40bbc30208eed98208c31d0daffa14
public class DirectSearchActivity extends AppCompatActivity {

    private EditText editTextDirectSearch;
    private ImageButton buttonDirectSearch;
    private RecyclerView recyclerViewDirectSearchResults;
    private Button btnAddBook;
    private ImageView btnBack;

    private DirectSearchBookAdapter adapter;
    private List<Book> searchResults;
    private List<Book> selectedBooks;
<<<<<<< HEAD
=======
    private Call<BookDetailEnvelope> inFlight;
>>>>>>> 5e7144d9bf40bbc30208eed98208c31d0daffa14

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_search);

        initializeViews();
        setupRecyclerView();
        setupListeners();
        
        // 임시 테스트 데이터 추가
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
    }

    private void setupListeners() {
        // 뒤로가기 버튼
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 검색 버튼
        buttonDirectSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSearch();
            }
        });

        // 검색창 텍스트 변경 리스너
        editTextDirectSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().isEmpty()) {
                    clearSearchResults();
                }
            }
        });

        // 추가하기 버튼
        btnAddBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSelectedBooks();
            }
        });
    }

    private void performSearch() {
        String query = editTextDirectSearch.getText().toString().trim();
<<<<<<< HEAD
        
        if (query.isEmpty()) {
            Toast.makeText(this, "검색어를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: 실제 API 호출 로직을 여기에 구현
        // 현재는 샘플 데이터를 필터링하여 표시
        filterSampleData(query);
        
        Toast.makeText(this, "\"" + query + "\" 검색 중...", Toast.LENGTH_SHORT).show();
    }

    private void filterSampleData(String query) {
        List<Book> filteredResults = new ArrayList<>();
        
        // 샘플 데이터에서 검색어와 일치하는 항목 찾기
        for (Book book : getSampleBooks()) {
            if (book.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                book.getAuthor().toLowerCase().contains(query.toLowerCase()) ||
                book.getIsbn().toLowerCase().contains(query.toLowerCase())) {
                filteredResults.add(book);
            }
        }
        
        searchResults.clear();
        searchResults.addAll(filteredResults);
        adapter.notifyDataSetChanged();
        
        selectedBooks.clear();
        updateAddButtonState();
=======

        if (query.isEmpty()) {
            Toast.makeText(this, "isbn13을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        loadBook(BuildConfig.DATA4LIB_AUTH_KEY, query);
        Toast.makeText(this, "\"" + query + "\" 검색 중...", Toast.LENGTH_SHORT).show();
    }

    private void loadBook(String authKey, String isbn13) {
        DataLibraryApi api = ApiClient.get();
        inFlight = api.getBookDetail(authKey, isbn13, "Y", "age", "json");

        inFlight.enqueue(new Callback<BookDetailEnvelope>() {
            @Override
            public void onResponse(Call<BookDetailEnvelope> call,
                                   Response<BookDetailEnvelope> res) {
                if (!res.isSuccessful() || res.body()==null || res.body().response==null) {
                    Toast.makeText(DirectSearchActivity.this, "응답 오류: " + res.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
                BookDetailEnvelope.Inner r = res.body().response;

                // 서버가 보낸 에러 문자열 우선 처리
                if (r.error != null && !r.error.isEmpty()) {
                    Toast.makeText(DirectSearchActivity.this, r.error, Toast.LENGTH_SHORT).show();
                    return;
                }

                // detail 배열 확인
                if (r.detail == null || r.detail.isEmpty() || r.detail.get(0).book == null) {
                    Toast.makeText(DirectSearchActivity.this, "도서 상세가 없습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // API → 기존 Book으로 변환 후 목록에 반영
                BookDetailEnvelope.Book apiBook = r.detail.get(0).book;
                Book uiBook = com.example.fe_ai_book.mapper.BookApiMapper.toUi(apiBook);

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
    }

    @Override protected void onDestroy() {
        if (inFlight != null) inFlight.cancel(); // 생명주기에서 취소(메모리 누수/콜백 크래시 방지)
        super.onDestroy();
>>>>>>> 5e7144d9bf40bbc30208eed98208c31d0daffa14
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

        // TODO: 실제 책 추가 로직 구현
        Toast.makeText(this, selectedBooks.size() + "권의 책이 추가되었습니다.", Toast.LENGTH_SHORT).show();
        
        // 선택된 책들을 결과로 반환하고 액티비티 종료
        finish();
    }

    private void updateAddButtonState() {
        btnAddBook.setEnabled(!selectedBooks.isEmpty());
        btnAddBook.setText("추가하기 (" + selectedBooks.size() + ")");
    }

    // 임시 샘플 데이터 로드
    private void loadSampleData() {
        // 초기에는 빈 리스트로 시작
        searchResults.clear();
        adapter.notifyDataSetChanged();
        updateAddButtonState();
    }

<<<<<<< HEAD
    // 샘플 데이터 생성
    private List<Book> getSampleBooks() {
        List<Book> sampleBooks = new ArrayList<>();
        
        Book book1 = new Book();
        book1.setTitle("블루온 검은 피");
        book1.setAuthor("하원");
        book1.setPublishDate("14.04.28");
        book1.setPublisher("민음사");
        book1.setIsbn("9788932468905");
        sampleBooks.add(book1);

        Book book2 = new Book();
        book2.setTitle("당신의 여행 노래가 되기");
        book2.setAuthor("하원");
        book2.setPublishDate("20.06.17");
        book2.setPublisher("문학과지성사");
        book2.setIsbn("9788932037479");
        sampleBooks.add(book2);

        Book book3 = new Book();
        book3.setTitle("오십 미터");
        book3.setAuthor("하원");
        book3.setPublishDate("16.02.31");
        book3.setPublisher("문학과지성사");
        book3.setIsbn("9788932028415");
        sampleBooks.add(book3);

        Book book4 = new Book();
        book4.setTitle("내가 원하는 전사");
        book4.setAuthor("하원");
        book4.setPublishDate("12.06.04");
        book4.setPublisher("문학과지성사");
        book4.setIsbn("9788932023007");
        sampleBooks.add(book4);
        
        return sampleBooks;
    }
=======
>>>>>>> 5e7144d9bf40bbc30208eed98208c31d0daffa14
}
