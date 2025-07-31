package com.example.fe_ai_book;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fe_ai_book.adapter.MyBookAdapter;
import com.example.fe_ai_book.model.Book;

import java.util.ArrayList;
import java.util.List;

public class MyBookRecentActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MyBookAdapter adapter;
    private List<Book> bookList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mybook_recent);

        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 현재 액티비티 종료 = 뒤로가기
            }
        });

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        bookList = new ArrayList<>();
        bookList.add(new Book("도서 제목 1", "작가 1", "2025.07.31", R.drawable.sample_cover_backducksu));
        bookList.add(new Book("도서 제목 2", "작가 2", "2025.07.30", R.drawable.sample_cover_backducksu));
        bookList.add(new Book("도서 제목 3", "작가 3", "2025.07.29", R.drawable.sample_cover_backducksu));
        bookList.add(new Book("도서 제목 4", "작가 4", "2025.07.28", R.drawable.sample_cover_backducksu));
        bookList.add(new Book("도서 제목 5", "작가 5", "2025.07.27", R.drawable.sample_cover_backducksu));

        adapter = new MyBookAdapter(this, bookList);
        recyclerView.setAdapter(adapter);
    }
}
