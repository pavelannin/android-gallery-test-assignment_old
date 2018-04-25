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

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;

import okhttp3.ConnectionSpec;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okhttp3.TlsVersion;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import ru.annin.gallerytestassignment.data.remote.response.PhotosResponse;
import ru.annin.gallerytestassignment.data.remote.ssl.Tls12SocketFactory;
import timber.log.Timber;

/**
 * @author Pavel Annin.
 */
public class UnsplashApi implements UnsplashService {

    private static final String HEADER_ACCEPT_VERSION = "Accept-Version";
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String ACCEPT_VERSION_1 = "v1";
    private static final String AUTHIRUZATION_FORMAT = "Client-ID %s";

    private static final long TIMEOUT_SEC = 60L;
    private static final long TIMEOUT_READ_SEC = 60L;
    private static final long TIMEOUT_WRITE_SEC = 2 * 60L;

    private final Retrofit retrofit;
    private final UnsplashService service;

    public UnsplashApi(boolean isDebugging, @NonNull String baseUrl, @NonNull String token) {
        retrofit = configRetrofit(isDebugging, baseUrl, token);
        service = retrofit.create(UnsplashService.class);
    }

    @NonNull
    @Override
    public Call<PhotosResponse> getPhotos(@NonNull String query, int perPage, int page) {
        return service.getPhotos(query, perPage, page);
    }

    @Nullable
    public <T> T responseBodyConverter(@NonNull Class<T> clazz, @NonNull ResponseBody body) {
        final Converter<ResponseBody, T> converter = retrofit.responseBodyConverter(clazz, new Annotation[0]);
        try {
            return converter.convert(body);
        } catch (IOException e) {
            return null;
        }
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
        final OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(TIMEOUT_SEC, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_READ_SEC, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_WRITE_SEC, TimeUnit.SECONDS)
                .addInterceptor(configHeaderInterceptor(token))
                .addInterceptor(configLoggingInterceptor(isDebugging));
        return enableTls12(okHttpClient).build();
    }

    @NonNull
    private Interceptor configHeaderInterceptor(@NonNull String token) {
        return chain -> {
            final Request request = chain.request().newBuilder()
                    .addHeader(HEADER_ACCEPT_VERSION, ACCEPT_VERSION_1)
                    .addHeader(HEADER_AUTHORIZATION, String.format(AUTHIRUZATION_FORMAT, token))
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

    @SuppressWarnings("deprecation")
    @NonNull
    private OkHttpClient.Builder enableTls12(@NonNull OkHttpClient.Builder builder) {
        // https://developer.android.com/reference/javax/net/ssl/SSLSocket
        if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 20) {
            try {
                final SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
                sslContext.init(null, null, null);
                builder.sslSocketFactory(new Tls12SocketFactory(sslContext.getSocketFactory()));

                final ConnectionSpec connectionSpec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                        .tlsVersions(TlsVersion.TLS_1_2)
                        .build();

                final List<ConnectionSpec> specs = new ArrayList<>();
                specs.add(connectionSpec);
                specs.add(ConnectionSpec.COMPATIBLE_TLS);
                specs.add(ConnectionSpec.CLEARTEXT);

                builder.connectionSpecs(specs);
            } catch (Exception e) {
                Timber.w(e, "Error while setting TLS 1.2");
            }
        }
        return builder;
    }
}
