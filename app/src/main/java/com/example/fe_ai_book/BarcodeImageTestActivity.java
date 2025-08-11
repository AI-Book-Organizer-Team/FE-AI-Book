package com.example.fe_ai_book;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

public class BarcodeImageTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_image_test);

        Button btnTest = findViewById(R.id.btnTest);

        btnTest.setOnClickListener(v -> {
            try {
                // 1. drawable에서 이미지 불러오기
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.isbn_test);
                if (bitmap == null) {
                    sendResultAndFinish("이미지 로드 실패");
                    return;
                }

                InputImage image = InputImage.fromBitmap(bitmap, 0);

                // 2. 바코드 스캐너 옵션
                BarcodeScannerOptions options = new BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(Barcode.FORMAT_EAN_13, Barcode.FORMAT_EAN_8)
                        .build();

                BarcodeScanner scanner = BarcodeScanning.getClient(options);

                // 3. 스캔 처리
                scanner.process(image)
                        .addOnSuccessListener(barcodes -> {
                            String isbn = "바코드 없음";
                            if (!barcodes.isEmpty()) {
                                isbn = barcodes.get(0).getRawValue();
                            }
                            sendResultAndFinish(isbn);
                        })
                        .addOnFailureListener(e -> {
                            sendResultAndFinish("스캔 실패: " + e.getMessage());
                        });

            } catch (Exception e) {
                sendResultAndFinish("예외 발생: " + e.getMessage());
            }
        });
    }

    // MainActivity로 결과 전달 후 종료
    private void sendResultAndFinish(String isbn) {
        Toast.makeText(this, isbn, Toast.LENGTH_SHORT).show();
        Intent resultIntent = new Intent();
        resultIntent.putExtra("isbn", isbn);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
