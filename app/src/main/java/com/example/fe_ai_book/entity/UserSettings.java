package com.example.fe_ai_book.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_settings")
public class UserSettings {
    @PrimaryKey
    @NonNull
    public String userId;
    
    public String theme; // "light", "dark", "system"
    public String language; // "ko", "en"
    public boolean pushNotifications;
    public boolean emailNotifications;
    public boolean autoSync;
    public int fontSize; // 12, 14, 16, 18, 20
    public long lastUpdated;
    
    // Default constructor for Room
    public UserSettings() {}
    
    public UserSettings(String userId, String theme, String language, 
                       boolean pushNotifications, boolean emailNotifications, 
                       boolean autoSync, int fontSize) {
        this.userId = userId;
        this.theme = theme;
        this.language = language;
        this.pushNotifications = pushNotifications;
        this.emailNotifications = emailNotifications;
        this.autoSync = autoSync;
        this.fontSize = fontSize;
        this.lastUpdated = System.currentTimeMillis();
    }
    
    // Default settings
    public static UserSettings createDefault(String userId) {
        return new UserSettings(userId, "system", "ko", true, false, true, 14);
    }
    
    public void updateTimestamp() {
        this.lastUpdated = System.currentTimeMillis();
    }
}
