package com.example.fe_ai_book.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Delete;

import com.example.fe_ai_book.entity.UserActivity;

import java.util.List;

@Dao
public interface UserActivityDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserActivity userActivity);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<UserActivity> activities);
    
    @Delete
    void delete(UserActivity userActivity);
    
    @Query("SELECT * FROM user_activities WHERE userId = :userId ORDER BY timestamp DESC")
    List<UserActivity> getUserActivities(String userId);
    
    @Query("SELECT * FROM user_activities WHERE userId = :userId AND activityType = :activityType ORDER BY timestamp DESC")
    List<UserActivity> getUserActivitiesByType(String userId, String activityType);
    
    @Query("SELECT * FROM user_activities WHERE userId = :userId AND bookId = :bookId ORDER BY timestamp DESC")
    List<UserActivity> getBookActivities(String userId, String bookId);
    
    @Query("SELECT * FROM user_activities WHERE userId = :userId ORDER BY timestamp DESC LIMIT :limit")
    List<UserActivity> getRecentActivities(String userId, int limit);
    
    @Query("DELETE FROM user_activities WHERE userId = :userId")
    void deleteUserActivities(String userId);
    
    @Query("DELETE FROM user_activities WHERE userId = :userId AND timestamp < :timestamp")
    void deleteOldActivities(String userId, long timestamp);
}
