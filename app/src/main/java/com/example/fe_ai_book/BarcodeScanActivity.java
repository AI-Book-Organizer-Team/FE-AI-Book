package com.example.fe_ai_book;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;

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
                                        if (isResultSent) return;
                                        for (Barcode barcode : barcodes) {
                                            String raw = barcode.getRawValue();
                                            if (raw != null && isIsbnCandidate(raw)) {
                                                isResultSent = true;
                                                if (cameraProvider != null) cameraProvider.unbindAll();
                                                Intent resultIntent = new Intent();
                                                resultIntent.putExtra("isbn", raw);
                                                setResult(RESULT_OK, resultIntent);
                                                finish();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (imageAnalysis != null) imageAnalysis.clearAnalyzer();
        if (cameraProvider != null) cameraProvider.unbindAll();
        if (scanner != null) scanner.close();
        analysisExecutor.shutdown();
    }
}
