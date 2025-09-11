package com.example.fe_ai_book.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fe_ai_book.LoginActivity;
import com.example.fe_ai_book.MyBookRecentActivity;
import com.example.fe_ai_book.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserPageFragment extends Fragment {

    private TextView tvNickname, tvBookCount;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public UserPageFragment() {
        // 기본 생성자 필요
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.user_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvNickname = view.findViewById(R.id.tv_nickname);
        tvBookCount = view.findViewById(R.id.tv_book_count);
        Button btnLogout = view.findViewById(R.id.btn_logout);
        Button btnMyLibrary = view.findViewById(R.id.btn_my_library);
        ImageView btnBack = view.findViewById(R.id.btn_back);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        String uid = mAuth.getCurrentUser().getUid();

        db.collection("users").document(uid)
                .addSnapshotListener((documentSnapshot, e) -> {
                    if (e != null) {
                        Log.e("UserInfo", "유저 데이터 실시간 반영 실패", e);
                        return;
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        String nickname = documentSnapshot.getString("nickname");
                        tvNickname.setText(nickname);

                        db.collection("users").document(uid).collection("books")
                                .addSnapshotListener((querySnapshot, err) -> {
                                    if (err != null) {
                                        Log.e("UserInfo", "책 개수 실시간 반영 실패", err);
                                        return;
                                    }

                                    if (querySnapshot != null) {
                                        int bookCount = querySnapshot.size();
                                        tvBookCount.setText("나의 모든 도서\n" + bookCount + "권");
                                    }
                                });
                    }
                });

        setButtonTextColorChange(btnMyLibrary);
        setButtonTextColorChange(btnLogout);

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            SharedPreferences prefs = requireContext().getSharedPreferences("autoLogin", 0);
            prefs.edit().clear().apply();

            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });

        btnMyLibrary.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), MyBookRecentActivity.class);
            startActivity(intent);
        });

        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
    }

    private void setButtonTextColorChange(Button button) {
        button.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    button.setTextColor(Color.WHITE);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    button.setTextColor(Color.BLACK);
                    break;
            }
            return false;
        });
    }
}
