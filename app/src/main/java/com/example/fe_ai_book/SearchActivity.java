package com.example.fe_ai_book;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fe_ai_book.adapter.BookListAdapter;
import com.example.fe_ai_book.model.Book;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private EditText editTextSearchQuery;
    private ImageButton buttonSearch;
    private RecyclerView recyclerViewSearchResults;

    private BookListAdapter bookListAdapter;
    private List<Book> bookList = new ArrayList<>();
    private List<Book> allBooks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        editTextSearchQuery = findViewById(R.id.editTextSearchQuery);
        buttonSearch = findViewById(R.id.buttonSearch);
        recyclerViewSearchResults = findViewById(R.id.recyclerViewSearchResults);

        // 더미 데이터 초기화
        /*initDummyData();*/

        recyclerViewSearchResults.setLayoutManager(new LinearLayoutManager(this));
        bookListAdapter = new BookListAdapter(bookList);
        recyclerViewSearchResults.setAdapter(bookListAdapter);

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = editTextSearchQuery.getText().toString().trim();
                if (query.isEmpty()) {
                    Toast.makeText(SearchActivity.this, "검색어를 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    performSearch(query);
                }
            }
        });
    }

/*    private void initDummyData() {
        allBooks.add(new Book("미움받을 용기", "기시미 이치로", "인플루엔셜", "아들러 심리학을 바탕으로 한 자기계발서", "https://example.com/courage.jpg"));
        allBooks.add(new Book("82년생 김지영", "조남주", "민음사", "한국 여성의 삶을 그린 소설", "https://example.com/kimjiyoung.jpg"));
        allBooks.add(new Book("사피엔스", "유발 하라리", "김영사", "인류 역사에 대한 통찰", "https://example.com/sapiens.jpg"));
        allBooks.add(new Book("데미안", "헤르만 헤세", "민음사", "성장소설의 고전", "https://example.com/demian.jpg"));
        allBooks.add(new Book("자바의 정석", "남궁성", "도우출판", "자바 프로그래밍 입문서", "https://example.com/java.jpg"));
        allBooks.add(new Book("해리포터와 마법사의 돌", "J.K. 롤링", "문학수첩", "마법 세계 판타지 소설", "https://example.com/harry.jpg"));
        allBooks.add(new Book("돈의 속성", "김승호", "스노우폭스북스", "부의 원리에 대한 실용서", "https://example.com/money.jpg"));
        allBooks.add(new Book("알고리즘 문제해결전략", "구종만", "인사이트", "프로그래밍 대회 준비서", "https://example.com/algorithm.jpg"));
    }*/

    private void performSearch(String query) {
        Log.d("SearchActivity", "Search started with query: " + query);
        Log.d("SearchActivity", "Total books in allBooks: " + allBooks.size());
        
        bookList.clear();
        for (Book book : allBooks) {
            Log.d("SearchActivity", "Checking book: " + book.getTitle());
            if (book.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                book.getAuthor().toLowerCase().contains(query.toLowerCase())) {
                bookList.add(book);
                Log.d("SearchActivity", "Match found: " + book.getTitle());
            }
        }
        
        Log.d("SearchActivity", "Search results count: " + bookList.size());
        
        if (bookList.isEmpty()) {
            Toast.makeText(SearchActivity.this, "검색 결과가 없습니다. Query: " + query, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(SearchActivity.this, bookList.size() + "개 결과를 찾았습니다.", Toast.LENGTH_SHORT).show();
        }
        bookListAdapter.notifyDataSetChanged();
    }
}