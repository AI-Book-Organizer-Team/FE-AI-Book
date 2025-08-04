package com.example.fe_ai_book.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.fe_ai_book.model.BookCategory;
import com.example.fe_ai_book.model.Book;
import com.example.fe_ai_book.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BookCategoryAdapter extends RecyclerView.Adapter<BookCategoryAdapter.CategoryViewHolder> {

    private Context context;
    private List<BookCategory> categoryList;

    public BookCategoryAdapter(Context context, List<BookCategory> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_book_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        BookCategory category = categoryList.get(position);
        holder.categoryTitle.setText(category.getCategoryTitle()); // ← 수정된 부분

        // 내부 책 리스트용 어댑터 설정
        BookCardAdapter bookCardAdapter = new BookCardAdapter(context, category.getBookList());
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        holder.recyclerView.setAdapter(bookCardAdapter);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryTitle;
        RecyclerView recyclerView;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryTitle = itemView.findViewById(R.id.tv_category_title);
            recyclerView = itemView.findViewById(R.id.recycler_books);
        }
    }
}
