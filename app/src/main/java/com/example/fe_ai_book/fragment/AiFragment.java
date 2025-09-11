package com.example.fe_ai_book.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fe_ai_book.R;
import com.example.fe_ai_book.adapter.HomeBookAdapter;
import com.example.fe_ai_book.model.Book;
import com.example.fe_ai_book.model.BookRecommendation;
import com.example.fe_ai_book.service.FirebaseBookRecommendationService;
import com.example.fe_ai_book.service.FirebaseBookQueryService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class AiFragment extends Fragment {
    private static final String TAG = "AiFragment";

    private RecyclerView recyclerView1, recyclerView2;
    private TextView aiCurating, bookCurating1, bookCurating2;
    private TextView aiRecs1, aiRecs2;

    private FirebaseBookRecommendationService recommendationService;
    private FirebaseBookQueryService bookQueryService;
    private FirebaseAuth firebaseAuth;

    private HomeBookAdapter adapter1, adapter2;
    private ArrayList<Book> bookList1, bookList2;

    public AiFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_ai, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        initializeServices();
        loadAIRecommendations();
    }

    private String getDefaultReason(String type, List<BookRecommendation> recs) {
        if (recs.isEmpty()) return "";

        switch (type) {
            case "genre":
                if (recs.get(0).getRecommendedBook() != null &&
                        recs.get(0).getRecommendedBook().getCategory() != null) {
                    return recs.get(0).getRecommendedBook().getCategory();
                }
                return "문학";

            case "author":
                if (recs.get(0).getRecommendedBook() != null &&
                        recs.get(0).getRecommendedBook().getAuthor() != null) {
                    return recs.get(0).getRecommendedBook().getAuthor();
                }
                return "인기 작가";

            case "publisher":
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

    private void initializeViews(View view) {
        recyclerView1 = view.findViewById(R.id.recycler_view1);
        recyclerView2 = view.findViewById(R.id.recycler_view2);
        aiCurating = view.findViewById(R.id.ai_curating);
        bookCurating1 = view.findViewById(R.id.book_curating1);
        bookCurating2 = view.findViewById(R.id.book_curating2);
        aiRecs1 = view.findViewById(R.id.ai_recs1);
        aiRecs2 = view.findViewById(R.id.ai_recs2);

        bookList1 = new ArrayList<>();
        bookList2 = new ArrayList<>();

        adapter1 = new HomeBookAdapter(requireContext(), bookList1);
        adapter2 = new HomeBookAdapter(requireContext(), bookList2);

        recyclerView1.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false));
        recyclerView2.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false));

        recyclerView1.setAdapter(adapter1);
        recyclerView2.setAdapter(adapter2);
    }

    private void initializeServices() {
        firebaseAuth = FirebaseAuth.getInstance();
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
        aiCurating.setText("AI가 당신의 취향을 분석하고 있습니다...");

        recommendationService.generateRecommendations(userId, new FirebaseBookRecommendationService.RecommendationCallback() {
            @Override
            public void onRecommendationsReady(List<BookRecommendation> recommendations) {
                displayRecommendations(recommendations);
            }

            @Override
            public void onRecommendationError(String error) {
                Log.e(TAG, "Recommendation error: " + error);
                Toast.makeText(requireContext(), "추천 생성 중 오류가 발생했습니다", Toast.LENGTH_SHORT).show();
                showDefaultRecommendations();
            }
        });
    }

    private void displayRecommendations(List<BookRecommendation> recommendations) {
        Log.d(TAG, "Displaying " + recommendations.size() + " recommendations");

        if (recommendations.isEmpty()) {
            showDefaultRecommendations();
            return;
        }

        // 그룹화 로직
        List<BookRecommendation> group1 = new ArrayList<>();
        List<BookRecommendation> group2 = new ArrayList<>();
        String primaryType = null;
        String secondaryType = null;

        for (BookRecommendation rec : recommendations) {
            String type = rec.getRecommendationType();
            if (type == null || type.isEmpty()) {
                type = "popular";
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

        // 첫 번째 그룹
        if (!group1.isEmpty()) {
            bookList1.clear();
            BookRecommendation firstRec = group1.get(0);
            String firstGroupType = firstRec.getRecommendationType();
            String firstGroupReason = firstRec.getRecommendationReason();

            if (firstGroupType == null || firstGroupType.isEmpty()) {
                firstGroupType = "popular";
            }
            if (firstGroupReason == null || firstGroupReason.isEmpty()) {
                firstGroupReason = getDefaultReason(firstGroupType, group1);
            }

            aiRecs1.setText(getRecommendationTitle(firstGroupType));
            bookCurating1.setText(getRecommendationDescription(firstGroupType, firstGroupReason));

            for (BookRecommendation rec : group1) {
                Book book = convertToBook(rec);
                bookList1.add(book);
            }
            adapter1.notifyDataSetChanged();
        }

        // 두 번째 그룹
        if (!group2.isEmpty()) {
            bookList2.clear();
            BookRecommendation secondRec = group2.get(0);
            String secondGroupType = secondRec.getRecommendationType();
            String secondGroupReason = secondRec.getRecommendationReason();

            if (secondGroupType == null || secondGroupType.isEmpty()) {
                secondGroupType = "collaborative";
            }
            if (secondGroupReason == null || secondGroupReason.isEmpty()) {
                secondGroupReason = getDefaultReason(secondGroupType, group2);
            }

            aiRecs2.setText(getRecommendationTitle(secondGroupType));
            bookCurating2.setText(getRecommendationDescription(secondGroupType, secondGroupReason));

            for (BookRecommendation rec : group2) {
                Book book = convertToBook(rec);
                bookList2.add(book);
            }
            adapter2.notifyDataSetChanged();
        }

        updateCurationMessage(recommendations.size());
    }

    private void showDefaultRecommendations() {
        Log.d(TAG, "showDefaultRecommendations() called");

        aiCurating.setText("저장한 도서를 기반으로 AI가 책을 추천해 드릴게요!");
        aiRecs1.setText("취향 맞춤 추천");
        aiRecs2.setText("좋아하는 작가");

        bookList1.clear();
        bookList2.clear();

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
    }

    private Book convertToBook(BookRecommendation recommendation) {
        Book book = new Book();
        com.example.fe_ai_book.entity.BookEntity recommendedBook = recommendation.getRecommendedBook();

        if (recommendedBook == null) {
            return book;
        }

        book.setTitle(recommendedBook.getTitle());
        book.setAuthor(recommendedBook.getAuthor());
        book.setImageUrl(recommendedBook.getImageUrl());
        book.setPublisher(recommendedBook.getPublisher());
        book.setPublishDate(recommendedBook.getPublishDate());
        book.setIsbn(recommendedBook.getIsbn());
        book.setCategory(recommendedBook.getCategory());
        book.setDescription(recommendedBook.getDescription());

        if (recommendedBook.getPageCount() != null) {
            book.setPageCount(recommendedBook.getPageCount());
        }

        if (book.getImageUrl() == null || book.getImageUrl().trim().isEmpty()) {
            book.setImageResId(R.drawable.sample_cover_backducksu);
        } else {
            book.setImageResId(0);
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
        String userName = "";
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null && currentUser.getDisplayName() != null && !currentUser.getDisplayName().trim().isEmpty()) {
            userName = currentUser.getDisplayName();
        }

        switch (type) {
            case "genre":
                return (!userName.isEmpty()) ? reason + "(을)를 좋아하는 " + userName + "님! 이런 책은 어떠세요?"
                        : reason + "(을)를 좋아하시는군요! 이런 책은 어떠세요?";
            case "author":
                return "최근에 읽은 " + reason + " 작가의 다른 도서를 찾아봤어요!";
            case "publisher":
                return (!userName.isEmpty()) ? reason + "(을)를 좋아하는 " + userName + "님! 이런 책은 어떠세요?"
                        : reason + " 출판사의 엄선된 도서들이에요!";
            case "collaborative":
                return (!userName.isEmpty()) ? userName + "님과 비슷한 취향의 독자들이 선택한 책이에요!"
                        : "비슷한 취향의 독자들이 선택한 책이에요!";
            case "popular":
                return "많은 사람들이 좋아하는 인기 도서에요!";
            default:
                return (!userName.isEmpty()) ? userName + "님을 위한 특별한 추천이에요!"
                        : "특별히 선별한 추천 도서에요!";
        }
    }

    private void updateCurationMessage(int recommendationCount) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String userName = "";
        if (currentUser != null && currentUser.getDisplayName() != null && !currentUser.getDisplayName().trim().isEmpty()) {
            userName = currentUser.getDisplayName();
        }

        String message = (!userName.isEmpty())
                ? userName + "님이 저장한 도서를 기반으로 AI가 책을 추천해 드릴게요!"
                : "저장한 도서를 기반으로 AI가 책을 추천해 드릴게요!";
        aiCurating.setText(message);
    }
}
