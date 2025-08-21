package com.example.fe_ai_book.service;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.fe_ai_book.model.Book;
import com.example.fe_ai_book.model.BookDetailEnvelope;
import com.example.fe_ai_book.mapper.BookApiMapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Firebase Firestore에서 사용자 도서 데이터를 조회하는 서비스
 * AI 추천 시스템에서 사용
 */
public class FirebaseBookQueryService {
    private static final String TAG = "FirebaseBookQuery";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    
    public interface BookQueryCallback {
        void onBooksLoaded(List<Book> books);
        void onError(String error);
    }
    
    /**
     * 특정 사용자의 모든 도서 조회
     */
    public void getUserBooks(@NonNull String userId, BookQueryCallback callback) {
        Log.d(TAG, "Querying books for user: " + userId);
        
        db.collection("users")
                .document(userId)
                .collection("books")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Book> books = new ArrayList<>();
                            QuerySnapshot result = task.getResult();
                            
                            if (result != null) {
                                for (QueryDocumentSnapshot document : result) {
                                    try {
                                        Book book = document.toObject(Book.class);
                                        if (book != null) {
                                            books.add(book);
                                        }
                                    } catch (Exception e) {
                                        Log.w(TAG, "Error converting document to Book: " + e.getMessage());
                                    }
                                }
                            }
                            
                            Log.d(TAG, "Found " + books.size() + " books for user: " + userId);
                            callback.onBooksLoaded(books);
                        } else {
                            String error = "Failed to query books: " + 
                                (task.getException() != null ? task.getException().getMessage() : "Unknown error");
                            Log.e(TAG, error);
                            callback.onError(error);
                        }
                    }
                });
    }
    
    /**
     * 특정 사용자의 특정 장르 도서 조회
     */
    public void getUserBooksByCategory(@NonNull String userId, @NonNull String category, BookQueryCallback callback) {
        Log.d(TAG, "Querying books for user: " + userId + ", category: " + category);
        
        db.collection("users")
                .document(userId)
                .collection("books")
                .whereEqualTo("category", category)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Book> books = new ArrayList<>();
                        QuerySnapshot result = task.getResult();
                        
                        if (result != null) {
                            for (QueryDocumentSnapshot document : result) {
                                try {
                                    Book book = document.toObject(Book.class);
                                    if (book != null) {
                                        books.add(book);
                                    }
                                } catch (Exception e) {
                                    Log.w(TAG, "Error converting document to Book: " + e.getMessage());
                                }
                            }
                        }
                        
                        Log.d(TAG, "Found " + books.size() + " books for category: " + category);
                        callback.onBooksLoaded(books);
                    } else {
                        String error = "Failed to query books by category: " + 
                            (task.getException() != null ? task.getException().getMessage() : "Unknown error");
                        Log.e(TAG, error);
                        callback.onError(error);
                    }
                });
    }
    
    /**
     * 특정 사용자의 특정 작가 도서 조회
     */
    public void getUserBooksByAuthor(@NonNull String userId, @NonNull String author, BookQueryCallback callback) {
        Log.d(TAG, "Querying books for user: " + userId + ", author: " + author);
        
        db.collection("users")
                .document(userId)
                .collection("books")
                .whereEqualTo("author", author)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Book> books = new ArrayList<>();
                        QuerySnapshot result = task.getResult();
                        
                        if (result != null) {
                            for (QueryDocumentSnapshot document : result) {
                                try {
                                    Book book = document.toObject(Book.class);
                                    if (book != null) {
                                        books.add(book);
                                    }
                                } catch (Exception e) {
                                    Log.w(TAG, "Error converting document to Book: " + e.getMessage());
                                }
                            }
                        }
                        
                        Log.d(TAG, "Found " + books.size() + " books for author: " + author);
                        callback.onBooksLoaded(books);
                    } else {
                        String error = "Failed to query books by author: " + 
                            (task.getException() != null ? task.getException().getMessage() : "Unknown error");
                        Log.e(TAG, error);
                        callback.onError(error);
                    }
                });
    }
    
    /**
     * 특정 사용자의 특정 출판사 도서 조회
     */
    public void getUserBooksByPublisher(@NonNull String userId, @NonNull String publisher, BookQueryCallback callback) {
        Log.d(TAG, "Querying books for user: " + userId + ", publisher: " + publisher);
        
        db.collection("users")
                .document(userId)
                .collection("books")
                .whereEqualTo("publisher", publisher)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Book> books = new ArrayList<>();
                        QuerySnapshot result = task.getResult();
                        
                        if (result != null) {
                            for (QueryDocumentSnapshot document : result) {
                                try {
                                    Book book = document.toObject(Book.class);
                                    if (book != null) {
                                        books.add(book);
                                    }
                                } catch (Exception e) {
                                    Log.w(TAG, "Error converting document to Book: " + e.getMessage());
                                }
                            }
                        }
                        
                        Log.d(TAG, "Found " + books.size() + " books for publisher: " + publisher);
                        callback.onBooksLoaded(books);
                    } else {
                        String error = "Failed to query books by publisher: " + 
                            (task.getException() != null ? task.getException().getMessage() : "Unknown error");
                        Log.e(TAG, error);
                        callback.onError(error);
                    }
                });
    }
    
    /**
     * 인기 도서 조회 (전체 사용자 대상)
     */
    public void getPopularBooks(BookQueryCallback callback) {
        Log.d(TAG, "Querying popular books from Data4Library API");
        
        // 정보나루 API에서 실제 인기도서 조회
        try {
            // 최근 30일간의 인기도서 조회
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String endDate = sdf.format(cal.getTime());
            
            cal.add(Calendar.DAY_OF_MONTH, -30);
            String startDate = sdf.format(cal.getTime());
            
            String authKey = com.example.fe_ai_book.BuildConfig.DATA4LIB_AUTH_KEY;
            
            DataLibraryApi api = ApiClient.get();
            Call<BookDetailEnvelope> call = api.getPopularBooks(authKey, startDate, endDate, 1, 20, "json");
            
            call.enqueue(new Callback<BookDetailEnvelope>() {
                @Override
                public void onResponse(Call<BookDetailEnvelope> call, Response<BookDetailEnvelope> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            List<Book> books = BookApiMapper.mapToBookList(response.body());
                            Log.d(TAG, "Successfully fetched " + books.size() + " popular books from API");
                            callback.onBooksLoaded(books);
                        } catch (Exception e) {
                            Log.e(TAG, "Error mapping API response to books", e);
                            // 실패 시 폴백
                            getFallbackPopularBooks(callback);
                        }
                    } else {
                        Log.w(TAG, "API call unsuccessful, using fallback");
                        // 실패 시 폴백
                        getFallbackPopularBooks(callback);
                    }
                }
                
                @Override
                public void onFailure(Call<BookDetailEnvelope> call, Throwable t) {
                    Log.e(TAG, "API call failed: " + t.getMessage());
                    // 실패 시 폴백
                    getFallbackPopularBooks(callback);
                }
            });
            
        } catch (Exception e) {
            Log.e(TAG, "Error setting up API call", e);
            getFallbackPopularBooks(callback);
        }
    }
    
    /**
     * API 실패 시 폴백 인기도서 (전역 컶렉션 조회)
     */
    private void getFallbackPopularBooks(BookQueryCallback callback) {
        Log.d(TAG, "Using fallback: querying from global book collections");
        
        // 다른 사용자들의 도서 중에서 인기도서 조회 (범위 확대)
        db.collectionGroup("books")
                .limit(50) // 더 많은 도서 가져오기
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Book> books = new ArrayList<>();
                        QuerySnapshot result = task.getResult();
                        
                        if (result != null) {
                            for (QueryDocumentSnapshot document : result) {
                                try {
                                    Book book = document.toObject(Book.class);
                                    if (book != null) {
                                        books.add(book);
                                    }
                                } catch (Exception e) {
                                    Log.w(TAG, "Error converting document to Book: " + e.getMessage());
                                }
                            }
                        }
                        
                        Log.d(TAG, "Found " + books.size() + " fallback popular books");
                        callback.onBooksLoaded(books);
                    } else {
                        String error = "Failed to query fallback popular books: " + 
                            (task.getException() != null ? task.getException().getMessage() : "Unknown error");
                        Log.e(TAG, error);
                        callback.onError(error);
                    }
                });
    }
    
    /**
     * 장르별 도서 검색 (정보나루 API 활용)
     */
    public void searchBooksByGenre(@NonNull String genre, BookQueryCallback callback) {
        Log.d(TAG, "Searching books by genre: " + genre + " using Data4Library API");
        
        try {
            String authKey = com.example.fe_ai_book.BuildConfig.DATA4LIB_AUTH_KEY;
            DataLibraryApi api = ApiClient.get();
            
            // 장르 관련 키워드로 검색
            Call<BookDetailEnvelope> call = api.searchBooks(authKey, genre, null, null, 1, 20, "json");
            
            call.enqueue(new Callback<BookDetailEnvelope>() {
                @Override
                public void onResponse(Call<BookDetailEnvelope> call, Response<BookDetailEnvelope> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            List<Book> books = BookApiMapper.mapToBookList(response.body());
                            Log.d(TAG, "Successfully fetched " + books.size() + " books for genre: " + genre);
                            callback.onBooksLoaded(books);
                        } catch (Exception e) {
                            Log.e(TAG, "Error mapping genre search response", e);
                            callback.onBooksLoaded(new ArrayList<>());
                        }
                    } else {
                        Log.w(TAG, "Genre search API call unsuccessful");
                        callback.onBooksLoaded(new ArrayList<>());
                    }
                }
                
                @Override
                public void onFailure(Call<BookDetailEnvelope> call, Throwable t) {
                    Log.e(TAG, "Genre search API call failed: " + t.getMessage());
                    callback.onBooksLoaded(new ArrayList<>());
                }
            });
            
        } catch (Exception e) {
            Log.e(TAG, "Error setting up genre search API call", e);
            callback.onBooksLoaded(new ArrayList<>());
        }
    }
    
    /**
     * 작가별 도서 검색 (정보나루 API 활용)
     */
    public void searchBooksByAuthor(@NonNull String author, BookQueryCallback callback) {
        Log.d(TAG, "Searching books by author: " + author + " using Data4Library API");
        
        try {
            String authKey = com.example.fe_ai_book.BuildConfig.DATA4LIB_AUTH_KEY;
            DataLibraryApi api = ApiClient.get();
            
            Call<BookDetailEnvelope> call = api.searchBooks(authKey, null, author, null, 1, 20, "json");
            
            call.enqueue(new Callback<BookDetailEnvelope>() {
                @Override
                public void onResponse(Call<BookDetailEnvelope> call, Response<BookDetailEnvelope> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            List<Book> books = BookApiMapper.mapToBookList(response.body());
                            Log.d(TAG, "Successfully fetched " + books.size() + " books for author: " + author);
                            callback.onBooksLoaded(books);
                        } catch (Exception e) {
                            Log.e(TAG, "Error mapping author search response", e);
                            callback.onBooksLoaded(new ArrayList<>());
                        }
                    } else {
                        Log.w(TAG, "Author search API call unsuccessful");
                        callback.onBooksLoaded(new ArrayList<>());
                    }
                }
                
                @Override
                public void onFailure(Call<BookDetailEnvelope> call, Throwable t) {
                    Log.e(TAG, "Author search API call failed: " + t.getMessage());
                    callback.onBooksLoaded(new ArrayList<>());
                }
            });
            
        } catch (Exception e) {
            Log.e(TAG, "Error setting up author search API call", e);
            callback.onBooksLoaded(new ArrayList<>());
        }
    }
    
    /**
     * 출판사별 도서 검색 (정보나루 API 활용)
     */
    public void searchBooksByPublisher(@NonNull String publisher, BookQueryCallback callback) {
        Log.d(TAG, "Searching books by publisher: " + publisher + " using Data4Library API");
        
        try {
            String authKey = com.example.fe_ai_book.BuildConfig.DATA4LIB_AUTH_KEY;
            DataLibraryApi api = ApiClient.get();
            
            Call<BookDetailEnvelope> call = api.searchBooks(authKey, null, null, publisher, 1, 20, "json");
            
            call.enqueue(new Callback<BookDetailEnvelope>() {
                @Override
                public void onResponse(Call<BookDetailEnvelope> call, Response<BookDetailEnvelope> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            List<Book> books = BookApiMapper.mapToBookList(response.body());
                            Log.d(TAG, "Successfully fetched " + books.size() + " books for publisher: " + publisher);
                            callback.onBooksLoaded(books);
                        } catch (Exception e) {
                            Log.e(TAG, "Error mapping publisher search response", e);
                            callback.onBooksLoaded(new ArrayList<>());
                        }
                    } else {
                        Log.w(TAG, "Publisher search API call unsuccessful");
                        callback.onBooksLoaded(new ArrayList<>());
                    }
                }
                
                @Override
                public void onFailure(Call<BookDetailEnvelope> call, Throwable t) {
                    Log.e(TAG, "Publisher search API call failed: " + t.getMessage());
                    callback.onBooksLoaded(new ArrayList<>());
                }
            });
            
        } catch (Exception e) {
            Log.e(TAG, "Error setting up publisher search API call", e);
            callback.onBooksLoaded(new ArrayList<>());
        }
    }
}
