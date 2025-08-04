package com.example.fe_ai_book.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fe_ai_book.R;
import com.example.fe_ai_book.model.Book;

import java.util.List;

public class DirectSearchBookAdapter extends RecyclerView.Adapter<DirectSearchBookAdapter.BookViewHolder> {

    private List<Book> books;
    private OnBookSelectListener listener;

    public interface OnBookSelectListener {
        void onBookSelected(Book book, boolean isSelected);
    }

    public DirectSearchBookAdapter(List<Book> books, OnBookSelectListener listener) {
        this.books = books;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_direct_search_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = books.get(position);
        holder.bind(book);
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public class BookViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageViewBookCover;
        private TextView textViewBookTitle;
        private TextView textViewAuthor;
        private TextView textViewPublishDate;
        private TextView textViewPublisher;
        private TextView textViewISBN;
        private CheckBox checkBoxSelectBook;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewBookCover = itemView.findViewById(R.id.imageViewBookCover);
            textViewBookTitle = itemView.findViewById(R.id.textViewBookTitle);
            textViewAuthor = itemView.findViewById(R.id.textViewAuthor);
            textViewPublishDate = itemView.findViewById(R.id.textViewPublishDate);
            textViewPublisher = itemView.findViewById(R.id.textViewPublisher);
            textViewISBN = itemView.findViewById(R.id.textViewISBN);
            checkBoxSelectBook = itemView.findViewById(R.id.checkBoxSelectBook);
        }

        public void bind(final Book book) {
            imageViewBookCover.setImageResource(R.drawable.book_placeholder_background); // 임시 설정
            textViewBookTitle.setText(book.getTitle());
            textViewAuthor.setText("작가: " + book.getAuthor());
            textViewPublishDate.setText("발행 연도: " + book.getPublishDate());
            textViewPublisher.setText("출판사: " + book.getPublisher());
            textViewISBN.setText("ISBN: " + book.getIsbn());

            checkBoxSelectBook.setChecked(false);
            checkBoxSelectBook.setOnCheckedChangeListener((buttonView, isChecked) -> listener.onBookSelected(book, isChecked));
        }
    }
}

