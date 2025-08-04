package com.example.fe_ai_book.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.fe_ai_book.R;
import com.example.fe_ai_book.model.Book;

import java.util.List;

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
        holder.ivCover.setImageResource(book.getImageResId()); // drawable 리소스
        holder.tvTitle.setText(book.getTitle());
        holder.tvAuthor.setText(book.getAuthor());
        if (book.getDateSaved() != null) {
            holder.tvPublisher.setText(book.getDateSaved());
        }
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }
}
