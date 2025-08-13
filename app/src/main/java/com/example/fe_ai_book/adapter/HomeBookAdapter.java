package com.example.fe_ai_book.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

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
        holder.ivCover.setImageResource(book.getImageResId()); // drawable 리소스
        holder.tvTitle.setText(book.getTitle());
        holder.tvAuthor.setText(book.getAuthor());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BookDetailActivity.class);
                intent.putExtra("book_title", book.getTitle());
                intent.putExtra("book_author", book.getAuthor());
                intent.putExtra("book_image", book.getImageResId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }
}
