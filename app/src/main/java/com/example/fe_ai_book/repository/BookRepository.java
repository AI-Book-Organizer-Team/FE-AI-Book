package com.example.fe_ai_book.repository;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.fe_ai_book.dao.BookDao;
import com.example.fe_ai_book.dao.BookCloudDao;
import com.example.fe_ai_book.database.AppDatabase;
import com.example.fe_ai_book.entity.BookEntity;

import java.util.List;
import java.util.UUID;

public class BookRepository {
    private static final String TAG = "BookRepository";
    
    private BookDao bookDao;
    private BookCloudDao cloudDao;
    
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
    
    public BookRepository(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        bookDao = database.bookDao();
        cloudDao = new BookCloudDao();
    }
    
    // Save book (Local + Cloud)
    public void saveBook(BookEntity book, OperationCallback callback) {
        // Generate new UUID if ID is empty
        if (book.getId() == null || book.getId().isEmpty()) {
            book.setId(UUID.randomUUID().toString());
        }
        
        // Step 1: Save to local DB first
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    bookDao.insertBook(book);
                    return null; // Success
                } catch (Exception e) {
                    return e.getMessage(); // Failure
                }
            }
            
            @Override
            protected void onPostExecute(String error) {
                if (error != null) {
                    Log.e(TAG, "Local save failed: " + error);
                    callback.onFailure("Local save failed: " + error);
                    return;
                }
                
                // Step 2: Save to cloud DB
                cloudDao.saveBook(book, new BookCloudDao.OperationCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "Book saved successfully (Local + Cloud): " + book.getTitle());
                        callback.onSuccess();
                    }
                    
                    @Override
                    public void onFailure(String cloudError) {
                        // Even if cloud save fails, local save succeeded
                        Log.w(TAG, "Cloud save failed (Local save succeeded): " + cloudError);
                        callback.onSuccess();
                    }
                });
            }
        }.execute();
    }
    
    // Get all books (Local first)
    public void getAllBooks(BooksCallback callback) {
        new AsyncTask<Void, Void, List<BookEntity>>() {
            @Override
            protected List<BookEntity> doInBackground(Void... voids) {
                return bookDao.getAllBooks();
            }
            
            @Override
            protected void onPostExecute(List<BookEntity> books) {
                callback.onSuccess(books);
            }
        }.execute();
    }
    
    // Get book by ID (Local first)
    public void getBookById(String bookId, BookCallback callback) {
        new AsyncTask<Void, Void, BookEntity>() {
            @Override
            protected BookEntity doInBackground(Void... voids) {
                return bookDao.getBookById(bookId);
            }
            
            @Override
            protected void onPostExecute(BookEntity book) {
                if (book != null) {
                    callback.onSuccess(book);
                } else {
                    // If not found locally, try cloud
                    cloudDao.getBook(bookId, new BookCloudDao.BookCallback() {
                        @Override
                        public void onSuccess(BookEntity book) {
                            callback.onSuccess(book);
                        }
                        
                        @Override
                        public void onFailure(String error) {
                            callback.onFailure(error);
                        }
                    });
                }
            }
        }.execute();
    }
}
