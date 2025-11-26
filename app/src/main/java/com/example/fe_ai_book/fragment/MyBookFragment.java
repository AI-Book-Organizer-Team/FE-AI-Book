package com.example.fe_ai_book.fragment;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fe_ai_book.R;


public class MyBookFragment extends Fragment {

    private TextView tabRecent, tabCategory, tabFavorite;
    private ImageView searchIcon;
    private EditText searchBox;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mybook, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tabRecent = view.findViewById(R.id.tab_recent);
        tabCategory = view.findViewById(R.id.tab_category);
        tabFavorite = view.findViewById(R.id.tab_favorite);
        searchBox = view.findViewById(R.id.search_box);
        searchIcon = view.findViewById(R.id.search_icon);

        // 기본은 최근 탭
        if (savedInstanceState == null) {
            showChildFragment(new MyBookRecentFragment());
            highlightTab(tabRecent);
        }

        tabRecent.setOnClickListener(v -> {
            showChildFragment(new MyBookRecentFragment());
            highlightTab(tabRecent);
        });

        tabCategory.setOnClickListener(v -> {
            showChildFragment(new MyBookCategoryFragment());
            highlightTab(tabCategory);
        });

        tabFavorite.setOnClickListener(v -> {
            showChildFragment(new MyBookFavoriteFragment());
            highlightTab(tabFavorite);
        });

        searchIcon.setOnClickListener(v -> {
            String keyword = searchBox.getText().toString().trim();
            sendSearchToCurrentFragment(keyword);
        });

        searchIcon.bringToFront();
        searchIcon.setClickable(true);
        searchIcon.setFocusable(true);
        searchIcon.invalidate();
    }

    private void showChildFragment(Fragment fragment) {
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.mybook_fragment_container, fragment)
                .commit();
    }

    private void highlightTab(TextView selected) {
        // 기본 회색
        tabRecent.setTextColor(Color.parseColor("#999999"));
        tabCategory.setTextColor(Color.parseColor("#999999"));
        tabFavorite.setTextColor(Color.parseColor("#999999"));

        tabRecent.setTypeface(null, Typeface.NORMAL);
        tabCategory.setTypeface(null, Typeface.NORMAL);
        tabFavorite.setTypeface(null, Typeface.NORMAL);

        // 선택된 탭은 검은색 + Bold
        selected.setTextColor(Color.parseColor("#000000"));
        selected.setTypeface(null, Typeface.BOLD);
    }

    private void sendSearchToCurrentFragment(String keyword) {
        Fragment current = getChildFragmentManager()
                .findFragmentById(R.id.mybook_fragment_container);

        if (current instanceof MyBookRecentFragment) {
            ((MyBookRecentFragment) current).search(keyword);
        }
    }
}
