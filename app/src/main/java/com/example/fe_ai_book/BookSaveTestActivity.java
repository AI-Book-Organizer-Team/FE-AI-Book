package com.example.fe_ai_book;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fe_ai_book.entity.BookEntity;
import com.example.fe_ai_book.repository.BookRepository;

public class BookSaveTestActivity extends AppCompatActivity {
    private static final String TAG = "BookSaveTestActivity";
    
    private EditText etTitle, etAuthor, etPublisher;
    private Button btnSave, btnLoad;
    private BookRepository bookRepository;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Simple programmatic layout
        createUI();
        
        bookRepository = new BookRepository(this);
        setupListeners();
    }
    
    private void createUI() {
        // Create layout programmatically to avoid needing XML
        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50);
        
        // Title
        android.widget.TextView title = new android.widget.TextView(this);
        title.setText("Book Save Test");
        title.setTextSize(20);
        title.setPadding(0, 0, 0, 30);
        layout.addView(title);
        
        // Title input
        android.widget.TextView lblTitle = new android.widget.TextView(this);
        lblTitle.setText("Title:");
        layout.addView(lblTitle);
        
        etTitle = new EditText(this);
        etTitle.setHint("Enter book title");
        layout.addView(etTitle);
        
        // Author input  
        android.widget.TextView lblAuthor = new android.widget.TextView(this);
        lblAuthor.setText("Author:");
        lblAuthor.setPadding(0, 20, 0, 0);
        layout.addView(lblAuthor);
        
        etAuthor = new EditText(this);
        etAuthor.setHint("Enter author name");
        layout.addView(etAuthor);
        
        // Publisher input
        android.widget.TextView lblPublisher = new android.widget.TextView(this);
        lblPublisher.setText("Publisher:");
        lblPublisher.setPadding(0, 20, 0, 0);
        layout.addView(lblPublisher);
        
        etPublisher = new EditText(this);
        etPublisher.setHint("Enter publisher");
        layout.addView(etPublisher);
        
        // Buttons
        android.widget.LinearLayout buttonLayout = new android.widget.LinearLayout(this);
        buttonLayout.setOrientation(android.widget.LinearLayout.HORIZONTAL);
        buttonLayout.setPadding(0, 40, 0, 0);
        
        btnSave = new Button(this);
        btnSave.setText("Save Book");
        buttonLayout.addView(btnSave);
        
        btnLoad = new Button(this);
        btnLoad.setText("Load Books");
        btnLoad.setPadding(20, 0, 0, 0);
        buttonLayout.addView(btnLoad);
        
        layout.addView(buttonLayout);
        setContentView(layout);
    }
    
    private void setupListeners() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTestBook();
            }
        });
        
        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAllBooks();
            }
        });
    }
    
    private void saveTestBook() {
        String title = etTitle.getText().toString().trim();
        String author = etAuthor.getText().toString().trim();
        String publisher = etPublisher.getText().toString().trim();
        
        if (title.isEmpty()) {
            Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Create BookEntity
        BookEntity book = new BookEntity();
        book.setId(java.util.UUID.randomUUID().toString());
        book.setTitle(title);
        book.setAuthor(author.isEmpty() ? "Unknown Author" : author);
        book.setPublisher(publisher.isEmpty() ? "Unknown Publisher" : publisher);
        book.setCategory("Test");
        book.setDescription("Test book created manually");
        book.setIsbn("TEST-" + System.currentTimeMillis());
        book.setPublishDate("2024");
        
        // Save book
        btnSave.setEnabled(false);
        btnSave.setText("Saving...");
        
        bookRepository.saveBook(book, new BookRepository.OperationCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(BookSaveTestActivity.this, 
                            "Book saved successfully!", Toast.LENGTH_LONG).show();
                        clearInputs();
                        btnSave.setEnabled(true);
                        btnSave.setText("Save Book");
                        Log.d(TAG, "Book saved: " + title);
                    }
                });
            }
            
            @Override
            public void onFailure(String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(BookSaveTestActivity.this, 
                            "Save failed: " + error, Toast.LENGTH_LONG).show();
                        btnSave.setEnabled(true);
                        btnSave.setText("Save Book");
                        Log.e(TAG, "Save failed: " + error);
                    }
                });
            }
        });
    }
    
    private void loadAllBooks() {
        bookRepository.getAllBooks(new BookRepository.BooksCallback() {
            @Override
            public void onSuccess(java.util.List<BookEntity> books) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        StringBuilder result = new StringBuilder();
                        result.append("Found ").append(books.size()).append(" books:\n\n");
                        
                        for (BookEntity book : books) {
                            result.append("• ").append(book.getTitle());
                            if (book.getAuthor() != null && !book.getAuthor().isEmpty()) {
                                result.append(" by ").append(book.getAuthor());
                            }
                            result.append("\n");
                        }
                        
                        Toast.makeText(BookSaveTestActivity.this, 
                            result.toString(), Toast.LENGTH_LONG).show();
                        Log.d(TAG, "Loaded " + books.size() + " books");
                    }
                });
            }
            
            @Override
            public void onFailure(String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(BookSaveTestActivity.this, 
                            "Load failed: " + error, Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Load failed: " + error);
                    }
                });
            }
        });
    }
    
    private void clearInputs() {
        etTitle.setText("");
        etAuthor.setText("");
        etPublisher.setText("");
    }
}
