package com.example.fe_ai_book;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.fe_ai_book.fragment.MyBookRecentFragment;
import com.example.fe_ai_book.fragment.MyBookCategoryFragment;
import com.example.fe_ai_book.fragment.MyBookFavoriteFragment;

public class MyBookActivity extends AppCompatActivity {

    private TextView tabRecent, tabCategory, tabFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mybook_activity);

        tabRecent = findViewById(R.id.tab_recent);
        tabCategory = findViewById(R.id.tab_category);
        tabFavorite = findViewById(R.id.tab_favorite);

        if (savedInstanceState == null) {
            showFragment(new MyBookRecentFragment());
            highlightTab(tabRecent);
        }

        tabRecent.setOnClickListener(v -> {
            showFragment(new MyBookRecentFragment());
            highlightTab(tabRecent);
        });

        tabCategory.setOnClickListener(v -> {
            showFragment(new MyBookCategoryFragment());
            highlightTab(tabCategory);
        });

        tabFavorite.setOnClickListener(v -> {
            showFragment(new MyBookFavoriteFragment());
            highlightTab(tabFavorite);
        });
    }

    private void showFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void highlightTab(TextView selected) {
        tabRecent.setTextColor(Color.parseColor("#999999"));
        tabCategory.setTextColor(Color.parseColor("#999999"));
        tabFavorite.setTextColor(Color.parseColor("#999999"));

        tabRecent.setTypeface(null, Typeface.NORMAL);
        tabCategory.setTypeface(null, Typeface.NORMAL);
        tabFavorite.setTypeface(null, Typeface.NORMAL);

        selected.setTextColor(Color.parseColor("#000000"));
        selected.setTypeface(null, Typeface.BOLD);
    }
}