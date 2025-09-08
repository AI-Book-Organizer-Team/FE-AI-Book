package com.example.fe_ai_book;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.fe_ai_book.fragment.MyBookRecentFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainHomebarActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_homebar);

        bottomNav = findViewById(R.id.bottom_nav);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_main) {
                startActivity(new Intent(this, HomeActivity.class));
                return true;

            } else if (id == R.id.nav_mybook) {
                // 프래그먼트 전환
                showFragment(new MyBookRecentFragment());
                return true;

            } else if (id == R.id.nav_addbook) {
                startActivity(new Intent(this, BarcodeScanActivity.class));
                return true;

            } else if (id == R.id.nav_userpage) {
                startActivity(new Intent(this, UserInfoActivity.class));
                return true;
            }

            return false;
        });

        // 앱 처음 실행 시 특정 프래그먼트를 기본으로 띄우고 싶으면 여기에 호출
        if (savedInstanceState == null) {
            // 예: 처음엔 내 서재 탭을 띄우고 싶으면 아래 실행
            // showFragment(new MyBookRecentFragment());
        }
    }

    private void showFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }
}
