/*
 * MIT License
 *
 * Copyright (c) 2018 Pavel Annin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package ru.annin.gallerytestassignment.data.remote;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import ru.annin.gallerytestassignment.data.remote.response.PhotosResponse;

/**
 * @author Pavel Annin.
 */
public class PexelApi implements PexelsService {

    private static final String HEADER_AUTHORIZATION = "Authorization";

    private static final long TIMEOUT_SEC = 60L;
    private static final long TIMEOUT_READ_SEC = 60L;
    private static final long TIMEOUT_WRITE_SEC = 2 * 60L;

    private final PexelsService service;

    public PexelApi(boolean isDebugging, @NonNull String baseUrl, @NonNull String token) {
        service = configRetrofit(isDebugging, baseUrl, token).create(PexelsService.class);
    }

    @NonNull
    @Override
    public Call<PhotosResponse> getPhotos(@NonNull String query, int perPage, int page) {
        return service.getPhotos(query, perPage, page);
    }

    @NonNull
    private Retrofit configRetrofit(boolean isDebugging, @NonNull String baseUrl, @NonNull String token) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(JacksonConverterFactory.create())
                .client(configHttpClient(isDebugging, token))
                .build();
    }

    @NonNull
    private OkHttpClient configHttpClient(boolean isDebugging, @NonNull String token) {
        return new OkHttpClient.Builder()
                .connectTimeout(TIMEOUT_SEC, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_READ_SEC, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_WRITE_SEC, TimeUnit.SECONDS)
                .addInterceptor(configHeaderInterceptor(token))
                .addInterceptor(configLoggingInterceptor(isDebugging))
                .build();
    }

    @NonNull
    private Interceptor configHeaderInterceptor(@NonNull String token) {
        return chain -> {
            final Request request = chain.request().newBuilder()
                    .addHeader(HEADER_AUTHORIZATION, token)
                    .build();
            return chain.proceed(request);
        };
    }

    @NonNull
    private Interceptor configLoggingInterceptor(boolean isDebugging) {
        final HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        final HttpLoggingInterceptor.Level level = isDebugging
                ? HttpLoggingInterceptor.Level.BODY
                : HttpLoggingInterceptor.Level.NONE;
        interceptor.setLevel(level);
        return interceptor;
    }
}