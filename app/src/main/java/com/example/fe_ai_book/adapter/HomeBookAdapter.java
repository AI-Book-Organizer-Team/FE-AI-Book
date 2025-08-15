package com.example.fe_ai_book.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fe_ai_book.BookDetailActivity;
import com.example.fe_ai_book.R;
import com.example.fe_ai_book.model.Book;

import java.util.ArrayList;
import java.util.List;

public class HomeBookAdapter extends RecyclerView.Adapter<HomeBookAdapter.BookViewHolder> {

    private Context context;
    private List<Book> bookList;

    public HomeBookAdapter(Context context, ArrayList<Book> bookList) {
        this.context = context;
        this.bookList = bookList;
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCover;
        TextView tvTitle, tvAuthor;

        public BookViewHolder(View itemView) {
            super(itemView);
            ivCover = itemView.findViewById(R.id.iv_book_cover);
            tvTitle = itemView.findViewById(R.id.tv_book_title);
            tvAuthor = itemView.findViewById(R.id.tv_book_author);
        }
    }

    @Override
    public BookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_main_home_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BookViewHolder holder, int position) {
        Book book = bookList.get(position);

        // Glide로 이미지 처리 (URL 우선, 없으면 drawable 리소스 사용)
        String url = book.getImageUrl();
        if (url != null && !url.trim().isEmpty()) {
            if (url.startsWith("http://")) {
                url = url.replaceFirst("http://", "https://"); // 가능하면 https 강제
            }

            Glide.with(context)
                    .load(url)
                    .placeholder(R.drawable.book_placeholder_background) // 로딩 중
                    .error(R.drawable.book_placeholder_background)       // 실패 시
                    .centerCrop()
                    .into(holder.ivCover);
        } else {
            holder.ivCover.setImageResource(book.getImageResId());
        }

        // 텍스트 설정
        holder.tvTitle.setText(book.getTitle());
        holder.tvAuthor.setText(book.getAuthor());

        // 아이템 클릭 → BookDetailActivity로 이동
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, BookDetailActivity.class);
            intent.putExtra("book_title", book.getTitle());
            intent.putExtra("book_author", book.getAuthor());
            intent.putExtra("book_image_url", book.getImageUrl()); // URL 전달
            intent.putExtra("book_image_res", book.getImageResId()); // 로컬 리소스 전달
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }
}
