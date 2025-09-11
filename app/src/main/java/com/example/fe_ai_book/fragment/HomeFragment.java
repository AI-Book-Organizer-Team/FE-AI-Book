package com.example.fe_ai_book.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fe_ai_book.R;
import com.example.fe_ai_book.adapter.HomeBookAdapter;
import com.example.fe_ai_book.model.Book;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

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

        // ================= 리사이클러뷰 더미 데이터 (테스트용) =================
        RecyclerView recycler_view1 = view.findViewById(R.id.recycler_view1);
        RecyclerView recycler_view2 = view.findViewById(R.id.recycler_view2);

        ArrayList<Book> bookList1 = new ArrayList<>();
        ArrayList<Book> bookList2 = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            bookList1.add(new Book("도서 제목 " + i, "작가" + i, R.drawable.sample_cover_backducksu));
            bookList2.add(new Book("도서 제목 " + i, "작가" + i, R.drawable.sample_cover_backducksu));
        }

        HomeBookAdapter adapter1 = new HomeBookAdapter(requireContext(), bookList1);
        HomeBookAdapter adapter2 = new HomeBookAdapter(requireContext(), bookList2);

        recycler_view1.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false));
        recycler_view2.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false));

        recycler_view1.setAdapter(adapter1);
        recycler_view2.setAdapter(adapter2);

        RadioGroup kategorielist_btn = view.findViewById(R.id.kategorielist_btn);
        kategorielist_btn.setOnCheckedChangeListener((radioGroup, checked) -> {
            bookList2.clear();
            if (checked == R.id.kategorielist_btn1) {
                for (int i = 1; i <= 5; i++) {
                    bookList2.add(new Book("도서 제목 " + i, "작가" + i, R.drawable.sample_cover_backducksu));
                }
            } else if (checked == R.id.kategorielist_btn2) {
                for (int i = 2; i <= 6; i++) {
                    bookList2.add(new Book("도서 제목 " + i, "작가" + i, R.drawable.sample_cover_backducksu));
                }
            } else if (checked == R.id.kategorielist_btn3) {
                for (int i = 3; i <= 7; i++) {
                    bookList2.add(new Book("도서 제목 " + i, "작가" + i, R.drawable.sample_cover_backducksu));
                }
            } else if (checked == R.id.kategorielist_btn4) {
                for (int i = 4; i <= 8; i++) {
                    bookList2.add(new Book("도서 제목 " + i, "작가" + i, R.drawable.sample_cover_backducksu));
                }
            } else {
                for (int i = 5; i <= 9; i++) {
                    bookList2.add(new Book("도서 제목 " + i, "작가" + i, R.drawable.sample_cover_backducksu));
                }
            }
            adapter2.notifyDataSetChanged();
        });

        // ================= AI 추천 도서 클릭 이벤트 =================
        View aiBookList = view.findViewById(R.id.AI_booklist);
        if (aiBookList != null) {
            aiBookList.setOnClickListener(v -> {
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, new AiFragment()) // AiFragment로 전환
                        .addToBackStack(null) // 뒤로가기 가능
                        .commit();
            });
        }
    }
}
