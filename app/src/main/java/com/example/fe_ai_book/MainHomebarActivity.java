package com.example.fe_ai_book;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.fe_ai_book.fragment.HomeFragment;
import com.example.fe_ai_book.fragment.MyBookFragment;
import com.example.fe_ai_book.fragment.UserPageFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainHomebarActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_homebar);

        bottomNav = findViewById(R.id.bottom_nav);

        // 기본 화면은 홈 탭
        if (savedInstanceState == null) {
            showFragment(new HomeFragment());
        }

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_main) {
                showFragment(new HomeFragment());
                return true;

            } else if (id == R.id.nav_mybook) {
                // ✅ 이제는 MyBookFragment를 붙인다
                showFragment(new MyBookFragment());
                return true;

            } else if (id == R.id.nav_addbook) {
                // 카메라는 예외적으로 Activity 실행
                startActivity(new Intent(this, BarcodeScanActivity.class));
                return true;

            } else if (id == R.id.nav_userpage) {
                showFragment(new UserPageFragment());
                return true;
            }

            return false;
        });
    }

    private void showFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }
}
