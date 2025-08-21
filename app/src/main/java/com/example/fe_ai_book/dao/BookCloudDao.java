package com.example.fe_ai_book.dao;

import android.util.Log;
import com.example.fe_ai_book.entity.BookEntity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookCloudDao {
    private static final String TAG = "BookCloudDao";
    private static final String COLLECTION_USERS = "users";
    private static final String SUBCOLLECTION_BOOKS = "books";
    
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    
    public BookCloudDao() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }
    
    // Callback interfaces
    public interface BookCallback {
        void onSuccess(BookEntity book);
        void onFailure(String error);
    }
    
    public interface BooksCallback {
        void onSuccess(List<BookEntity> books);
        void onFailure(String error);
    }
    
    public interface OperationCallback {
        void onSuccess();
        void onFailure(String error);
    }
    
    // Get current user ID for data isolation
    private String getCurrentUserId() {
        FirebaseUser user = auth.getCurrentUser();
        return user != null ? user.getUid() : "anonymous";
    }
    
    // Convert BookEntity to Firestore Map
    private Map<String, Object> bookEntityToMap(BookEntity book) {
        Map<String, Object> bookMap = new HashMap<>();
//        bookMap.put("id", book.getId());
        bookMap.put("title", book.getTitle());
        bookMap.put("author", book.getAuthor());
        bookMap.put("publisher", book.getPublisher());
        bookMap.put("publishDate", book.getPublishDate());
        bookMap.put("isbn", book.getIsbn());
        bookMap.put("description", book.getDescription());
        bookMap.put("imageUrl", book.getImageUrl());
        bookMap.put("category", book.getCategory());
        bookMap.put("pageCount", book.getPageCount());
        bookMap.put("rating", book.getRating());
        bookMap.put("notes", book.getNotes());
        bookMap.put("createdAt", book.getCreatedAt());
        bookMap.put("updatedAt", book.getUpdatedAt());
        return bookMap;
    }
    
    // Convert Firestore DocumentSnapshot to BookEntity
    private BookEntity documentToBookEntity(DocumentSnapshot document) {
        BookEntity book = new BookEntity();
//        book.setId(document.getString("id"));
        book.setTitle(document.getString("title"));
        book.setAuthor(document.getString("author"));
        book.setPublisher(document.getString("publisher"));
        book.setPublishDate(document.getString("publishDate"));
        book.setIsbn(document.getString("isbn"));
        book.setDescription(document.getString("description"));
        book.setImageUrl(document.getString("imageUrl"));
        book.setCategory(document.getString("category"));
        
        // Handle nullable fields
        Long pageCount = document.getLong("pageCount");
        book.setPageCount(pageCount != null ? pageCount.intValue() : null);
        
        book.setRating(document.getDouble("rating"));
        book.setNotes(document.getString("notes"));
        book.setCreatedAt(document.getLong("createdAt"));
        book.setUpdatedAt(document.getLong("updatedAt"));
        book.setSyncedToCloud(true); // From cloud, so it's synced
        
        return book;
    }
    
    // Save book to Firebase Firestore
    public void saveBook(BookEntity book, OperationCallback callback) {
        String userId = getCurrentUserId();
        String bookId = userId + "_" + book.getIsbn(); // User-specific document ID
        
        Map<String, Object> bookMap = bookEntityToMap(book);
        
        db.collection(COLLECTION_USERS)
            .document(userId)
            .collection(SUBCOLLECTION_BOOKS)
            .document(bookId)
            .set(bookMap)
            .addOnSuccessListener(aVoid -> {
                Log.d(TAG, "Book saved to Firestore subcollection: " + book.getTitle());
                callback.onSuccess();
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Failed to save book to Firestore subcollection", e);
                callback.onFailure("Firestore save failed: " + e.getMessage());
            });
    }
    
    // Get all user's books from Firestore
    public void getAllBooks(BooksCallback callback) {
        String userId = getCurrentUserId();
        
        db.collection(COLLECTION_USERS)
            .document(userId)
            .collection(SUBCOLLECTION_BOOKS)
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    QuerySnapshot result = task.getResult();
                    List<BookEntity> books = new ArrayList<>();
                    
                    if (result != null) {
                        for (DocumentSnapshot document : result) {
                            BookEntity book = documentToBookEntity(document);
                            books.add(book);
                        }
                    }
                    
                    Log.d(TAG, "Loaded " + books.size() + " books from Firestore subcollection");
                    callback.onSuccess(books);
                } else {
                    Log.e(TAG, "Failed to load books from Firestore subcollection", task.getException());
                    callback.onFailure("Failed to load books: " + task.getException().getMessage());
                }
            });
    }
    
    // Get specific book by ID
    public void getBook(String bookId, BookCallback callback) {
        String userId = getCurrentUserId();
        
        db.collection(COLLECTION_USERS)
            .document(userId)
            .collection(SUBCOLLECTION_BOOKS)
            .document(bookId)
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        BookEntity book = documentToBookEntity(document);
                        Log.d(TAG, "Book found in Firestore subcollection: " + book.getTitle());
                        callback.onSuccess(book);
                    } else {
                        Log.d(TAG, "Book not found in Firestore subcollection: " + bookId);
                        callback.onFailure("Book not found");
                    }
                } else {
                    Log.e(TAG, "Failed to get book from Firestore subcollection", task.getException());
                    callback.onFailure("Failed to get book: " + task.getException().getMessage());
                }
            });
    }
    
    // Update book in Firestore
    public void updateBook(BookEntity book, OperationCallback callback) {
        String userId = getCurrentUserId();
        String bookId = userId + "_" + book.getIsbn();
        
        Map<String, Object> bookMap = bookEntityToMap(book);
        
        db.collection(COLLECTION_USERS)
            .document(userId)
            .collection(SUBCOLLECTION_BOOKS)
            .document(bookId)
            .update(bookMap)
            .addOnSuccessListener(aVoid -> {
                Log.d(TAG, "Book updated in Firestore subcollection: " + book.getTitle());
                callback.onSuccess();
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Failed to update book in Firestore subcollection", e);
                callback.onFailure("Firestore update failed: " + e.getMessage());
            });
    }
    
    // Delete book from Firestore (subcollection 구조: users/{userId}/books/{bookId})
    public void deleteBook(String bookId, OperationCallback callback) {
        String userId = getCurrentUserId();
        
        db.collection(COLLECTION_USERS)
            .document(userId)
            .collection(SUBCOLLECTION_BOOKS)
            .document(bookId)
            .delete()
            .addOnSuccessListener(aVoid -> {
                Log.d(TAG, "Book deleted from Firestore subcollection: " + bookId);
                callback.onSuccess();
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Failed to delete book from Firestore subcollection", e);
                callback.onFailure("Firestore delete failed: " + e.getMessage());
            });
    }
}
