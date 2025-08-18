package com.example.fe_ai_book.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.fe_ai_book.entity.UserSettings;

@Dao
public interface UserSettingsDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(UserSettings userSettings);
    
    @Update
    void update(UserSettings userSettings);
    
    @Query("SELECT * FROM user_settings WHERE userId = :userId")
    UserSettings getUserSettings(String userId);
    
    @Query("DELETE FROM user_settings WHERE userId = :userId")
    void deleteUserSettings(String userId);
    
    @Query("SELECT COUNT(*) FROM user_settings WHERE userId = :userId")
    int getUserSettingsCount(String userId);
}
