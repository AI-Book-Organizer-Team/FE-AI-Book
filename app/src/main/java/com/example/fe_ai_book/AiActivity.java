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
        
        recyclerView1.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        recyclerView2.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        
        recyclerView1.setAdapter(adapter1);
        recyclerView2.setAdapter(adapter2);
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
        aiCurating.setText("더 정확한 추천을 위해 도서를 추가하고 별점을 매겨보세요!");
        aiRecs1.setText("인기 도서");
        aiRecs2.setText("최신 도서");
        
        // 기본 문구 설정 (나중에 사용자 데이터로 덮어쓰기)
        bookCurating1.setText("많은 사람들이 좋아하는 인기 도서에요!");
        bookCurating2.setText("최근 출간된 신간 도서들입니다!");
        
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
        book.setTitle(recommendation.getRecommendedBook().getTitle());
        book.setAuthor(recommendation.getRecommendedBook().getAuthor());
        book.setImageUrl(recommendation.getRecommendedBook().getImageUrl());
        // 이미지 URL이 비어있으면 샘플 커버로 폴백 (UI 수정 없이 최소 표시 보장)
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
                if (!userName.isEmpty()) {
                    return reason + " 장르를 좋아하는 " + userName + "님! 이런 책은 어떠세요?";
                } else {
                    return reason + " 장르를 좋아하시는군요! 이런 책은 어떠세요?";
                }
            case "author": 
                return "최근에 읽은 " + reason + " 작가의 다른 도서를 찾아봤어요!";
            case "publisher": 
                if (!userName.isEmpty()) {
                    return reason + " 출판사를 좋아하는 " + userName + "님을 위한 추천!";
                } else {
                    return reason + " 출판사의 엄선된 도서들이에요!";
                }
            case "collaborative": 
                if (!userName.isEmpty()) {
                    return userName + "님과 비슷한 취향의 독자들이 선택한 책이에요!";
                } else {
                    return "비슷한 취향의 독자들이 선택한 책이에요!";
                }
            case "popular": 
                return "많은 사람들이 좋아하는 인기 도서에요!";
            default: 
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
        
        // 작가별 분석
        String mostFrequentAuthor = getMostFrequentAuthor(userBooks);
        
        // 첫 번째 그룹: 최근 저장한 책 기반 추천
        if (!userName.isEmpty()) {
            bookCurating1.setText(recentBook.getTitle() + "을 좋아하는 " + userName + "님! 이런 책은 어떠세요?");
        } else {
            bookCurating1.setText(recentBook.getTitle() + "과 비슷한 책들을 찾아봤어요!");
        }
        
        // 두 번째 그룹: 가장 많이 읽은 작가 기반 추천
        if (mostFrequentAuthor != null && !mostFrequentAuthor.trim().isEmpty()) {
            bookCurating2.setText("최근에 읽은 " + mostFrequentAuthor + " 작가의 다른 도서를 찾아봤어요!");
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
