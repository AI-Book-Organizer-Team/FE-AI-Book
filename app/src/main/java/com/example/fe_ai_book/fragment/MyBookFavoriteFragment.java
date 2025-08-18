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
import com.example.fe_ai_book.adapter.MyBookAdapter;
import com.example.fe_ai_book.model.Book;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MyBookFavoriteFragment extends Fragment {
    private RecyclerView recyclerView;
    private MyBookAdapter adapter;
    private List<Book> bookList = new ArrayList<>();
    private FirebaseFirestore db;
    private String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mybook_favorite, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_favorite);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MyBookAdapter(getContext(), bookList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        loadFavoriteBooks();

        return view;
    }

    private void loadFavoriteBooks() {
        db.collection("users")
                .document(userId)
                .collection("books")
                .whereEqualTo("favorite", true)  // Firestore 필드 조건
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    bookList.clear();
                    for (DocumentSnapshot doc : querySnapshot) {
                        Book book = doc.toObject(Book.class);
                        if (book != null) bookList.add(book);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error getting books", e));
    }
}
