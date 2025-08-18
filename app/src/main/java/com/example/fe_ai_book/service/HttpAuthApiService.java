package com.example.fe_ai_book.service;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.fe_ai_book.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * HTTP API를 사용하는 AuthApiService 구현
 */
public class HttpAuthApiService implements AuthApiService {
    private static final String TAG = "HttpAuthApiService";
    private static final String BASE_URL = "https://your-api-server.com/api"; // 실제 서버 URL로 변경 필요
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    
    private OkHttpClient client;
    private Handler mainHandler;

    public HttpAuthApiService() {
        client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        mainHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void sendEmailVerification(String email, EmailVerificationCallback callback) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("email", email);
        } catch (JSONException e) {
            callback.onFailure("요청 데이터 생성 중 오류가 발생했습니다.");
            return;
        }

        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
        Request request = new Request.Builder()
                .url(BASE_URL + "/auth/send-verification")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "이메일 인증번호 전송 실패", e);
                mainHandler.post(() -> callback.onFailure("네트워크 오류가 발생했습니다."));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String responseBody = response.body().string();
                    
                    if (response.isSuccessful()) {
                        mainHandler.post(() -> callback.onSuccess());
                    } else {
                        JSONObject errorJson = new JSONObject(responseBody);
                        String errorMessage = errorJson.optString("message", "이메일 전송에 실패했습니다.");
                        mainHandler.post(() -> callback.onFailure(errorMessage));
                    }
                } catch (JSONException e) {
                    mainHandler.post(() -> callback.onFailure("응답 처리 중 오류가 발생했습니다."));
                }
            }
        });
    }

    @Override
    public void verifyEmail(String email, String verificationCode, EmailVerificationCallback callback) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("email", email);
            jsonBody.put("verification_code", verificationCode);
        } catch (JSONException e) {
            callback.onFailure("요청 데이터 생성 중 오류가 발생했습니다.");
            return;
        }

        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
        Request request = new Request.Builder()
                .url(BASE_URL + "/auth/verify-email")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "이메일 인증 실패", e);
                mainHandler.post(() -> callback.onFailure("네트워크 오류가 발생했습니다."));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String responseBody = response.body().string();
                    
                    if (response.isSuccessful()) {
                        mainHandler.post(() -> callback.onSuccess());
                    } else {
                        JSONObject errorJson = new JSONObject(responseBody);
                        String errorMessage = errorJson.optString("message", "인증번호가 올바르지 않습니다.");
                        mainHandler.post(() -> callback.onFailure(errorMessage));
                    }
                } catch (JSONException e) {
                    mainHandler.post(() -> callback.onFailure("응답 처리 중 오류가 발생했습니다."));
                }
            }
        });
    }

    @Override
    public void checkNicknameDuplicate(String nickname, ApiCallback<Boolean> callback) {
        Request request = new Request.Builder()
                .url(BASE_URL + "/auth/check-nickname?nickname=" + nickname)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "닉네임 중복 확인 실패", e);
                mainHandler.post(() -> callback.onFailure("네트워크 오류가 발생했습니다."));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String responseBody = response.body().string();
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    
                    if (response.isSuccessful()) {
                        boolean isAvailable = jsonResponse.optBoolean("available", false);
                        mainHandler.post(() -> callback.onSuccess(isAvailable));
                    } else {
                        String errorMessage = jsonResponse.optString("message", "닉네임 확인에 실패했습니다.");
                        mainHandler.post(() -> callback.onFailure(errorMessage));
                    }
                } catch (JSONException e) {
                    mainHandler.post(() -> callback.onFailure("응답 처리 중 오류가 발생했습니다."));
                }
            }
        });
    }

    @Override
    public void signUp(User user, SignUpCallback callback) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("email", user.getEmail());
            jsonBody.put("password", user.getPassword());
            jsonBody.put("nickname", user.getNickname());
            jsonBody.put("gender", user.getGender());
            
            if (user.getBirthDate() != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                jsonBody.put("birth_date", dateFormat.format(user.getBirthDate()));
            }
            
        } catch (JSONException e) {
            callback.onFailure("요청 데이터 생성 중 오류가 발생했습니다.");
            return;
        }

        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
        Request request = new Request.Builder()
                .url(BASE_URL + "/auth/signup")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "회원가입 실패", e);
                mainHandler.post(() -> callback.onFailure("네트워크 오류가 발생했습니다."));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String responseBody = response.body().string();
                    
                    if (response.isSuccessful()) {
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        JSONObject userData = jsonResponse.optJSONObject("user");
                        
                        User newUser = new User();
                        newUser.setEmail(userData.optString("email"));
                        newUser.setNickname(userData.optString("nickname"));
                        newUser.setGender(userData.optString("gender"));
                        newUser.setEmailVerified(userData.optBoolean("email_verified"));
                        
                        mainHandler.post(() -> callback.onSuccess(newUser));
                    } else {
                        JSONObject errorJson = new JSONObject(responseBody);
                        String errorMessage = errorJson.optString("message", "회원가입에 실패했습니다.");
                        mainHandler.post(() -> callback.onFailure(errorMessage));
                    }
                } catch (JSONException e) {
                    mainHandler.post(() -> callback.onFailure("응답 처리 중 오류가 발생했습니다."));
                }
            }
        });
    }

    @Override
    public void signIn(String email, String password, SignInCallback callback) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("email", email);
            jsonBody.put("password", password);
        } catch (JSONException e) {
            callback.onFailure("요청 데이터 생성 중 오류가 발생했습니다.");
            return;
        }

        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
        Request request = new Request.Builder()
                .url(BASE_URL + "/auth/login")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "로그인 실패", e);
                mainHandler.post(() -> callback.onFailure("네트워크 오류가 발생했습니다."));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String responseBody = response.body().string();
                    
                    if (response.isSuccessful()) {
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        JSONObject userData = jsonResponse.optJSONObject("user");
                        
                        User user = new User();
                        user.setEmail(userData.optString("email"));
                        user.setNickname(userData.optString("nickname"));
                        user.setGender(userData.optString("gender"));
                        user.setEmailVerified(userData.optBoolean("email_verified"));
                        
                        mainHandler.post(() -> callback.onSuccess(user));
                    } else {
                        JSONObject errorJson = new JSONObject(responseBody);
                        String errorMessage = errorJson.optString("message", "로그인에 실패했습니다.");
                        mainHandler.post(() -> callback.onFailure(errorMessage));
                    }
                } catch (JSONException e) {
                    mainHandler.post(() -> callback.onFailure("응답 처리 중 오류가 발생했습니다."));
                }
            }
        });
    }
}
