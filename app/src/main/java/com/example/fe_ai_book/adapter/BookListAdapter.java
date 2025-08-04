package com.example.fe_ai_book.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.fe_ai_book.Book;
import com.example.fe_ai_book.R;

import java.util.List;

public class BookListAdapter extends RecyclerView.Adapter<BookListAdapter.BookViewHolder> {

    private List<Book> bookList;

    public BookListAdapter(List<Book> bookList) {
        this.bookList = bookList;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = bookList.get(position);
        holder.textViewTitle.setText(book.getTitle());
        holder.textViewAuthor.setText(book.getAuthor());
        holder.textViewPublisher.setText(book.getPublisher());

        Glide.with(holder.itemView.getContext())
             .load(book.getCoverImageUrl())
             .placeholder(R.drawable.ic_launcher_background)
             .error(R.drawable.ic_launcher_foreground)
             .into(holder.imageViewCover);
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewCover;
        TextView textViewTitle;
        TextView textViewAuthor;
        TextView textViewPublisher;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewCover = itemView.findViewById(R.id.imageViewCover);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewAuthor = itemView.findViewById(R.id.textViewAuthor);
            textViewPublisher = itemView.findViewById(R.id.textViewPublisher);
        }
    }
}