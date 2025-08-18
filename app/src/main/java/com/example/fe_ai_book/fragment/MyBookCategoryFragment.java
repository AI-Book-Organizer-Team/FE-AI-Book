package com.example.fe_ai_book.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fe_ai_book.R;
import com.example.fe_ai_book.adapter.BookCategoryAdapter;
import com.example.fe_ai_book.model.Book;
import com.example.fe_ai_book.model.BookCategory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.fe_ai_book.adapter.MyBookAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyBookCategoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private MyBookAdapter adapter;
    private List<Book> bookList = new ArrayList<>();
    private FirebaseFirestore db;
    private String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mybook_category, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_category);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MyBookAdapter(getContext(), bookList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        loadCategoryBooks();

        return view;
    }

    private void loadCategoryBooks() {
        db.collection("users")
                .document(userId)
                .collection("books")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    Map<String, List<Book>> grouped = new HashMap<>();

                    for (DocumentSnapshot doc : querySnapshot) {
                        Book book = doc.toObject(Book.class);
                        if (book != null) {
                            // 카테고리 문자열에서 대주제만 추출
                            String cat;
                            if (book.getCategory() != null && !book.getCategory().isEmpty()) {
                                // "사회과학 > 사회학, 사회문제 > 사회학" → "사회과학"
                                cat = book.getCategory().split(">")[0].trim();
                            } else {
                                cat = "기타";
                            }

                            if (!grouped.containsKey(cat)) {
                                grouped.put(cat, new ArrayList<>());
                            }
                            grouped.get(cat).add(book);
                        }
                    }

                    List<BookCategory> categoryList = new ArrayList<>();
                    for (Map.Entry<String, List<Book>> entry : grouped.entrySet()) {
                        categoryList.add(new BookCategory(entry.getKey(), entry.getValue()));
                    }

                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerView.setAdapter(new BookCategoryAdapter(getContext(), categoryList));
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error getting books", e));
    }



}

