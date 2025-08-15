package com.example.fe_ai_book.dao;

import android.util.Log;

import com.example.fe_ai_book.entity.UserActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserActivityCloudDao {
    private static final String TAG = "UserActivityCloudDao";
    private static final String COLLECTION_NAME = "userActivities";
    
    private FirebaseFirestore db;
    
    public UserActivityCloudDao() {
        db = FirebaseFirestore.getInstance();
    }
    
    public interface UserActivityCallback {
        void onSuccess();
        void onError(String error);
    }
    
    public interface GetActivitiesCallback {
        void onSuccess(List<UserActivity> activities);
        void onError(String error);
    }
    
    public void saveUserActivity(UserActivity activity, UserActivityCallback callback) {
        Map<String, Object> activityMap = new HashMap<>();
        activityMap.put("activityId", activity.activityId);
        activityMap.put("userId", activity.userId);
        activityMap.put("activityType", activity.activityType);
        activityMap.put("bookId", activity.bookId);
        activityMap.put("bookTitle", activity.bookTitle);
        activityMap.put("searchQuery", activity.searchQuery);
        activityMap.put("rating", activity.rating);
        activityMap.put("note", activity.note);
        activityMap.put("timestamp", activity.timestamp);
        activityMap.put("duration", activity.duration);
        
        db.collection(COLLECTION_NAME)
                .document(activity.activityId)
                .set(activityMap)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User activity saved to cloud successfully");
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error saving user activity to cloud", e);
                    callback.onError(e.getMessage());
                });
    }
    
    public void getUserActivities(String userId, GetActivitiesCallback callback) {
        db.collection(COLLECTION_NAME)
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<UserActivity> activities = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        UserActivity activity = documentToActivity(document);
                        if (activity != null) {
                            activities.add(activity);
                        }
                    }
                    Log.d(TAG, "Retrieved " + activities.size() + " activities from cloud");
                    callback.onSuccess(activities);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error retrieving user activities from cloud", e);
                    callback.onError(e.getMessage());
                });
    }
    
    public void getUserActivitiesByType(String userId, String activityType, GetActivitiesCallback callback) {
        db.collection(COLLECTION_NAME)
                .whereEqualTo("userId", userId)
                .whereEqualTo("activityType", activityType)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<UserActivity> activities = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        UserActivity activity = documentToActivity(document);
                        if (activity != null) {
                            activities.add(activity);
                        }
                    }
                    Log.d(TAG, "Retrieved " + activities.size() + " activities of type " + activityType + " from cloud");
                    callback.onSuccess(activities);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error retrieving user activities by type from cloud", e);
                    callback.onError(e.getMessage());
                });
    }
    
    public void deleteUserActivities(String userId, UserActivityCallback callback) {
        db.collection(COLLECTION_NAME)
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        document.getReference().delete();
                    }
                    Log.d(TAG, "User activities deleted from cloud successfully");
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error deleting user activities from cloud", e);
                    callback.onError(e.getMessage());
                });
    }
    
    private UserActivity documentToActivity(QueryDocumentSnapshot document) {
        try {
            UserActivity activity = new UserActivity();
            activity.activityId = document.getString("activityId");
            activity.userId = document.getString("userId");
            activity.activityType = document.getString("activityType");
            activity.bookId = document.getString("bookId");
            activity.bookTitle = document.getString("bookTitle");
            activity.searchQuery = document.getString("searchQuery");
            activity.rating = document.getDouble("rating").floatValue();
            activity.note = document.getString("note");
            activity.timestamp = document.getLong("timestamp");
            activity.duration = document.getLong("duration");
            return activity;
        } catch (Exception e) {
            Log.e(TAG, "Error converting document to UserActivity", e);
            return null;
        }
    }
}
