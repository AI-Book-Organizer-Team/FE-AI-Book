package com.example.fe_ai_book;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fe_ai_book.adapter.HomeBookAdapter;
import com.example.fe_ai_book.model.Book;
import com.example.fe_ai_book.model.BookRecommendation;
import com.example.fe_ai_book.service.FirebaseBookRecommendationService;
import com.example.fe_ai_book.service.FirebaseBookQueryService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AI_02: 사용자 패턴 기반 AI 추천 학습 구현
 * 사용자의 독서 이력, 별점, 선호 키워드를 분석하여 맞춤형 도서를 추천
 */
public class AiActivity extends AppCompatActivity {
    private static final String TAG = "AiActivity";
    
    private RecyclerView recyclerView1, recyclerView2;
    private TextView aiCurating, bookCurating1, bookCurating2;
    private TextView aiRecs1, aiRecs2;
    
    private FirebaseBookRecommendationService recommendationService;
    private FirebaseBookQueryService bookQueryService;
    private FirebaseAuth firebaseAuth;
    
    private HomeBookAdapter adapter1, adapter2;
    private ArrayList<Book> bookList1, bookList2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai);
        
        initializeViews();
        initializeServices();
        loadAIRecommendations();
    }
    
    private void initializeViews() {
        recyclerView1 = findViewById(R.id.recycler_view1);
        recyclerView2 = findViewById(R.id.recycler_view2);
        aiCurating = findViewById(R.id.ai_curating);
        bookCurating1 = findViewById(R.id.book_curating1);
        bookCurating2 = findViewById(R.id.book_curating2);
        aiRecs1 = findViewById(R.id.ai_recs1);
        aiRecs2 = findViewById(R.id.ai_recs2);
        
        bookList1 = new ArrayList<>();
        bookList2 = new ArrayList<>();
        
        adapter1 = new HomeBookAdapter(this, bookList1);
        adapter2 = new HomeBookAdapter(this, bookList2);
        
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        
        recyclerView1.setLayoutManager(layoutManager1);
        recyclerView2.setLayoutManager(layoutManager2);
        
        recyclerView1.setAdapter(adapter1);
        recyclerView2.setAdapter(adapter2);
        
        // 스크롤을 처음 위치로 설정
        recyclerView1.scrollToPosition(0);
        recyclerView2.scrollToPosition(0);
    }
    
    private void initializeServices() {
        firebaseAuth = FirebaseAuth.getInstance();
        
        // Firebase 기반 서비스들 초기화
        recommendationService = new FirebaseBookRecommendationService();
        bookQueryService = new FirebaseBookQueryService();
    }
    
    private void loadAIRecommendations() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            Log.w(TAG, "User not authenticated, showing default recommendations");
            showDefaultRecommendations();
            return;
        }
        
        String userId = currentUser.getUid();
        Log.d(TAG, "Loading AI recommendations for user: " + userId);
        
        // 로딩 상태 표시
        aiCurating.setText("AI가 당신의 취향을 분석하고 있습니다...");
        
        recommendationService.generateRecommendations(userId, new FirebaseBookRecommendationService.RecommendationCallback() {
            @Override
            public void onRecommendationsReady(List<BookRecommendation> recommendations) {
                runOnUiThread(() -> {
                    displayRecommendations(recommendations);
                });
            }
            
            @Override
            public void onRecommendationError(String error) {
                runOnUiThread(() -> {
                    Log.e(TAG, "Recommendation error: " + error);
                    Toast.makeText(AiActivity.this, "추천 생성 중 오류가 발생했습니다", Toast.LENGTH_SHORT).show();
                    showDefaultRecommendations();
                });
            }
        });
    }
    
    private void displayRecommendations(List<BookRecommendation> recommendations) {
        Log.d(TAG, "Displaying " + recommendations.size() + " recommendations");
        
        if (recommendations.isEmpty()) {
            showDefaultRecommendations();
            return;
        }
        
        // 디버깅을 위한 상세 로그
        for (int i = 0; i < Math.min(3, recommendations.size()); i++) {
            BookRecommendation rec = recommendations.get(i);
            Log.d(TAG, "Recommendation " + i + ": type=" + rec.getRecommendationType() + 
                      ", reason=" + rec.getRecommendationReason() + 
                      ", book=" + (rec.getRecommendedBook() != null ? rec.getRecommendedBook().getTitle() : "null"));
        }
        
        // 추천 결과를 두 그룹으로 나누기 - 타입별로 그룹화
        List<BookRecommendation> group1 = new ArrayList<>();
        List<BookRecommendation> group2 = new ArrayList<>();
        
        // 타입별로 분류
        String primaryType = null;
        String secondaryType = null;
        
        for (BookRecommendation rec : recommendations) {
            String type = rec.getRecommendationType();
            if (type == null || type.isEmpty()) {
                type = "popular"; // 기본값
                rec.setRecommendationType(type);
            }
            
            if (primaryType == null) {
                primaryType = type;
                group1.add(rec);
            } else if (primaryType.equals(type) && group1.size() < 5) {
                group1.add(rec);
            } else if (secondaryType == null) {
                secondaryType = type;
                group2.add(rec);
            } else if (secondaryType.equals(type) && group2.size() < 5) {
                group2.add(rec);
            }
            
            if (group1.size() >= 5 && group2.size() >= 5) break;
        }
        
        // 첫 번째 그룹 설정
        if (!group1.isEmpty()) {
            bookList1.clear();
            BookRecommendation firstRec = group1.get(0);
            String firstGroupType = firstRec.getRecommendationType();
            String firstGroupReason = firstRec.getRecommendationReason();
            
            // null 체크 및 기본값 설정
            if (firstGroupType == null || firstGroupType.isEmpty()) {
                firstGroupType = "popular";
            }
            if (firstGroupReason == null || firstGroupReason.isEmpty()) {
                // 타입에 따른 기본 reason 설정
                firstGroupReason = getDefaultReason(firstGroupType, group1);
            }
            
            Log.d(TAG, "Group1 - Type: " + firstGroupType + ", Reason: " + firstGroupReason);
            
            String title = getRecommendationTitle(firstGroupType);
            String description = getRecommendationDescription(firstGroupType, firstGroupReason);
            
            Log.d(TAG, "Group1 - Title: " + title + ", Description: " + description);
            
            aiRecs1.setText(title);
            bookCurating1.setText(description);
            
            for (BookRecommendation rec : group1) {
                Book book = convertToBook(rec);
                Log.d(TAG, "Group1 book: title=" + book.getTitle() + ", imageUrl=" + book.getImageUrl());
                bookList1.add(book);
            }
            adapter1.notifyDataSetChanged();
        }
        
        // 두 번째 그룹 설정
        if (!group2.isEmpty()) {
            bookList2.clear();
            BookRecommendation secondRec = group2.get(0);
            String secondGroupType = secondRec.getRecommendationType();
            String secondGroupReason = secondRec.getRecommendationReason();
            
            // null 체크 및 기본값 설정
            if (secondGroupType == null || secondGroupType.isEmpty()) {
                secondGroupType = "collaborative";
            }
            if (secondGroupReason == null || secondGroupReason.isEmpty()) {
                // 타입에 따른 기본 reason 설정
                secondGroupReason = getDefaultReason(secondGroupType, group2);
            }
            
            Log.d(TAG, "Group2 - Type: " + secondGroupType + ", Reason: " + secondGroupReason);
            
            String title = getRecommendationTitle(secondGroupType);
            String description = getRecommendationDescription(secondGroupType, secondGroupReason);
            
            Log.d(TAG, "Group2 - Title: " + title + ", Description: " + description);
            
            aiRecs2.setText(title);
            bookCurating2.setText(description);
            
            for (BookRecommendation rec : group2) {
                Book book = convertToBook(rec);
                Log.d(TAG, "Group2 book: title=" + book.getTitle() + ", imageUrl=" + book.getImageUrl());
                bookList2.add(book);
            }
            adapter2.notifyDataSetChanged();
        }
        
        // 큐레이션 메시지 업데이트
        updateCurationMessage(recommendations.size());
    }
    
    private void showDefaultRecommendations() {
        Log.d(TAG, "showDefaultRecommendations() called");
        
        // 사용자 이름 가져오기
        String userName = "";
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null && currentUser.getDisplayName() != null && !currentUser.getDisplayName().trim().isEmpty()) {
            userName = currentUser.getDisplayName();
        }
        
        // 상단 배너 문구
        if (!userName.isEmpty()) {
            aiCurating.setText(userName + "님이 저장한 도서를 기반으로 AI가 책을 추천해 드릴게요!");
        } else {
            aiCurating.setText("저장한 도서를 기반으로 AI가 책을 추천해 드릴게요!");
        }
        
        aiRecs1.setText("취향 맞춤 추천");
        aiRecs2.setText("좋아하는 작가");
        
        // 기본 문구 설정 - 바로 새로운 형식으로
        if (!userName.isEmpty()) {
            bookCurating1.setText("사회과학 장르를 좋아하는 " + userName + "님! 이런 책은 어떠세요?");
            bookCurating2.setText("처비 출판사의 엄선된 도서들이에요!");
        } else {
            bookCurating1.setText("사회과학을 좋아하시는군요! 이런 책은 어떠세요?");
            bookCurating2.setText("처비 출판사의 엄선된 도서들이에요!");
        }
        
        // 사용자의 실제 독서 데이터를 가져와서 큐레이팅 문구 생성 (비동기로 덮어쓰기)
        loadUserBooksForCuration();
        
        // 임시 진단: 우선 폴백 데이터를 먼저 표시
        bookList1.clear();
        bookList2.clear();
        
        // 임시 폴백 데이터 추가
        Book fallback1 = new Book();
        fallback1.setTitle("하루키 무라카미 대표작");
        fallback1.setAuthor("하루키 무라카미");
        fallback1.setImageResId(R.drawable.sample_cover_backducksu);
        bookList1.add(fallback1);
        
        Book fallback2 = new Book();
        fallback2.setTitle("사피엔스");
        fallback2.setAuthor("유발 노아 하라리");
        fallback2.setImageResId(R.drawable.sample_cover_backducksu);
        bookList2.add(fallback2);
        
        adapter1.notifyDataSetChanged();
        adapter2.notifyDataSetChanged();
        Log.d(TAG, "Temporary fallback data displayed");
        
        // 실제 인기 도서 API 호출
        Log.d(TAG, "Starting to load popular books...");
        bookQueryService.getPopularBooks(new FirebaseBookQueryService.BookQueryCallback() {
            @Override
            public void onBooksLoaded(List<Book> books) {
                Log.d(TAG, "Popular books API callback - received " + books.size() + " books");
                runOnUiThread(() -> {
                    bookList1.clear();
                    // 최대 5권까지만
                    int limit = Math.min(books.size(), 5);
                    for (int i = 0; i < limit; i++) {
                        Book book = books.get(i);
                        Log.d(TAG, "Adding popular book: " + book.getTitle() + " by " + book.getAuthor());
                        bookList1.add(book);
                    }
                    adapter1.notifyDataSetChanged();
                    Log.d(TAG, "Popular books UI updated with " + limit + " books");
                });
            }
            
            @Override
            public void onError(String error) {
                Log.e(TAG, "Failed to load popular books: " + error);
                // API 실패시 임시 폴백 데이터
                runOnUiThread(() -> {
                    bookList1.clear();
                    // 임시 폴백: 수동으로 만든 책 데이터
                    Book fallback1 = new Book();
                    fallback1.setTitle("하루키 무라카미 대표작");
                    fallback1.setAuthor("하루키 무라카미");
                    fallback1.setImageResId(R.drawable.sample_cover_backducksu);
                    bookList1.add(fallback1);
                    
                    Book fallback2 = new Book();
                    fallback2.setTitle("사피엔스");
                    fallback2.setAuthor("유발 노아 하라리");
                    fallback2.setImageResId(R.drawable.sample_cover_backducksu);
                    bookList1.add(fallback2);
                    
                    adapter1.notifyDataSetChanged();
                    Log.d(TAG, "Used fallback data for popular books");
                });
            }
        });
        
        // 최신 도서를 위해 일반 검색 (소설 장르로 검색)
        bookQueryService.searchBooksByGenre("소설", new FirebaseBookQueryService.BookQueryCallback() {
            @Override
            public void onBooksLoaded(List<Book> books) {
                runOnUiThread(() -> {
                    bookList2.clear();
                    // 최대 5권까지만
                    int limit = Math.min(books.size(), 5);
                    for (int i = 0; i < limit; i++) {
                        bookList2.add(books.get(i));
                    }
                    adapter2.notifyDataSetChanged();
                    Log.d(TAG, "Loaded " + limit + " recent books");
                });
            }
            
            @Override
            public void onError(String error) {
                Log.e(TAG, "Failed to load recent books: " + error);
                // API 실패시 빈 리스트로 처리
                runOnUiThread(() -> adapter2.notifyDataSetChanged());
            }
        });
    }
    
    private Book convertToBook(BookRecommendation recommendation) {
        Book book = new Book();
        com.example.fe_ai_book.entity.BookEntity recommendedBook = recommendation.getRecommendedBook();
        
        if (recommendedBook == null) {
            return book;
        }
        
        // BookEntity의 필드를 Book으로 복사
        book.setTitle(recommendedBook.getTitle());
        book.setAuthor(recommendedBook.getAuthor());
        book.setImageUrl(recommendedBook.getImageUrl());
        book.setPublisher(recommendedBook.getPublisher());
        book.setPublishDate(recommendedBook.getPublishDate());
        book.setIsbn(recommendedBook.getIsbn());
        book.setCategory(recommendedBook.getCategory());
        book.setDescription(recommendedBook.getDescription());
        
        // PageCount가 Integer이므로 null 체크 후 변환
        if (recommendedBook.getPageCount() != null) {
            book.setPageCount(recommendedBook.getPageCount());
        }
        
        // 이미지 URL이 비어있으면 샘플 커버로 폴백
        if (book.getImageUrl() == null || book.getImageUrl().trim().isEmpty()) {
            book.setImageResId(R.drawable.sample_cover_backducksu);
        } else {
            book.setImageResId(0); // URL 우선
        }
        return book;
    }
    
    private String getRecommendationTitle(String type) {
        switch (type) {
            case "genre": return "취향 맞춤 추천";
            case "author": return "좋아하는 작가";
            case "publisher": return "신뢰하는 출판사";
            case "collaborative": return "인기 추천";
            case "popular": return "인기 도서";
            default: return "AI 추천 도서";
        }
    }
    
    private String getRecommendationDescription(String type, String reason) {
        // 사용자 이름 가져오기
        String userName = "";
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null && currentUser.getDisplayName() != null && !currentUser.getDisplayName().trim().isEmpty()) {
            userName = currentUser.getDisplayName();
        }
        
        switch (type) {
            case "genre": 
                // 장르를 좋아하는 xx님! 이런 책은 어떠세요?
                if (!userName.isEmpty()) {
                    // reason이 장르명인 경우 (예: "소설", "인문" 등)
                    return reason + "(을)를 좋아하는 " + userName + "님! 이런 책은 어떠세요?";
                } else {
                    return reason + "(을)를 좋아하시는군요! 이런 책은 어떠세요?";
                }
            case "author": 
                // 최근에 읽은 xx 작가의 다른 도서를 찾아봤어요!
                // reason이 작가명인 경우 (예: "한강", "무라카미 하루키" 등)
                return "최근에 읽은 " + reason + " 작가의 다른 도서를 찾아봤어요!";
            case "publisher": 
                // 출판사를 좋아하는 xx님! 이런 책은 어떠세요?
                if (!userName.isEmpty()) {
                    return reason + "(을)를 좋아하는 " + userName + "님! 이런 책은 어떠세요?";
                } else {
                    return reason + " 출판사의 엄선된 도서들이에요!";
                }
            case "collaborative": 
                // xx님과 비슷한 취향의 독자들이 선택한 책이에요!
                if (!userName.isEmpty()) {
                    return userName + "님과 비슷한 취향의 독자들이 선택한 책이에요!";
                } else {
                    return "비슷한 취향의 독자들이 선택한 책이에요!";
                }
            case "popular": 
                // 많은 사람들이 좋아하는 인기 도서에요!
                return "많은 사람들이 좋아하는 인기 도서에요!";
            default: 
                // xx님을 위한 특별한 추천이에요!
                if (!userName.isEmpty()) {
                    return userName + "님을 위한 특별한 추천이에요!";
                } else {
                    return "특별히 선별한 추천 도서에요!";
                }
        }
    }
    
    private void updateCurationMessage(int recommendationCount) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String userName = "";
        if (currentUser != null && currentUser.getDisplayName() != null && !currentUser.getDisplayName().trim().isEmpty()) {
            userName = currentUser.getDisplayName();
        }
        
        String message;
        if (!userName.isEmpty()) {
            message = userName + "님이 저장한 도서를 기반으로 AI가 책을 추천해 드릴게요!";
        } else {
            message = "저장한 도서를 기반으로 AI가 책을 추천해 드릴게요!";
        }
        aiCurating.setText(message);
    }
    
    private void loadUserBooksForCuration() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            Log.w(TAG, "User not authenticated for curation");
            return;
        }
        
        String userId = currentUser.getUid();
        String userEmail = currentUser.getEmail();
        String userName = currentUser.getDisplayName();
        
        Log.d(TAG, "Loading books for user: " + userId);
        Log.d(TAG, "User email: " + userEmail);
        Log.d(TAG, "User name: " + userName);
        
        // 사용자의 저장된 책들을 가져와서 큐레이팅 문구 생성
        bookQueryService.getUserBooks(userId, new FirebaseBookQueryService.BookQueryCallback() {
            @Override
            public void onBooksLoaded(List<Book> userBooks) {
                Log.d(TAG, "User books loaded successfully: " + userBooks.size() + " books");
                if (!userBooks.isEmpty()) {
                    for (int i = 0; i < Math.min(3, userBooks.size()); i++) {
                        Book book = userBooks.get(i);
                        Log.d(TAG, "Book " + i + ": " + book.getTitle() + " by " + book.getAuthor());
                    }
                }
                runOnUiThread(() -> {
                    generateCurationMessages(userBooks);
                });
            }
            
            @Override
            public void onError(String error) {
                Log.e(TAG, "Failed to load user books for curation: " + error);
                Log.e(TAG, "Error details: userId=" + userId);
                // 에러시 기본 문구 유지
            }
        });
    }
    
    private void generateCurationMessages(List<Book> userBooks) {
        String userName = "";
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null && currentUser.getDisplayName() != null && !currentUser.getDisplayName().trim().isEmpty()) {
            userName = currentUser.getDisplayName();
        }
        
        Log.d(TAG, "generateCurationMessages called with " + userBooks.size() + " books");
        Log.d(TAG, "Current user name: " + userName);
        
        if (userBooks.isEmpty()) {
            Log.w(TAG, "No user books found in Firebase");
            // 저장된 책이 없는 경우
            if (!userName.isEmpty()) {
                bookCurating1.setText(userName + "님을 위한 인기 도서 추천이에요!");
                bookCurating2.setText("다양한 장르의 최신 도서들을 만나보세요!");
            } else {
                // 사용자 이름이 없는 경우도 처리
                bookCurating1.setText("인기 도서를 추천해드릴게요!");
                bookCurating2.setText("다양한 장르의 최신 도서들을 만나보세요!");
            }
            return;
        }
        
        // 가장 최근에 저장한 책 (첫 번째)
        Book recentBook = userBooks.get(0);
        
        // 장르별 분석
        String favoriteGenre = getMostFrequentGenre(userBooks);
        
        // 작가별 분석
        String mostFrequentAuthor = getMostFrequentAuthor(userBooks);
        int authorBookCount = getAuthorBookCount(userBooks, mostFrequentAuthor);
        
        // 첫 번째 그룹: 장르 기반 추천
        if (favoriteGenre != null && !favoriteGenre.isEmpty()) {
            if (!userName.isEmpty()) {
                // "소설(을)를 좋아하는 xx님! 이런 책은 어떠세요?"
                bookCurating1.setText(favoriteGenre + "(을)를 좋아하는 " + userName + "님! 이런 책은 어떠세요?");
            } else {
                bookCurating1.setText(favoriteGenre + "(을)를 좋아하시는군요! 이런 책은 어떠세요?");
            }
        } else if (recentBook != null && recentBook.getTitle() != null) {
            // 장르 정보가 없으면 최근 책 기반
            if (!userName.isEmpty()) {
                bookCurating1.setText(recentBook.getTitle() + "(을)를 좋아하는 " + userName + "님! 이런 책은 어떠세요?");
            } else {
                bookCurating1.setText(recentBook.getTitle() + "과 비슷한 책들을 찾아봤어요!");
            }
        }
        
        // 두 번째 그룹: 작가 기반 추천 (한강 작가 예시 처럼)
        if (mostFrequentAuthor != null && !mostFrequentAuthor.trim().isEmpty()) {
            if (authorBookCount >= 2) {
                // 같은 작가의 책을 2권 이상 읽었을 때
                bookCurating2.setText("최근에 읽은 " + mostFrequentAuthor + " 작가의 다른 도서를 찾아봤어요!");
            } else {
                // 작가의 책을 1권만 읽었을 때
                bookCurating2.setText(mostFrequentAuthor + " 작가의 새로운 작품을 발견해보세요!");
            }
        } else {
            // 작가 정보가 없으면 일반적인 추천 문구
            if (!userName.isEmpty()) {
                bookCurating2.setText(userName + "님을 위한 특별한 추천 도서에요!");
            } else {
                bookCurating2.setText("새롭게 주목받는 화제의 도서들을 만나보세요!");
            }
        }
        
        Log.d(TAG, "Generated curation messages based on " + userBooks.size() + " user books");
    }
    
    private String getMostFrequentAuthor(List<Book> books) {
        if (books.isEmpty()) return null;
        
        // 작가별 빈도 카운트
        java.util.Map<String, Integer> authorCount = new java.util.HashMap<>();
        for (Book book : books) {
            String author = book.getAuthor();
            if (author != null && !author.trim().isEmpty()) {
                authorCount.put(author, authorCount.getOrDefault(author, 0) + 1);
            }
        }
        
        // 가장 빈도가 높은 작가 찾기
        String mostFrequentAuthor = null;
        int maxCount = 0;
        for (java.util.Map.Entry<String, Integer> entry : authorCount.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                mostFrequentAuthor = entry.getKey();
            }
        }
        
        return mostFrequentAuthor;
    }
    
    private String getMostFrequentGenre(List<Book> books) {
        if (books.isEmpty()) return null;
        
        // 장르별 빈도 카운트
        java.util.Map<String, Integer> genreCount = new java.util.HashMap<>();
        for (Book book : books) {
            String category = book.getCategory();
            if (category != null && !category.trim().isEmpty()) {
                // 카테고리가 "국내도서>소설" 같은 형태일 수 있으므로 추출
                String[] parts = category.split("[>/]");
                if (parts.length > 1) {
                    // 마지막 부분이 주 장르
                    String mainGenre = parts[parts.length - 1].trim();
                    genreCount.put(mainGenre, genreCount.getOrDefault(mainGenre, 0) + 1);
                } else {
                    genreCount.put(category.trim(), genreCount.getOrDefault(category.trim(), 0) + 1);
                }
            }
        }
        
        // 가장 빈도가 높은 장르 찾기
        String mostFrequentGenre = null;
        int maxCount = 0;
        for (java.util.Map.Entry<String, Integer> entry : genreCount.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                mostFrequentGenre = entry.getKey();
            }
        }
        
        return mostFrequentGenre;
    }
    
    private int getAuthorBookCount(List<Book> books, String author) {
        if (books.isEmpty() || author == null || author.isEmpty()) return 0;
        
        int count = 0;
        for (Book book : books) {
            if (author.equals(book.getAuthor())) {
                count++;
            }
        }
        return count;
    }
    
    private String getDefaultReason(String type, List<BookRecommendation> recs) {
        if (recs.isEmpty()) return "";
        
        switch (type) {
            case "genre":
                // 첫 번째 책의 카테고리나 장르 정보 추출
                if (recs.get(0).getRecommendedBook() != null && 
                    recs.get(0).getRecommendedBook().getCategory() != null) {
                    return recs.get(0).getRecommendedBook().getCategory();
                }
                return "문학";
                
            case "author":
                // 첫 번째 책의 작가 정보 추출
                if (recs.get(0).getRecommendedBook() != null && 
                    recs.get(0).getRecommendedBook().getAuthor() != null) {
                    return recs.get(0).getRecommendedBook().getAuthor();
                }
                return "인기 작가";
                
            case "publisher":
                // 첫 번째 책의 출판사 정보 추출
                if (recs.get(0).getRecommendedBook() != null && 
                    recs.get(0).getRecommendedBook().getPublisher() != null) {
                    return recs.get(0).getRecommendedBook().getPublisher();
                }
                return "주요 출판사";
                
            case "collaborative":
                return "비슷한 취향";
                
            case "popular":
                return "베스트셀러";
                
            default:
                return "추천";
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Firebase 서비스는 자동으로 정리뜨
    }
}
