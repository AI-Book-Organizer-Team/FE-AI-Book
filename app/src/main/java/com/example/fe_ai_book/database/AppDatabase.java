package com.example.fe_ai_book.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.fe_ai_book.dao.BookDao;
import com.example.fe_ai_book.dao.UserSettingsDao;
import com.example.fe_ai_book.dao.UserActivityDao;
import com.example.fe_ai_book.entity.BookEntity;
import com.example.fe_ai_book.entity.UserSettings;
import com.example.fe_ai_book.entity.UserActivity;

@Database(
    entities = {BookEntity.class, UserSettings.class, UserActivity.class},
    version = 2,
    exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    
    private static final String DATABASE_NAME = "fe_ai_book_database";
    private static volatile AppDatabase INSTANCE;
    
    // DAO methods
    public abstract BookDao bookDao();
    public abstract UserSettingsDao userSettingsDao();
    public abstract UserActivityDao userActivityDao();
    
    // 싱글톤 패턴으로 데이터베이스 인스턴스 생성
    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            DATABASE_NAME
                    )
                    .fallbackToDestructiveMigration() // 마이그레이션 실패시 데이터베이스 재생성
                    .build();
                }
            }
        }
        return INSTANCE;
    }
    
    // 데이터베이스 연결 해제 (테스트용)
    public static void closeDatabase() {
        if (INSTANCE != null) {
            INSTANCE.close();
            INSTANCE = null;
        }
    }
    
    // 데이터베이스 생성 시 실행되는 콜백
    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(SupportSQLiteDatabase db) {
            super.onCreate(db);
            // 데이터베이스가 처음 생성될 때 실행할 작업
        }
        
        @Override
        public void onOpen(SupportSQLiteDatabase db) {
            super.onOpen(db);
            // 데이터베이스가 열릴 때마다 실행할 작업
        }
    };
    
    // 향후 데이터베이스 스키마 변경시 사용할 마이그레이션
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // 예: 새로운 컬럼 추가
            // database.execSQL("ALTER TABLE books ADD COLUMN new_column TEXT");
        }
    };
}
