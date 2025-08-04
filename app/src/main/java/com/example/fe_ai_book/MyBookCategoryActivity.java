package com.example.fe_ai_book;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fe_ai_book.adapter.BookCategoryAdapter;
import com.example.fe_ai_book.model.Book;
import com.example.fe_ai_book.model.BookCategory;

import java.util.ArrayList;
import java.util.List;

public class MyBookCategoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BookCategoryAdapter adapter;
    private List<BookCategory> categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mybook_category);

        recyclerView = findViewById(R.id.recycler_category);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        categoryList = new ArrayList<>();

        List<Book> fantasyBooks = new ArrayList<>();
        fantasyBooks.add(new Book("판타지 책1", "작가A", "2025.08.01", R.drawable.sample_cover_backducksu));
        fantasyBooks.add(new Book("판타지 책2", "작가B", "2025.07.30", R.drawable.sample_cover_backducksu));

        List<Book> historyBooks = new ArrayList<>();
        historyBooks.add(new Book("역사 책1", "작가C", "2025.07.28", R.drawable.sample_cover_backducksu));
        historyBooks.add(new Book("역사 책2", "작가D", "2025.07.26", R.drawable.sample_cover_backducksu));

        List<Book> scienceBooks = new ArrayList<>();
        scienceBooks.add(new Book("과학 책1", "작가E", "2025.07.25", R.drawable.sample_cover_backducksu));
        scienceBooks.add(new Book("과학 책2", "작가F", "2025.07.22", R.drawable.sample_cover_backducksu));

        categoryList.add(new BookCategory("판타지", fantasyBooks));
        categoryList.add(new BookCategory("역사", historyBooks));
        categoryList.add(new BookCategory("과학", scienceBooks));

        adapter = new BookCategoryAdapter(this, categoryList);
        recyclerView.setAdapter(adapter);
    }
}
