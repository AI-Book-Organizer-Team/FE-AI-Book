package com.example.fe_ai_book.service;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.fe_ai_book.model.Book;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class BookFirebaseService {
    private static final String TAG = "BookFirebaseService";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // 저장 or 수정
    public void saveOrUpdateBook(@NonNull Book book, @NonNull String userId) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", book.getId());
        data.put("title", book.getTitle());
        data.put("author", book.getAuthor());
        data.put("publisher", book.getPublisher());
        data.put("publishDate", book.getPublishDate());
        data.put("isbn", book.getIsbn());

        // ✅ category null 방지
        data.put("category",
                (book.getCategory() != null && !book.getCategory().isEmpty())
                        ? book.getCategory()
                        : "기타"
        );

        data.put("description", book.getDescription());
        data.put("imageUrl", book.getImageUrl());
        data.put("notes", book.getNotes());
        data.put("pageCount", book.getPageCount());
        data.put("rating", book.getRating());
        data.put("userId", userId);

        if (book.getCreatedAt() == null) {
            data.put("createdAt", FieldValue.serverTimestamp());
        }
        data.put("updatedAt", FieldValue.serverTimestamp());

        db.collection("users")
                .document(userId)
                .collection("books")
                .document(book.getIsbn())
                .set(data)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Book saved/updated: " + book.getTitle()))
                .addOnFailureListener(e -> Log.w(TAG, "Error saving/updating book", e));
    }


    // 삭제
    public void deleteBook(@NonNull String isbn, @NonNull String userId) {
        db.collection("users")
                .document(userId)
                .collection("books")
                .document(isbn)
                .delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Book deleted: " + isbn))
                .addOnFailureListener(e -> Log.w(TAG, "Error deleting book", e));
    }
}
