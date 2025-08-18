package com.example.fe_ai_book.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fe_ai_book.R;
import com.example.fe_ai_book.model.Book;

import java.util.List;

public class DirectSearchBookAdapter extends RecyclerView.Adapter<DirectSearchBookAdapter.BookViewHolder> {

    private List<Book> books;
    private OnBookSelectListener listener;

    private OnItemClickListener itemClickListener;

    public interface OnBookSelectListener {
        void onBookSelected(Book book, boolean isSelected);
    }

    // 아이템 클릭 리스너
    public interface OnItemClickListener {
        void onItemClick(Book book, int position, View imageView);
    }

    public DirectSearchBookAdapter(List<Book> books, OnBookSelectListener listener) {
        this.books = books;
        this.listener = listener;
    }
    public void setOnItemClickListener(OnItemClickListener l) {
        this.itemClickListener = l;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_direct_search_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = books.get(position);
        holder.bind(book);

        // 이미지 클릭 시 상세로 이동
        holder.imageViewBookCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    int pos = holder.getBindingAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        itemClickListener.onItemClick(books.get(pos), pos, holder.imageViewBookCover);
                    }
                }
            }
        });

        // 체크박스는 기존대로 (전파 방지)
        holder.checkBoxSelectBook.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (listener != null) {
                    int pos = holder.getBindingAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        listener.onBookSelected(books.get(pos), holder.checkBoxSelectBook.isChecked());
                    }
                }
            }
        });
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
            String url = book.getImageUrl();
            if (url != null && !url.trim().isEmpty()) {
                // http → https 강제 변환
                if (url.startsWith("http://")) {
                    url = url.replaceFirst("http://", "https://");
                }

                Glide.with(itemView)
                        .load(url)
                        .placeholder(R.drawable.book_placeholder_background) // 로딩 중
                        .error(R.drawable.book_placeholder_background)       // 실패 시
                        .centerCrop()
                        .into(imageViewBookCover);
            } else {
                imageViewBookCover.setImageResource(R.drawable.book_placeholder_background);
            }

            textViewBookTitle.setText(book.getTitle());
            textViewAuthor.setText("작가: " + book.getAuthor());
            textViewPublishDate.setText("발행 연도: " + book.getPublishDate());
            textViewPublisher.setText("출판사: " + book.getPublisher());
            textViewISBN.setText("ISBN: " + book.getIsbn());

            checkBoxSelectBook.setChecked(false);
            checkBoxSelectBook.setOnCheckedChangeListener(
                    (buttonView, isChecked) -> listener.onBookSelected(book, isChecked));
        }
    }
}
