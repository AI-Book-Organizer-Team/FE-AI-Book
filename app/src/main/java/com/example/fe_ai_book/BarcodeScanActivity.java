package com.example.fe_ai_book;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.fe_ai_book.mapper.BookApiMapper;
import com.example.fe_ai_book.model.Book;
import com.example.fe_ai_book.model.BookDetailEnvelope;
import com.example.fe_ai_book.service.ApiClient;
import com.example.fe_ai_book.service.DataLibraryApi;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BarcodeScanActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST = 1001;
    private static final String TAG = "BarcodeScan";

    private PreviewView previewView;
    private BarcodeScanner scanner;

    private ProcessCameraProvider cameraProvider;
    private ImageAnalysis imageAnalysis;

    private final ExecutorService analysisExecutor = Executors.newSingleThreadExecutor();
    private volatile boolean isResultSent = false;
    private volatile boolean isProcessing = false;
    private Book currentBook;
    private Call<BookDetailEnvelope> apiCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scan);

        previewView = findViewById(R.id.previewView);

        BarcodeScannerOptions options = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_EAN_13, Barcode.FORMAT_EAN_8)
                .build();
        scanner = BarcodeScanning.getClient(options);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                finish();
            }
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                imageAnalysis.setAnalyzer(analysisExecutor, new ImageAnalysis.Analyzer() {
                    @Override
                    @ExperimentalGetImage // ← 경고 사라짐
                    public void analyze(@NonNull ImageProxy imageProxy) {
                        try {
                            if (isResultSent) {
                                imageProxy.close();
                                return;
                            }
                            Image mediaImage = imageProxy.getImage();
                            if (mediaImage == null) {
                                imageProxy.close();
                                return;
                            }

                            InputImage image = InputImage.fromMediaImage(
                                    mediaImage,
                                    imageProxy.getImageInfo().getRotationDegrees()
                            );

                            scanner.process(image)
                                    .addOnSuccessListener(barcodes -> {
                                        if (isResultSent || isProcessing) return;
                                        for (Barcode barcode : barcodes) {
                                            String raw = barcode.getRawValue();
                                            if (raw != null && isIsbnCandidate(raw)) {
                                                isProcessing = true;
                                                // 카메라는 계속 켜두고 BottomSheet 표시
                                                runOnUiThread(() -> showBookConfirmBottomSheet(raw));
                                                break;
                                            }
                                        }
                                    })
                                    .addOnFailureListener(e -> Log.e(TAG, "Barcode scan failed", e))
                                    .addOnCompleteListener(t -> imageProxy.close());
                        } catch (Exception e) {
                            Log.e(TAG, "analyze error", e);
                            imageProxy.close();
                        }
                    }
                });

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(
                        this,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageAnalysis
                );

            } catch (Exception e) {
                Log.e(TAG, "startCamera error", e);
                finish();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    // ISBN 후보 간단 필터 (EAN-13, 978/979 prefix)
    private boolean isIsbnCandidate(String raw) {
        String digits = raw.replaceAll("[^0-9Xx]", "");
        return digits.length() == 13 && (digits.startsWith("978") || digits.startsWith("979"));
    }
    
    private void showBookConfirmBottomSheet(String isbn) {
        // 먼저 API 호출
        currentBook = new Book();
        DataLibraryApi api = ApiClient.get();
        apiCall = api.getBookDetail(BuildConfig.DATA4LIB_AUTH_KEY, isbn, "N", "age", "json");
        
        // 로딩 중 토스트 메시지 표시
        Toast.makeText(this, "ISBN: " + isbn + " 정보를 가져오는 중...", Toast.LENGTH_SHORT).show();

        apiCall.enqueue(new Callback<BookDetailEnvelope>() {
            @Override
            public void onResponse(Call<BookDetailEnvelope> call, Response<BookDetailEnvelope> response) {
                if (!response.isSuccessful() || response.body() == null || response.body().response == null) {
                    Toast.makeText(BarcodeScanActivity.this, "응답 오류: " + response.code(), Toast.LENGTH_SHORT).show();
                    isProcessing = false;
                    return;
                }

                BookDetailEnvelope.Inner r = response.body().response;

                if (r.error != null || r.detail == null || r.detail.isEmpty() || r.detail.get(0).book == null) {
                    Toast.makeText(BarcodeScanActivity.this, "도서 상세가 없습니다.", Toast.LENGTH_SHORT).show();
                    isProcessing = false;
                    return;
                }

                // 서버 모델 -> UI 모델 변환
                BookDetailEnvelope.Book apiBook = r.detail.get(0).book;
                Book ui = BookApiMapper.toUi(apiBook);
                currentBook = ui;
                
                // API 응답을 받은 후에만 BottomSheet 표시
                runOnUiThread(() -> {
                    BottomSheetDialog dialog = new BottomSheetDialog(BarcodeScanActivity.this);
                    View sheet = getLayoutInflater().inflate(R.layout.sheet_book_confirm, null);

                    ImageView cover = sheet.findViewById(R.id.ivCover);
                    TextView title = sheet.findViewById(R.id.tvTitle);
                    TextView author = sheet.findViewById(R.id.tvAuthor);
                    TextView Date = sheet.findViewById(R.id.tvPubDate);
                    TextView Pub = sheet.findViewById(R.id.tvPub);
                    TextView isbn13 = sheet.findViewById(R.id.tvIsbn);
                    Button btnConfirm = sheet.findViewById(R.id.btnConfirm);
                    Button btnCancel = sheet.findViewById(R.id.btnCancel);

                    // UI에 데이터 설정
                    if (ui.getIsbn() != null) {
                        title.setText(currentBook.getTitle());
                        author.setText("저자: " + currentBook.getAuthor());
                        Pub.setText("출판사: " + currentBook.getPublisher());
                        Date.setText("출시 연도: " + currentBook.getPublishDate());
                        isbn13.setText("ISBN: " + currentBook.getIsbn());
                    }

                    // 표지 이미지
                    if (ui.getImageUrl() != null && !ui.getImageUrl().trim().isEmpty()) {
                        Glide.with(BarcodeScanActivity.this)
                                .load(ui.getImageUrl())
                                .placeholder(R.drawable.book_placeholder_background)
                                .error(R.drawable.book_placeholder_background)
                                .into(cover);
                    } else {
                        cover.setImageResource(R.drawable.book_placeholder_background);
                    }
                    
                    // 버튼 설정
                    btnCancel.setOnClickListener(v -> {
                        dialog.dismiss();
                        isProcessing = false;
                    });

                    btnConfirm.setOnClickListener(v -> {
                        // BookDetailActivity로 이동
                        Intent intent = new Intent(BarcodeScanActivity.this, BookDetailActivity.class);
                        intent.putExtra("book_title", currentBook.getTitle());
                        intent.putExtra("book_author", currentBook.getAuthor());
                        intent.putExtra("book_publisher", currentBook.getPublisher());
                        intent.putExtra("book_publishDate", currentBook.getPublishDate());
                        intent.putExtra("book_isbn", currentBook.getIsbn());
                        intent.putExtra("book_description", currentBook.getDescription());
                        intent.putExtra("book_imageUrl", currentBook.getImageUrl());
                        intent.putExtra("book_category", currentBook.getCategory());
                        intent.putExtra("isbn13", currentBook.getIsbn());
                        startActivity(intent);
                        dialog.dismiss();
                        finish(); // 카메라 화면 종료
                    });
                    
                    // 다이얼로그 닫힐 때 처리
                    dialog.setOnDismissListener(dialogInterface -> {
                        isProcessing = false;
                    });
                    
                    // BottomSheet 표시
                    dialog.setContentView(sheet);
                    dialog.show();
                });
            }

            @Override
            public void onFailure(Call<BookDetailEnvelope> call, Throwable throwable) {
                if (!call.isCanceled()) {
                    Toast.makeText(BarcodeScanActivity.this, "ISBN 조회 실패: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    isProcessing = false;
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (imageAnalysis != null) imageAnalysis.clearAnalyzer();
        if (cameraProvider != null) cameraProvider.unbindAll();
        if (scanner != null) scanner.close();
        analysisExecutor.shutdown();
    }
}
