package com.example.fe_ai_book;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fe_ai_book.adapter.BookCategoryAdapter;
import com.example.fe_ai_book.model.Book;
import com.example.fe_ai_book.model.BookCategory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyBookCategoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BookCategoryAdapter adapter;
    private List<BookCategory> categoryList;

    private FirebaseFirestore db;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mybook_category);

        recyclerView = findViewById(R.id.recycler_category);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        categoryList = new ArrayList<>();
        adapter = new BookCategoryAdapter(this, categoryList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        loadBooksGroupedByCategory();
    }

    private void loadBooksGroupedByCategory() {
        db.collection("users")
                .document(userId)
                .collection("books")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    // 카테고리별로 Book 묶기
                    Map<String, List<Book>> categoryMap = new HashMap<>();

                    for (DocumentSnapshot doc : querySnapshot) {
                        Book book = doc.toObject(Book.class);
                        if (book != null) {
                            String category = book.getCategory() != null ? book.getCategory() : "기타";
                            if (!categoryMap.containsKey(category)) {
                                categoryMap.put(category, new ArrayList<>());
                            }
                            categoryMap.get(category).add(book);
                        }
                    }

                    // categoryMap → categoryList 변환
                    categoryList.clear();
                    for (Map.Entry<String, List<Book>> entry : categoryMap.entrySet()) {
                        categoryList.add(new BookCategory(entry.getKey(), entry.getValue()));
                    }

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error getting category books", e);
                    Toast.makeText(this, "카테고리별 책 불러오기 실패", Toast.LENGTH_SHORT).show();
                });
    }
}
