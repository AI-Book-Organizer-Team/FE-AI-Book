package com.example.fe_ai_book;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fe_ai_book.adapter.HomeBookAdapter;
import com.example.fe_ai_book.model.Book;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_home);

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

        RadioGroup kategorielist_btn = findViewById(R.id.kategorielist_btn);

        kategorielist_btn.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checked) {
                bookList2.clear();

                if (checked == R.id.kategorielist_btn1) {
                    for (int i=1; i<=5; i++){
                        bookList2.add(new Book("도서 제목 " + i, "작가" +i,R.drawable.sample_cover_backducksu));
                    }
                } else if (checked == R.id.kategorielist_btn2) {
                    for (int i=2; i<=6; i++){
                        bookList2.add(new Book("도서 제목 " + i, "작가" +i,R.drawable.sample_cover_backducksu));
                    }
                }else if (checked == R.id.kategorielist_btn3) {
                    for (int i=3; i<=7; i++){
                        bookList2.add(new Book("도서 제목 " + i, "작가" +i,R.drawable.sample_cover_backducksu));
                    }
                }else if (checked == R.id.kategorielist_btn4) {
                    for (int i=4; i<=8; i++){
                        bookList2.add(new Book("도서 제목 " + i, "작가" +i,R.drawable.sample_cover_backducksu));
                    }
                }else {
                    for (int i=5; i<=9; i++){
                        bookList2.add(new Book("도서 제목 " + i, "작가" +i,R.drawable.sample_cover_backducksu));
                    }
                }
                adapter2.notifyDataSetChanged();
            }
        });


    }
}
