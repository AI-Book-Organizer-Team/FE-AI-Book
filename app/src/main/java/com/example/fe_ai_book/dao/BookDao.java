package com.example.fe_ai_book.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.fe_ai_book.entity.BookEntity;

import java.util.List;

@Dao
public interface BookDao {
    
    // 도서 삽입 (중복시 교체)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBook(BookEntity book);
    
    // 여러 도서 삽입
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBooks(List<BookEntity> books);
    
    // 도서 정보 업데이트
    @Update
    void updateBook(BookEntity book);
    
    // 도서 삭제
    @Delete
    void deleteBook(BookEntity book);
    
    // Isbn로 도서 삭제
    @Query("DELETE FROM books WHERE isbn = :bookId")
    void deleteBookById(String bookId);
    
    // 모든 도서 조회
    @Query("SELECT * FROM books ORDER BY createdAt DESC")
    List<BookEntity> getAllBooks();
    
    // Isbn로 도서 조회
    @Query("SELECT * FROM books WHERE isbn = :bookId")
    BookEntity getBookById(String bookId);
    
    // ISBN으로 도서 조회
    @Query("SELECT * FROM books WHERE isbn = :isbn")
    BookEntity getBookByIsbn(String isbn);
    
    // 제목으로 도서 검색 (부분 일치)
    @Query("SELECT * FROM books WHERE title LIKE '%' || :title || '%' ORDER BY createdAt DESC")
    List<BookEntity> searchBooksByTitle(String title);
    
    // 저자로 도서 검색 (부분 일치)
    @Query("SELECT * FROM books WHERE author LIKE '%' || :author || '%' ORDER BY createdAt DESC")
    List<BookEntity> searchBooksByAuthor(String author);
    
    // 카테고리별 도서 조회
    @Query("SELECT * FROM books WHERE category = :category ORDER BY createdAt DESC")
    List<BookEntity> getBooksByCategory(String category);
    
    // 클라우드 동기화가 필요한 도서들 조회
    @Query("SELECT * FROM books WHERE isSyncedToCloud = 0")
    List<BookEntity> getUnsyncedBooks();
    
    // 도서 수 조회
    @Query("SELECT COUNT(*) FROM books")
    int getBooksCount();
    
    // 카테고리별 도서 수 조회
    @Query("SELECT COUNT(*) FROM books WHERE category = :category")
    int getBooksCountByCategory(String category);
    
    // 최근 추가된 도서들 조회 (limit 개수만큼)
    @Query("SELECT * FROM books ORDER BY createdAt DESC LIMIT :limit")
    List<BookEntity> getRecentBooks(int limit);
    
    // 평점 기준으로 도서 조회
    @Query("SELECT * FROM books WHERE rating >= :minRating ORDER BY rating DESC")
    List<BookEntity> getBooksByRating(double minRating);
    
    // 전체 텍스트 검색 (제목, 저자, 출판사에서 검색)
    @Query("SELECT * FROM books WHERE title LIKE '%' || :searchText || '%' " +
           "OR author LIKE '%' || :searchText || '%' " +
           "OR publisher LIKE '%' || :searchText || '%' " +
           "ORDER BY createdAt DESC")
    List<BookEntity> searchBooks(String searchText);
    
    // 특정 기간 내 추가된 도서들 조회
    @Query("SELECT * FROM books WHERE createdAt BETWEEN :startTime AND :endTime ORDER BY createdAt DESC")
    List<BookEntity> getBooksByDateRange(long startTime, long endTime);
    
    // 모든 도서 삭제 (테스트용)
    @Query("DELETE FROM books")
    void deleteAllBooks();
    
    // 클라우드 동기화 상태 업데이트
    @Query("UPDATE books SET isSyncedToCloud = :synced, updatedAt = :updatedAt WHERE isbn = :bookId")
    void updateSyncStatus(String bookId, boolean synced, long updatedAt);
    
    // 특정 출판사의 도서들 조회
    @Query("SELECT * FROM books WHERE publisher = :publisher ORDER BY createdAt DESC")
    List<BookEntity> getBooksByPublisher(String publisher);
}

    //