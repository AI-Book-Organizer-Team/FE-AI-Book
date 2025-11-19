package com.example.fe_ai_book.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fe_ai_book.R;
import com.example.fe_ai_book.model.Book;

import java.util.List;

public class BookCardAdapter extends RecyclerView.Adapter<BookCardAdapter.BookCardViewHolder> {

    private Context context;
    private List<Book> bookList;

    public BookCardAdapter(Context context, List<Book> bookList) {
        this.context = context;
        this.bookList = bookList;
    }

    @NonNull
    @Override
    public BookCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_book_card, parent, false);
        return new BookCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookCardViewHolder holder, int position) {
        Book book = bookList.get(position);
        holder.title.setText(book.getTitle());
        holder.author.setText(book.getAuthor());

        // Firestore 이미지 URL 있으면 Glide로 로드, 아니면 로컬 리소스 표시
        if (book.getImageUrl() != null && !book.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(book.getImageUrl())
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(holder.image);
        } else {
            System.out.println("null");
        }
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    static class BookCardViewHolder extends RecyclerView.ViewHolder {
        TextView title, author;
        ImageView image;

        public BookCardViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textBookTitle);
            author = itemView.findViewById(R.id.textBookAuthor);
            image = itemView.findViewById(R.id.imageBookCover);
        }
    }
}
