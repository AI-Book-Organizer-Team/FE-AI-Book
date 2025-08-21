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
        
        // 추천 결과를 두 그룹으로 나누기
        List<BookRecommendation> group1 = recommendations.stream()
                .limit(5)
                .collect(Collectors.toList());
        List<BookRecommendation> group2 = recommendations.stream()
                .skip(5)
                .limit(5)
                .collect(Collectors.toList());
        
        // 첫 번째 그룹 설정
        if (!group1.isEmpty()) {
            bookList1.clear();
            String firstGroupType = group1.get(0).getRecommendationType();
            String firstGroupReason = group1.get(0).getRecommendationReason();
            
            aiRecs1.setText(getRecommendationTitle(firstGroupType));
            bookCurating1.setText(getRecommendationDescription(firstGroupType, firstGroupReason));
            
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
            String secondGroupType = group2.get(0).getRecommendationType();
            String secondGroupReason = group2.get(0).getRecommendationReason();
            
            aiRecs2.setText(getRecommendationTitle(secondGroupType));
            bookCurating2.setText(getRecommendationDescription(secondGroupType, secondGroupReason));
            
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
        bookCurating1.setText("많은 독자들이 좋아하는 인기 도서들입니다");
        bookCurating2.setText("최근 출간된 신간 도서들입니다");
        
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
        switch (type) {
            case "genre": return reason + " 장르를 좋아하시는군요!";
            case "author": return reason + " 작가의 다른 작품들이에요";
            case "publisher": return reason + " 출판사의 엄선된 도서들";
            case "collaborative": return "비슷한 취향의 독자들이 선택한 책";
            case "popular": return "많은 사람들이 좋아하는 인기 도서";
            default: return "당신을 위한 특별한 추천";
        }
    }
    
    private void updateCurationMessage(int recommendationCount) {
        String message = "총 " + recommendationCount + "권의 맞춤 도서를 추천해드릴게요!";
        aiCurating.setText(message);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Firebase 서비스는 자동으로 정리됨
    }
}
