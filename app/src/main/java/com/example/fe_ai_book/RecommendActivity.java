package com.example.fe_ai_book;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fe_ai_book.dto.RecommendItem;
import com.example.fe_ai_book.dto.RecommendRequest;
import com.example.fe_ai_book.dto.RecommendResponse;
import com.example.fe_ai_book.service.RecommendApi;
import com.example.fe_ai_book.service.RetrofitClient;

import java.util.Arrays;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecommendActivity extends AppCompatActivity {

    private TextView tvResult;
    private Button btnCall;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);

        tvResult = findViewById(R.id.tvResult);
        btnCall = findViewById(R.id.btnCall);

        btnCall.setOnClickListener(v -> callRecommend());
    }

    private void callRecommend() {
        RecommendApi api = RetrofitClient.getInstance().create(RecommendApi.class);

        RecommendRequest body = new RecommendRequest(
                Arrays.asList("b001","b002","b010"), 10
        );

        api.getRecommendations(body).enqueue(new Callback<RecommendResponse>() {
            @Override
            public void onResponse(Call<RecommendResponse> call, Response<RecommendResponse> resp) {
                if (!resp.isSuccessful()) {
                    tvResult.setText("HTTP " + resp.code() + " 에러");
                    return;
                }
                RecommendResponse data = resp.body();
                if (data == null || data.recommendations == null) {
                    tvResult.setText("빈 응답");
                    return;
                }
                StringBuilder sb = new StringBuilder();
                for (RecommendItem item : data.recommendations) {
                    sb.append(item.title)
                      .append(" (").append(item.author).append(")")
                      .append(" score=").append(item.score)
                      .append("\n");
                }
                tvResult.setText(sb.toString());
            }

            @Override
            public void onFailure(Call<RecommendResponse> call, Throwable t) {
                tvResult.setText("통신 실패: " + t.getMessage());
            }
        });
    }
}