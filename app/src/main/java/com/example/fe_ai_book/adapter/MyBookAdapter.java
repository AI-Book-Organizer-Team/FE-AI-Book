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

import java.text.SimpleDateFormat;
import java.util.Calendar;   // 🔹 추가
import java.util.Date;
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
        ImageView imageBookCover;
        TextView textBookTitle, textBookAuthor, textBookPublishDate, textBookSavedDate;

        public BookViewHolder(View itemView) {
            super(itemView);
            imageBookCover = itemView.findViewById(R.id.imageBookCover);
            textBookTitle = itemView.findViewById(R.id.textBookTitle);
            textBookAuthor = itemView.findViewById(R.id.textBookAuthor);
            textBookPublishDate = itemView.findViewById(R.id.textBookPublishDate);
            textBookSavedDate = itemView.findViewById(R.id.textBookSavedDate);
        }
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_book_recent, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = bookList.get(position);

        holder.textBookTitle.setText(book.getTitle());
        holder.textBookAuthor.setText(book.getAuthor());

        // 출판일 표시
        holder.textBookPublishDate.setText(book.getPublishDate() != null ? book.getPublishDate() : "");

        // 저장 날짜 표시
        if (book.getCreatedAt() != null) {
            Date date = book.getCreatedAt().toDate();
            holder.textBookSavedDate.setText(formatRelativeDate(date));
        } else {
            holder.textBookSavedDate.setText("날짜 없음");
        }

        // 이미지 로딩
        Glide.with(holder.itemView.getContext())
                .load(book.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.imageBookCover);
    }

    /** 상대 날짜 포맷 */
    private String formatRelativeDate(Date date) {
        Calendar now = Calendar.getInstance();
        Calendar saved = Calendar.getInstance();
        saved.setTime(date);

        int currentWeek = now.get(Calendar.WEEK_OF_YEAR);
        int savedWeek = saved.get(Calendar.WEEK_OF_YEAR);
        int currentYear = now.get(Calendar.YEAR);
        int savedYear = saved.get(Calendar.YEAR);

        long diffMs = now.getTimeInMillis() - saved.getTimeInMillis();
        long diffDays = diffMs / (1000 * 60 * 60 * 24);

        if (currentWeek == savedWeek && currentYear == savedYear) {
            if (diffDays == 0) {
                return "오늘 저장";
            } else if (diffDays == 1) {
                return "어제 저장";
            } else {
                return diffDays + "일 전 저장";
            }
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());
            return sdf.format(date) + " 저장";
        }
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }
}
