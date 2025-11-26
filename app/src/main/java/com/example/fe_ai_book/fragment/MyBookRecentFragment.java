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

public class MyBookRecentFragment extends Fragment {

    private RecyclerView recyclerView;
    private MyBookAdapter adapter;
    private List<Book> bookList = new ArrayList<>();
    private List<Book> originalList = new ArrayList<>();
    private FirebaseFirestore db;
    private String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_mybook_recent, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_recent);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MyBookAdapter(getContext(), bookList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Log.e("MyBook", "로그인 안 됨 → 책 불러오기 불가");
            return view;
        }

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        loadRecentBooks();

        return view;
    }

    private void loadRecentBooks() {
        if (userId == null) {
            Log.e("MyBook", "로그인 안 됨 → 책 불러오기 불가");
            return;
        }

        // createdAt이 문자열이라 orderBy 사용 시 오류 발생 → 우선 orderBy 제거
        db.collection("users")
                .document(userId)
                .collection("books")
                //.orderBy("createdAt", Query.Direction.DESCENDING) // ❌ 문자열이면 사용 불가
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    bookList.clear();
                    originalList.clear();

                    if (querySnapshot.isEmpty()) {
                        Log.d("MyBook", "책이 없습니다.");
                    } else {
                        for (DocumentSnapshot doc : querySnapshot) {
                            try {
                                Book book = doc.toObject(Book.class);
                                if (book != null) {
                                    bookList.add(book);
                                    Log.d("MyBook", "불러온 책 = " + book.getTitle());
                                }
                            } catch (Exception e) {
                                Log.e("MyBook", "Book 매핑 실패: " + doc.getData(), e);
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("MyBook", "책 불러오기 실패", e));
    }

    public void search(String query) {
        if (originalList.isEmpty()) return;

        if (query == null || query.trim().isEmpty()) {
            // 검색어 없으면 원본 복구
            bookList.clear();
            bookList.addAll(originalList);
            adapter.notifyDataSetChanged();
            return;
        }

        String q = query.toLowerCase().trim();
        List<Book> filtered = new ArrayList<>();

        for (Book b : originalList) {
            if (safe(b.getTitle()).contains(q) ||
                    safe(b.getAuthor()).contains(q) ||
                    safe(b.getPublisher()).contains(q) ||
                    safe(b.getIsbn()).contains(q)) {

                filtered.add(b);
            }
        }

        bookList.clear();
        bookList.addAll(filtered);
        adapter.notifyDataSetChanged();
    }

    // NPE 방지
    private String safe(String s) {
        return s == null ? "" : s.toLowerCase();
    }
}
