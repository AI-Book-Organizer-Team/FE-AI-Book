package com.example.fe_ai_book.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fe_ai_book.R;
import com.example.fe_ai_book.model.Book;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MyBookAdapter extends RecyclerView.Adapter<MyBookAdapter.BookViewHolder> {

    private Context context;
    private List<Book> bookList;

    public MyBookAdapter(Context context, List<Book> bookList) {
        this.context = context;
        this.bookList = bookList;
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCover;
        TextView tvTitle, tvAuthor, tvPublisher;

        public BookViewHolder(View itemView) {
            super(itemView);
            ivCover = itemView.findViewById(R.id.imageViewCover);
            tvTitle = itemView.findViewById(R.id.textViewTitle);
            tvAuthor = itemView.findViewById(R.id.textViewAuthor);
            tvPublisher = itemView.findViewById(R.id.textViewPublisher);
        }
    }

    @Override
    public BookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_mybook, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BookViewHolder holder, int position) {
        Book book = bookList.get(position);

        // 이미지 (imageUrl 있으면 Glide로 로드, 없으면 기본 이미지)
        if (book.getImageUrl() != null && !book.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(book.getImageUrl())
                    .placeholder(R.drawable.ic_launcher_background) // 로딩 중
                    .into(holder.ivCover);
        } else {
            holder.ivCover.setImageResource(R.drawable.ic_launcher_background);
        }

        // 텍스트
        holder.tvTitle.setText(book.getTitle() != null ? book.getTitle() : "제목 없음");
        holder.tvAuthor.setText(book.getAuthor() != null ? book.getAuthor() : "작가 미상");

        // 날짜 or 출판사 표시
        if (book.getPublishDate() != null && !book.getPublishDate().isEmpty()) {
            holder.tvPublisher.setText(book.getPublishDate());
        } else if (book.getPublisher() != null) {
            holder.tvPublisher.setText(book.getPublisher());
        } else {
            holder.tvPublisher.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }
}
