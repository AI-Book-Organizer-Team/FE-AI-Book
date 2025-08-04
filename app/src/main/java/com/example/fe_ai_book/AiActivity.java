package com.example.fe_ai_book;

import android.os.Bundle;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fe_ai_book.adapter.HomeBookAdapter;
import com.example.fe_ai_book.model.Book;

import java.util.ArrayList;

public class AiActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai);

        RecyclerView recycler_view1 = findViewById(R.id.recycler_view1);
        RecyclerView recycler_view2 = findViewById(R.id.recycler_view2);


        ArrayList<Book> bookList1 = new ArrayList<>();
        ArrayList<Book> bookList2 = new ArrayList<>();

        for (int i=1; i<=5; i++){
            bookList1.add(new Book("도서 제목 " + i, "작가" +i,R.drawable.sample_cover_backducksu));
            bookList2.add(new Book("도서 제목 " + i, "작가" +i,R.drawable.sample_cover_backducksu));
        }

        // 어댑터 설정
        HomeBookAdapter adapter1 = new HomeBookAdapter(this, bookList1);
        HomeBookAdapter adapter2 = new HomeBookAdapter(this, bookList2);

        recycler_view1.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL,false));
        recycler_view2.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL,false));

        // 리사이클뷰 설정
        recycler_view1.setAdapter(adapter1);
        recycler_view2.setAdapter(adapter2);


    }
}
