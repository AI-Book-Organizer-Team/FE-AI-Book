package com.example.fe_ai_book.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fe_ai_book.BookDetailActivity;
import com.example.fe_ai_book.BuildConfig;
import com.example.fe_ai_book.R;
import com.example.fe_ai_book.SearchActivity;
import com.example.fe_ai_book.adapter.HomeBookAdapter;
import com.example.fe_ai_book.dto.RecommendRequest;
import com.example.fe_ai_book.dto.RecommendResponse;
import com.example.fe_ai_book.mapper.BookApiMapper;
import com.example.fe_ai_book.model.Book;
import com.example.fe_ai_book.model.BookDetailEnvelope;
import com.example.fe_ai_book.service.ApiClient;
import com.example.fe_ai_book.service.DataLibraryApi;
import com.example.fe_ai_book.service.FirebaseBookQueryService;
import com.example.fe_ai_book.service.RecommendApi;
import com.example.fe_ai_book.service.RetrofitClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    // Service
    private FirebaseBookQueryService bookQueryService;
    private DataLibraryApi dataLibraryApi;

    // Adapters and Lists
    private HomeBookAdapter aiBooksAdapter;
    private HomeBookAdapter categoryBooksAdapter;
    private final ArrayList<Book> aiBookList = new ArrayList<>();
    private final ArrayList<Book> categoryBookList = new ArrayList<>();

    // UI Components
    private ImageView myBookCover;
    private TextView bookCuratingText;
    private RecyclerView aiRecyclerView;
    private RecyclerView categoryRecyclerView;
    private RadioGroup categoryRadioGroup;

    // User Info
    private String currentUserId;
    private String currentUserDisplayName;
    private Book recentBook; // 현재 표시된 최신 도서 정보를 저장

    public HomeFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. 서비스 초기화
        bookQueryService = new FirebaseBookQueryService();
        dataLibraryApi = ApiClient.get();

        // 2. 유저 정보 확인
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            currentUserId = user.getUid();
            currentUserDisplayName = user.getDisplayName() != null ? user.getDisplayName() : "USER";
        } else {
            Toast.makeText(getContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 3. UI 컴포넌트 초기화
        myBookCover = view.findViewById(R.id.my_book_cover);
        bookCuratingText = view.findViewById(R.id.book_curating);
        aiRecyclerView = view.findViewById(R.id.recycler_view1);
        categoryRecyclerView = view.findViewById(R.id.recycler_view2);
        categoryRadioGroup = view.findViewById(R.id.kategorielist_btn);

        // 4. 리사이클러뷰 및 어댑터 설정
        setupRecyclerViews();

        // 5. 데이터 로드
        loadMyRecentBook();
        loadAiBooks();
        loadCategoryBooks("인기"); // 기본으로 '인기' 도서 로드

        // 6. 이벤트 리스너 설정
        setupCategoryTags();
        setupEventListeners(view);
    }

    private void setupRecyclerViews() {
        // AI 추천 도서 리사이클러뷰
        aiBooksAdapter = new HomeBookAdapter(requireContext(), aiBookList);
        aiRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false));
        aiRecyclerView.setAdapter(aiBooksAdapter);

        // 카테고리별 추천 도서 리사이클러뷰
        categoryBooksAdapter = new HomeBookAdapter(requireContext(), categoryBookList);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false));
        categoryRecyclerView.setAdapter(categoryBooksAdapter);
    }

    private void setupEventListeners(View view) {
        // 카테고리 태그 변경 리스너
        categoryRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selectedRadioButton = group.findViewById(checkedId);
            if (selectedRadioButton != null) {
                String category = selectedRadioButton.getText().toString();
                loadCategoryBooks(category);
            }
        });

        // AI 추천 도서 목록 전체보기 클릭 이벤트
        View aiBookListLayout = view.findViewById(R.id.AI_booklist);
        if (aiBookListLayout != null) {
            aiBookListLayout.setOnClickListener(v -> {
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, new AiFragment()) // AiFragment로 전환
                        .addToBackStack(null)
                        .commit();
            });
        }
        View aiBanner = view.findViewById(R.id.banner_ai_recommend);
        if (aiBanner != null) {
            aiBanner.setOnClickListener(v -> {
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, new AiFragment())
                        .addToBackStack(null)
                        .commit();
            });
        }

        // 나의 서재 전체보기 클릭 이벤트
        View myBookshelfLayout = view.findViewById(R.id.my_bookshelf);
        if (myBookshelfLayout != null) {
            myBookshelfLayout.setOnClickListener(v -> {
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, new MyBookFragment()) // MyBookFragment로 전환
                        .addToBackStack(null)
                        .commit();
            });
        }

        // 카테고리 추천 전체보기 클릭 이벤트
        View categoryListLayout = view.findViewById(R.id.kategorielist);
        if (categoryListLayout != null) {
            categoryListLayout.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), SearchActivity.class);
                startActivity(intent);
            });
        }

        // 나의 서재 책 표지 클릭 이벤트
        myBookCover.setOnClickListener(v -> {
            if (recentBook != null) {
                Intent intent = new Intent(getContext(), BookDetailActivity.class);
                intent.putExtra("book_title", recentBook.getTitle());
                intent.putExtra("book_author", recentBook.getAuthor());
                intent.putExtra("book_publisher", recentBook.getPublisher());
                intent.putExtra("book_publishDate", recentBook.getPublishDate());
                intent.putExtra("book_isbn", recentBook.getIsbn());
                intent.putExtra("book_description", recentBook.getDescription());
                intent.putExtra("book_imageUrl", recentBook.getImageUrl());
                intent.putExtra("book_category", recentBook.getCategory());
                startActivity(intent);
            }
        });
    }

    /**
     * 나의 서재: 가장 최근 등록한 책 1권 표시
     */
    private void loadMyRecentBook() {
        if (currentUserId == null) return;

        bookQueryService.getUserBooks(currentUserId, new FirebaseBookQueryService.BookQueryCallback() {
            @Override
            public void onBooksLoaded(List<Book> books) {
                if (isAdded() && books != null && !books.isEmpty()) {
                    // [마지막에 추가된 책을 최신으로]
                    recentBook = books.get(books.size() - 1);
                    if (recentBook.getImageUrl() != null) {
                        Glide.with(HomeFragment.this)
                             .load(recentBook.getImageUrl())
                             .placeholder(R.drawable.sample_cover_backducksu) // 로딩 중 이미지
                             .error(R.drawable.main_home_my_book_list)       // 에러 시 이미지
                             .into(myBookCover);
                    }
                } else {
                    Log.d(TAG, "사용자의 책이 없거나 Fragment가 Detached 상태입니다.");
                }
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "최근 등록 도서 로드 실패: " + error);
            }
        });
    }

    /**
     * 카테고리별 추천 도서 목록 로드 (DataLibrary API 사용)
     * @param category 검색할 카테고리 (태그 이름)
     */
    private void loadCategoryBooks(String category) {
        categoryBookList.clear();

        Call<BookDetailEnvelope> call;
        String authKey = BuildConfig.DATA4LIB_AUTH_KEY;

        if ("인기".equals(category)) {
            // 인기 도서 API는 임시로 최근 한 달로 설정
            call = dataLibraryApi.getPopularBooks(authKey, "2023-10-01", "2023-10-30", 1, 10, "json");
        } else {
            call = dataLibraryApi.searchBooks(authKey, category, null, null, 1, 10, "json");
        }

        call.enqueue(new Callback<BookDetailEnvelope>() {
            @Override
            public void onResponse(Call<BookDetailEnvelope> call, Response<BookDetailEnvelope> response) {
                if (isAdded() && response.isSuccessful() && response.body() != null) {
                    List<Book> books = BookApiMapper.mapToBookList(response.body());
                    categoryBookList.addAll(books);
                    categoryBooksAdapter.notifyDataSetChanged();
                } else {
                    Log.e(TAG, "카테고리 도서 로드 실패: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<BookDetailEnvelope> call, Throwable t) {
                if (isAdded()) {
                    Log.e(TAG, "카테고리 도서 로드 API 호출 실패", t);
                }
            }
        });
    }
    private void loadAiBooks() {
        if (currentUserId == null) return;

        // 1. 사용자가 읽은 책 목록 가져오기
        bookQueryService.getUserBooks(currentUserId, new FirebaseBookQueryService.BookQueryCallback() {
            @Override
            public void onBooksLoaded(List<Book> userBooks) {
                if (!isAdded()) return;

                if (userBooks.size() < 3) {
                    // 읽은 책이 3권 미만이면, 인기 도서를 AI 추천으로 대신 보여줌
                    bookCuratingText.setText(String.format("%s님, 이런 책은 어떠세요?", currentUserDisplayName));
                    loadPopularBooksForAi();
                } else {
                    // 읽은 책 기반으로 AI 추천 요청
                    List<String> bookIsbns = new ArrayList<>();
                    for (Book book : userBooks) {
                        bookIsbns.add(book.getIsbn());
                    }
                    getAiRecommendations(bookIsbns);
                }
            }

            @Override
            public void onError(String error) {
                if (isAdded()) {
                    Log.e(TAG, "AI 추천을 위한 사용자 도서 로드 실패: " + error);
                    // 실패 시에도 인기 도서로 대체
                    bookCuratingText.setText(String.format("%s님, 이런 책은 어떠세요?", currentUserDisplayName));
                    loadPopularBooksForAi();
                }
            }
        });
    }

    private void getAiRecommendations(List<String> bookIsbns) {
        RecommendApi recommendApi = RetrofitClient.getInstance().create(RecommendApi.class);
        RecommendRequest request = new RecommendRequest(bookIsbns, 10);

        recommendApi.getRecommendations(request).enqueue(new Callback<RecommendResponse>() {
            @Override
            public void onResponse(Call<RecommendResponse> call, Response<RecommendResponse> response) {
                if (isAdded() && response.isSuccessful() && response.body() != null && response.body().recommendations != null) {
                    // 현재 더미 데이터.
                    aiBookList.clear();
                    for (int i = 1; i <= 5; i++) {
                        aiBookList.add(new Book("AI 추천 도서 " + i, "AI 작가" + i, R.drawable.sample_cover_backducksu));
                    }
                    aiBooksAdapter.notifyDataSetChanged();
                    bookCuratingText.setText(String.format("%s님을 위한 맞춤 도서 추천입니다.", currentUserDisplayName));
                } else {
                    Log.e(TAG, "AI 추천 실패, 인기 도서로 대체");
                    bookCuratingText.setText(String.format("%s님, 이런 인기 도서는 어떠세요?", currentUserDisplayName));
                    loadPopularBooksForAi();
                }
            }

            @Override
            public void onFailure(Call<RecommendResponse> call, Throwable t) {
                if (isAdded()) {
                    Log.e(TAG, "AI 추천 API 호출 실패, 인기 도서로 대체", t);
                    bookCuratingText.setText(String.format("%s님, 이런 인기 도서는 어떠세요?", currentUserDisplayName));
                    loadPopularBooksForAi();
                }
            }
        });
    }

    private void loadPopularBooksForAi() {
        String authKey = BuildConfig.DATA4LIB_AUTH_KEY;
        dataLibraryApi.getPopularBooks(authKey, "2023-10-01", "2023-10-30", 1, 10, "json").enqueue(new Callback<BookDetailEnvelope>() {
            @Override
            public void onResponse(Call<BookDetailEnvelope> call, Response<BookDetailEnvelope> response) {
                if (isAdded() && response.isSuccessful() && response.body() != null) {
                    List<Book> books = BookApiMapper.mapToBookList(response.body());
                    aiBookList.clear();
                    aiBookList.addAll(books);
                    aiBooksAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<BookDetailEnvelope> call, Throwable t) {
                if(isAdded()) {
                    Log.e(TAG, "AI 대체 인기 도서 로드 실패", t);
                }
            }
        });
    }

    private void setupCategoryTags() {
        List<String> categories = Arrays.asList("인기", "소설", "경제", "에세이", "IT", "인문");
        RadioGroup radioGroup = categoryRadioGroup; // 멤버 변수 사용
        radioGroup.setOrientation(LinearLayout.HORIZONTAL);

        for (String category : categories) {
            RadioButton radioButton = new RadioButton(getContext());
            radioButton.setText(category);
            radioButton.setBackgroundResource(R.drawable.main_home_kategorie); // 배경 drawable 적용
            radioButton.setButtonDrawable(null); // 기본 라디오 버튼 아이콘 숨기기

            // 패딩과 마진 설정
            int padding = (int) (5 * getResources().getDisplayMetrics().density);
            radioButton.setPadding(padding, padding, padding, padding);
            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
            int marginRight = (int) (10 * getResources().getDisplayMetrics().density);
            params.setMargins(0, 0, marginRight, 0);
            radioButton.setLayoutParams(params);

            radioGroup.addView(radioButton);

            if (category.equals("인기")) { // 기본 선택
                radioButton.setChecked(true);
            }
        }
    }
}
