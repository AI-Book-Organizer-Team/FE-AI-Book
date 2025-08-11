package com.example.fe_ai_book;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fe_ai_book.model.BookDetailEnvelope;
import com.example.fe_ai_book.model.BookDetailResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookDetailEXActivity extends AppCompatActivity {

    private EditText etIsbn;
    private TextView tvTitle, tvAuthor;
    private ProgressBar progress;
    private Call<BookDetailEnvelope> inFlight; // 진행 중 요청 참조

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail_ex);

        etIsbn = findViewById(R.id.etIsbn);
        tvTitle = findViewById(R.id.tvTitle);
        tvAuthor = findViewById(R.id.tvAuthor);
        progress = findViewById(R.id.progress);

        findViewById(R.id.btnLoad).setOnClickListener(v -> {
            String isbn = etIsbn.getText().toString().trim();
            if (isbn.isEmpty()) {
                Toast.makeText(this, "ISBN13을 입력하세요", Toast.LENGTH_SHORT).show();
                String key = BuildConfig.DATA4LIB_AUTH_KEY;
                debugOnce(key, isbn);
                return;
            }

            loadBook(BuildConfig.DATA4LIB_AUTH_KEY, isbn); // 키는 BuildConfig로 관리 추천
        });
    }

    private void loadBook(String authKey, String isbn13) {
        progress.setVisibility(View.VISIBLE);

        DataLibraryApi api = ApiClient.get();
        inFlight = api.getBookDetail(authKey, isbn13, "Y", "age", "json");

        inFlight.enqueue(new Callback<BookDetailEnvelope>() {
            @Override
            public void onResponse(Call<BookDetailEnvelope> call,
                                   Response<BookDetailEnvelope> res) {
                progress.setVisibility(View.GONE);
                if (!res.isSuccessful() || res.body()==null || res.body().response==null || res.body().response.detail==null) {
                    Toast.makeText(BookDetailEXActivity.this, "응답 오류: " + res.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
                BookDetailEnvelope.Inner r = res.body().response;

                // 서버가 보낸 에러 문자열 우선 처리
                if (r.error != null && !r.error.isEmpty()) {
                    Toast.makeText(BookDetailEXActivity.this, r.error, Toast.LENGTH_SHORT).show();
                    return;
                }

                // 🔴 detail 배열 확인
                if (r.detail == null || r.detail.isEmpty() || r.detail.get(0).book == null) {
                    Toast.makeText(BookDetailEXActivity.this, "도서 상세가 없습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                BookDetailEnvelope.Book b = r.detail.get(0).book;  // 첫 아이템 사용
                tvTitle.setText(b.bookname != null ? b.bookname : "(제목 없음)");
                tvAuthor.setText(b.authors != null ? b.authors : "(저자 없음)");
            }

            @Override
            public void onFailure(Call<BookDetailEnvelope> call, Throwable t) {
                if (call.isCanceled()) return;
                progress.setVisibility(View.GONE);
                Toast.makeText(BookDetailEXActivity.this, "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override protected void onDestroy() {
        if (inFlight != null) inFlight.cancel(); // 생명주기에서 취소(메모리 누수/콜백 크래시 방지)
        super.onDestroy();
    }

    private void debugOnce(String authKey, String isbn13) {
        ApiClient.get().debugBookDetail(authKey, isbn13, "Y", "age")
                .enqueue(new retrofit2.Callback<okhttp3.ResponseBody>() {
                    @Override public void onResponse(retrofit2.Call<okhttp3.ResponseBody> call,
                                                     retrofit2.Response<okhttp3.ResponseBody> res) {
                        try {
                            String raw = res.body() != null ? res.body().string() : "null";
                            Log.d("RAW", raw);
                            // 여기 로그에 보이는 최상위 키가 {"response": {...}} 인지, 에러 메시지/HTML/XML인지 확인!
                        } catch (Exception e) { Log.e("RAW", Log.getStackTraceString(e)); }
                    }
                    @Override public void onFailure(retrofit2.Call<okhttp3.ResponseBody> call, Throwable t) {
                        Log.e("RAW", Log.getStackTraceString(t));
                    }
                });
    }

}
