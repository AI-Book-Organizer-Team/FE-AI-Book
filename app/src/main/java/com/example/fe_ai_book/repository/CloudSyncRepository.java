package com.example.fe_ai_book.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.fe_ai_book.dao.BookDao;
import com.example.fe_ai_book.dao.BookCloudDao;
import com.example.fe_ai_book.dao.UserSettingsDao;
import com.example.fe_ai_book.dao.UserSettingsCloudDao;
import com.example.fe_ai_book.dao.UserActivityDao;
import com.example.fe_ai_book.dao.UserActivityCloudDao;
import com.example.fe_ai_book.database.AppDatabase;
import com.example.fe_ai_book.entity.BookEntity;
import com.example.fe_ai_book.entity.UserSettings;
import com.example.fe_ai_book.entity.UserActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CloudSyncRepository {
    private static final String TAG = "CloudSyncRepository";
    private static final String PREFS_NAME = "cloud_sync_prefs";
    private static final String LAST_SYNC_TIME = "last_sync_time";
    private static final String SYNC_IN_PROGRESS = "sync_in_progress";
    
    // Local DAOs
    public BookDao bookDao;
    public UserSettingsDao userSettingsDao;
    public UserActivityDao userActivityDao;
    
    // Cloud DAOs
    private BookCloudDao bookCloudDao;
    private UserSettingsCloudDao userSettingsCloudDao;
    private UserActivityCloudDao userActivityCloudDao;
    
    // Other dependencies
    private SharedPreferences sharedPreferences;
    private ExecutorService executorService;
    private FirebaseAuth firebaseAuth;
    
    public CloudSyncRepository(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        this.bookDao = database.bookDao();
        this.userSettingsDao = database.userSettingsDao();
        this.userActivityDao = database.userActivityDao();
        
        this.bookCloudDao = new BookCloudDao();
        this.userSettingsCloudDao = new UserSettingsCloudDao();
        this.userActivityCloudDao = new UserActivityCloudDao();
        
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.executorService = Executors.newFixedThreadPool(3);
        this.firebaseAuth = FirebaseAuth.getInstance();
    }
    
    public interface SyncCallback {
        void onSyncComplete();
        void onSyncError(String error);
        void onSyncProgress(String message);
    }
    
    // Full synchronization - pull data from cloud and push local changes
    public void performFullSync(SyncCallback callback) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            callback.onSyncError("User not authenticated");
            return;
        }
        
        String userId = currentUser.getUid();
        setSyncInProgress(true);
        callback.onSyncProgress("Starting synchronization...");
        
        executorService.execute(() -> {
            try {
                // Step 1: Pull user settings from cloud
                callback.onSyncProgress("Syncing user settings...");
                syncUserSettingsFromCloud(userId, new SyncCallback() {
                    @Override
                    public void onSyncComplete() {
                        // Step 2: Pull user activities from cloud
                        callback.onSyncProgress("Syncing user activities...");
                        syncUserActivitiesFromCloud(userId, new SyncCallback() {
                            @Override
                            public void onSyncComplete() {
                                // Step 3: Pull books from cloud
                                callback.onSyncProgress("Syncing books...");
                                syncBooksFromCloud(userId, new SyncCallback() {
                                    @Override
                                    public void onSyncComplete() {
                                        // Step 4: Push any local changes to cloud
                                        callback.onSyncProgress("Uploading local changes...");
                                        pushLocalChangesToCloud(userId, new SyncCallback() {
                                            @Override
                                            public void onSyncComplete() {
                                                updateLastSyncTime();
                                                setSyncInProgress(false);
                                                callback.onSyncProgress("Synchronization completed!");
                                                callback.onSyncComplete();
                                            }
                                            
                                            @Override
                                            public void onSyncError(String error) {
                                                setSyncInProgress(false);
                                                callback.onSyncError("Error uploading changes: " + error);
                                            }
                                            
                                            @Override
                                            public void onSyncProgress(String message) {
                                                callback.onSyncProgress(message);
                                            }
                                        });
                                    }
                                    
                                    @Override
                                    public void onSyncError(String error) {
                                        setSyncInProgress(false);
                                        callback.onSyncError("Error syncing books: " + error);
                                    }
                                    
                                    @Override
                                    public void onSyncProgress(String message) {
                                        callback.onSyncProgress(message);
                                    }
                                });
                            }
                            
                            @Override
                            public void onSyncError(String error) {
                                setSyncInProgress(false);
                                callback.onSyncError("Error syncing activities: " + error);
                            }
                            
                            @Override
                            public void onSyncProgress(String message) {
                                callback.onSyncProgress(message);
                            }
                        });
                    }
                    
                    @Override
                    public void onSyncError(String error) {
                        setSyncInProgress(false);
                        callback.onSyncError("Error syncing settings: " + error);
                    }
                    
                    @Override
                    public void onSyncProgress(String message) {
                        callback.onSyncProgress(message);
                    }
                });
                
            } catch (Exception e) {
                setSyncInProgress(false);
                Log.e(TAG, "Error during full sync", e);
                callback.onSyncError("Sync failed: " + e.getMessage());
            }
        });
    }
    
    // Sync user settings from cloud to local
    private void syncUserSettingsFromCloud(String userId, SyncCallback callback) {
        userSettingsCloudDao.getUserSettings(userId, new UserSettingsCloudDao.GetUserSettingsCallback() {
            @Override
            public void onSuccess(UserSettings cloudSettings) {
                executorService.execute(() -> {
                    try {
                        UserSettings localSettings = userSettingsDao.getUserSettings(userId);
                        if (localSettings == null || cloudSettings.lastUpdated > localSettings.lastUpdated) {
                            userSettingsDao.insertOrUpdate(cloudSettings);
                            Log.d(TAG, "User settings updated from cloud");
                        }
                        callback.onSyncComplete();
                    } catch (Exception e) {
                        Log.e(TAG, "Error saving settings from cloud", e);
                        callback.onSyncError(e.getMessage());
                    }
                });
            }
            
            @Override
            public void onError(String error) {
                if (error.equals("No settings found")) {
                    // Create default settings if none exist in cloud
                    UserSettings defaultSettings = UserSettings.createDefault(userId);
                    userSettingsCloudDao.saveUserSettings(defaultSettings, new UserSettingsCloudDao.UserSettingsCallback() {
                        @Override
                        public void onSuccess() {
                            executorService.execute(() -> {
                                userSettingsDao.insertOrUpdate(defaultSettings);
                                callback.onSyncComplete();
                            });
                        }
                        
                        @Override
                        public void onError(String error) {
                            callback.onSyncError(error);
                        }
                    });
                } else {
                    callback.onSyncError(error);
                }
            }
        });
    }
    
    // Sync user activities from cloud to local
    private void syncUserActivitiesFromCloud(String userId, SyncCallback callback) {
        userActivityCloudDao.getUserActivities(userId, new UserActivityCloudDao.GetActivitiesCallback() {
            @Override
            public void onSuccess(List<UserActivity> cloudActivities) {
                executorService.execute(() -> {
                    try {
                        List<UserActivity> localActivities = userActivityDao.getUserActivities(userId);
                        
                        // Simple merge: add cloud activities that don't exist locally
                        for (UserActivity cloudActivity : cloudActivities) {
                            boolean exists = localActivities.stream()
                                    .anyMatch(local -> local.activityId.equals(cloudActivity.activityId));
                            if (!exists) {
                                userActivityDao.insert(cloudActivity);
                            }
                        }
                        
                        Log.d(TAG, "User activities updated from cloud");
                        callback.onSyncComplete();
                    } catch (Exception e) {
                        Log.e(TAG, "Error saving activities from cloud", e);
                        callback.onSyncError(e.getMessage());
                    }
                });
            }
            
            @Override
            public void onError(String error) {
                Log.d(TAG, "No activities found in cloud or error: " + error);
                callback.onSyncComplete(); // Continue sync even if no activities
            }
        });
    }
    
    // Sync books from cloud to local
    private void syncBooksFromCloud(String userId, SyncCallback callback) {
        bookCloudDao.getAllBooks(new BookCloudDao.BooksCallback() {
            @Override
            public void onSuccess(List<BookEntity> cloudBooks) {
                executorService.execute(() -> {
                    try {
                        List<BookEntity> localBooks = bookDao.getAllBooks();
                        
                        // Merge logic: update local books with newer cloud data
                        for (BookEntity cloudBook : cloudBooks) {
                            BookEntity localBook = findBookById(localBooks, cloudBook.getIsbn());
                            
                            if (localBook == null) {
                                // New book from cloud - add to local
                                cloudBook.setSyncedToCloud(true);
                                bookDao.insertBook(cloudBook);
                                Log.d(TAG, "New book added from cloud: " + cloudBook.getTitle());
                            } else if (cloudBook.getUpdatedAt() > localBook.getUpdatedAt()) {
                                // Cloud version is newer - update local
                                cloudBook.setSyncedToCloud(true);
                                bookDao.updateBook(cloudBook);
                                Log.d(TAG, "Book updated from cloud: " + cloudBook.getTitle());
                            }
                        }
                        
                        Log.d(TAG, "Books synchronized from cloud: " + cloudBooks.size() + " books processed");
                        callback.onSyncComplete();
                    } catch (Exception e) {
                        Log.e(TAG, "Error syncing books from cloud", e);
                        callback.onSyncError(e.getMessage());
                    }
                });
            }
            
            @Override
            public void onFailure(String error) {
                Log.d(TAG, "No books found in cloud or error: " + error);
                callback.onSyncComplete(); // Continue sync even if no books
            }
        });
    }
    
    // Helper method to find book by ID in local list
    private BookEntity findBookById(List<BookEntity> books, String bookId) {
        for (BookEntity book : books) {
            if (book.getIsbn().equals(bookId)) {
                return book;
            }
        }
        return null;
    }
    
    // Push any local changes to cloud
    private void pushLocalChangesToCloud(String userId, SyncCallback callback) {
        executorService.execute(() -> {
            try {
                // Push local books that are not synced to cloud
                List<BookEntity> unsyncedBooks = bookDao.getUnsyncedBooks();
                for (BookEntity book : unsyncedBooks) {
                    bookCloudDao.saveBook(book, new BookCloudDao.OperationCallback() {
                        @Override
                        public void onSuccess() {
                            // Mark book as synced in local database
                            executorService.execute(() -> {
                                try {
                                    book.setSyncedToCloud(true);
                                    bookDao.updateBook(book);
                                    Log.d(TAG, "Book synced to cloud: " + book.getTitle());
                                } catch (Exception e) {
                                    Log.e(TAG, "Error updating book sync status", e);
                                }
                            });
                        }
                        
                        @Override
                        public void onFailure(String error) {
                            Log.e(TAG, "Error pushing book to cloud: " + error);
                        }
                    });
                }
                
                // Push local user settings to cloud
                UserSettings localSettings = userSettingsDao.getUserSettings(userId);
                if (localSettings != null) {
                    userSettingsCloudDao.saveUserSettings(localSettings, new UserSettingsCloudDao.UserSettingsCallback() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "Local settings pushed to cloud");
                        }
                        
                        @Override
                        public void onError(String error) {
                            Log.e(TAG, "Error pushing settings to cloud: " + error);
                        }
                    });
                }
                
                // Push local activities to cloud (recent ones only)
                List<UserActivity> recentActivities = userActivityDao.getRecentActivities(userId, 100);
                for (UserActivity activity : recentActivities) {
                    userActivityCloudDao.saveUserActivity(activity, new UserActivityCloudDao.UserActivityCallback() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "Activity pushed to cloud: " + activity.activityType);
                        }
                        
                        @Override
                        public void onError(String error) {
                            Log.e(TAG, "Error pushing activity to cloud: " + error);
                        }
                    });
                }
                
                callback.onSyncComplete();
            } catch (Exception e) {
                Log.e(TAG, "Error pushing local changes", e);
                callback.onSyncError(e.getMessage());
            }
        });
    }
    
    // Utility methods for sync state management
    public boolean isSyncInProgress() {
        return sharedPreferences.getBoolean(SYNC_IN_PROGRESS, false);
    }
    
    private void setSyncInProgress(boolean inProgress) {
        sharedPreferences.edit().putBoolean(SYNC_IN_PROGRESS, inProgress).apply();
    }
    
    public long getLastSyncTime() {
        return sharedPreferences.getLong(LAST_SYNC_TIME, 0);
    }
    
    private void updateLastSyncTime() {
        sharedPreferences.edit().putLong(LAST_SYNC_TIME, System.currentTimeMillis()).apply();
    }
    
    public String getLastSyncTimeString() {
        long lastSync = getLastSyncTime();
        if (lastSync == 0) {
            return "Never synced";
        }
        return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
                .format(new java.util.Date(lastSync));
    }
}
