package com.example.fe_ai_book.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_activities")
public class UserActivity {
    @PrimaryKey
    @NonNull
    public String activityId;
    
    public String userId;
    public String activityType; // "search", "read", "rating", "favorite", "bookmark"
    public String bookId; // Related book (if applicable)
    public String bookTitle;
    public String searchQuery; // For search activities
    public float rating; // For rating activities (0.0 - 5.0)
    public String note; // User notes or comments
    public long timestamp;
    public long duration; // Activity duration in milliseconds (for reading time)
    
    // Default constructor for Room
    public UserActivity() {}
    
    public UserActivity(String userId, String activityType, String bookId, 
                       String bookTitle, String searchQuery, float rating, 
                       String note, long duration) {
        this.activityId = generateId();
        this.userId = userId;
        this.activityType = activityType;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.searchQuery = searchQuery;
        this.rating = rating;
        this.note = note;
        this.duration = duration;
        this.timestamp = System.currentTimeMillis();
    }
    
    // Factory methods for different activity types
    public static UserActivity createSearchActivity(String userId, String searchQuery) {
        return new UserActivity(userId, "search", null, null, searchQuery, 0.0f, null, 0);
    }
    
    public static UserActivity createReadActivity(String userId, String bookId, String bookTitle, long duration) {
        return new UserActivity(userId, "read", bookId, bookTitle, null, 0.0f, null, duration);
    }
    
    public static UserActivity createRatingActivity(String userId, String bookId, String bookTitle, float rating, String note) {
        return new UserActivity(userId, "rating", bookId, bookTitle, null, rating, note, 0);
    }
    
    public static UserActivity createFavoriteActivity(String userId, String bookId, String bookTitle) {
        return new UserActivity(userId, "favorite", bookId, bookTitle, null, 0.0f, null, 0);
    }
    
    private String generateId() {
        return System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
    }
}
