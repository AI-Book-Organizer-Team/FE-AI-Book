package com.example.fe_ai_book.dao;

import android.util.Log;

import com.example.fe_ai_book.entity.UserSettings;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserSettingsCloudDao {
    private static final String TAG = "UserSettingsCloudDao";
    private static final String COLLECTION_NAME = "userSettings";
    
    private FirebaseFirestore db;
    
    public UserSettingsCloudDao() {
        db = FirebaseFirestore.getInstance();
    }
    
    public interface UserSettingsCallback {
        void onSuccess();
        void onError(String error);
    }
    
    public interface GetUserSettingsCallback {
        void onSuccess(UserSettings userSettings);
        void onError(String error);
    }
    
    public void saveUserSettings(UserSettings userSettings, UserSettingsCallback callback) {
        Map<String, Object> settingsMap = new HashMap<>();
        settingsMap.put("userId", userSettings.userId);
        settingsMap.put("theme", userSettings.theme);
        settingsMap.put("language", userSettings.language);
        settingsMap.put("pushNotifications", userSettings.pushNotifications);
        settingsMap.put("emailNotifications", userSettings.emailNotifications);
        settingsMap.put("autoSync", userSettings.autoSync);
        settingsMap.put("fontSize", userSettings.fontSize);
        settingsMap.put("lastUpdated", userSettings.lastUpdated);
        
        db.collection(COLLECTION_NAME)
                .document(userSettings.userId)
                .set(settingsMap)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User settings saved to cloud successfully");
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error saving user settings to cloud", e);
                    callback.onError(e.getMessage());
                });
    }
    
    public void getUserSettings(String userId, GetUserSettingsCallback callback) {
        db.collection(COLLECTION_NAME)
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        UserSettings userSettings = new UserSettings();
                        userSettings.userId = documentSnapshot.getString("userId");
                        userSettings.theme = documentSnapshot.getString("theme");
                        userSettings.language = documentSnapshot.getString("language");
                        userSettings.pushNotifications = documentSnapshot.getBoolean("pushNotifications");
                        userSettings.emailNotifications = documentSnapshot.getBoolean("emailNotifications");
                        userSettings.autoSync = documentSnapshot.getBoolean("autoSync");
                        userSettings.fontSize = documentSnapshot.getLong("fontSize").intValue();
                        userSettings.lastUpdated = documentSnapshot.getLong("lastUpdated");
                        
                        Log.d(TAG, "User settings retrieved from cloud successfully");
                        callback.onSuccess(userSettings);
                    } else {
                        Log.d(TAG, "No user settings found in cloud");
                        callback.onError("No settings found");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error retrieving user settings from cloud", e);
                    callback.onError(e.getMessage());
                });
    }
    
    public void deleteUserSettings(String userId, UserSettingsCallback callback) {
        db.collection(COLLECTION_NAME)
                .document(userId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User settings deleted from cloud successfully");
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error deleting user settings from cloud", e);
                    callback.onError(e.getMessage());
                });
    }
}
