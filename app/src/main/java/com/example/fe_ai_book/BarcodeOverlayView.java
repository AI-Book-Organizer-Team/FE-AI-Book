package com.example.fe_ai_book;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class BarcodeOverlayView extends View {

    private Paint paint;
    private RectF boxRect;
    private float cornerLength = 40f; // 모서리 선 길이

    public BarcodeOverlayView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setColor(0xFF000000);
        paint.setStrokeWidth(6f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 중앙에 네모 영역 계산
        float boxWidth = 600f;
        float boxHeight = 240f;
        float left = (getWidth() - boxWidth) / 2f;
        float top = (getHeight() - boxHeight) / 2f;
        float right = left + boxWidth;
        float bottom = top + boxHeight;
        boxRect = new RectF(left, top, right, bottom);

        // 모서리 네 군데만 그리기
        // 위-왼
        canvas.drawLine(left, top, left + cornerLength, top, paint);
        canvas.drawLine(left, top, left, top + cornerLength, paint);

        // 위-오
        canvas.drawLine(right, top, right - cornerLength, top, paint);
        canvas.drawLine(right, top, right, top + cornerLength, paint);

        // 아래-왼
        canvas.drawLine(left, bottom, left + cornerLength, bottom, paint);
        canvas.drawLine(left, bottom, left, bottom - cornerLength, paint);

        // 아래-오
        canvas.drawLine(right, bottom, right - cornerLength, bottom, paint);
        canvas.drawLine(right, bottom, right, bottom - cornerLength, paint);
    }


}
