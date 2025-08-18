package com.example.fe_ai_book;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fe_ai_book.adapter.MyBookAdapter;
import com.example.fe_ai_book.model.Book;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class MyBookRecentActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MyBookAdapter adapter;
    private List<Book> bookList;
    private FirebaseFirestore db;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mybook_recent);

        // 뒤로가기 버튼
        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        // RecyclerView 초기화
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        bookList = new ArrayList<>();
        adapter = new MyBookAdapter(this, bookList);
        recyclerView.setAdapter(adapter);

        // Firebase 초기화
        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // 책 불러오기
        loadRecentBooks();
    }

    private void loadRecentBooks() {
        db.collection("users")
                .document(userId)
                .collection("books")
                .orderBy("createdAt", Query.Direction.DESCENDING) // 최신순 정렬
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    bookList.clear();
                    for (DocumentSnapshot doc : querySnapshot) {
                        Book book = doc.toObject(Book.class);
                        if (book != null) bookList.add(book);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error getting books", e);
                    Toast.makeText(this, "책 불러오기 실패", Toast.LENGTH_SHORT).show();
                });
    }
}
