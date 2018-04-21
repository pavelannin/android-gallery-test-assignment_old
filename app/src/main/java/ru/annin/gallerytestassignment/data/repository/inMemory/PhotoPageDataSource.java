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

package ru.annin.gallerytestassignment.data.repository.inMemory;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PageKeyedDataSource;
import android.support.annotation.NonNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.annin.gallerytestassignment.data.entity.Photo;
import ru.annin.gallerytestassignment.data.remote.PexelApi;
import ru.annin.gallerytestassignment.data.remote.response.PhotosResponse;
import ru.annin.gallerytestassignment.data.repository.NetworkState;

/**
 * @author Pavel Annin.
 */
public class PhotoPageDataSource extends PageKeyedDataSource<Integer, Photo> {

    private final PexelApi api;
    private final String query;
    private final MutableLiveData<NetworkState> initialLoad;
    private final MutableLiveData<NetworkState> networkState;
    private Runnable retry;

    PhotoPageDataSource(@NonNull PexelApi api, @NonNull String query) {
        this.api = api;
        this.query = query;
        networkState = new MutableLiveData<>();
        initialLoad = new MutableLiveData<>();
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Integer, Photo> callback) {
        final int page = 1;
        final Call<PhotosResponse> request = api.getPhotos(query, params.requestedLoadSize, page);
        initialLoad.postValue(NetworkState.loading());
        request.enqueue(new Callback<PhotosResponse>() {
            @Override
            public void onResponse(@NonNull Call<PhotosResponse> call, @NonNull Response<PhotosResponse> response) {
                final PhotosResponse photoResponse = response.body();
                if (photoResponse != null) {
                    callback.onResult(photoResponse.getPhotos(), null, page + 1);
                    initialLoad.postValue(NetworkState.loaded());
                    retry = null;
                } else  {
                    initialLoad.postValue(NetworkState.failure(new RuntimeException("body is null")));
                    retry = () -> loadInitial(params, callback);
                }
            }

            @Override
            public void onFailure(@NonNull Call<PhotosResponse> call, @NonNull Throwable t) {
                initialLoad.postValue(NetworkState.failure(t));
                retry = () -> loadInitial(params, callback);
            }
        });
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Photo> callback) {
        // Ignored, since we only ever append to our initial load
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Photo> callback) {
        final Call<PhotosResponse> request = api.getPhotos(query, params.requestedLoadSize, params.key);
        networkState.postValue(NetworkState.loading());
        request.enqueue(new Callback<PhotosResponse>() {
            @Override
            public void onResponse(@NonNull Call<PhotosResponse> call, @NonNull Response<PhotosResponse> response) {
                final PhotosResponse photoResponse = response.body();
                if (photoResponse != null) {
                    callback.onResult(photoResponse.getPhotos(), params.key + 1);
                    networkState.postValue(NetworkState.loaded());
                    retry = null;
                } else  {
                    networkState.postValue(NetworkState.failure(new RuntimeException("body is null")));
                    retry = () -> loadAfter(params, callback);
                }
            }

            @Override
            public void onFailure(@NonNull Call<PhotosResponse> call, @NonNull Throwable t) {
                networkState.postValue(NetworkState.failure(t));
                retry = () -> loadAfter(params, callback);
            }
        });
    }

    public synchronized void retryFailed() {
        final Runnable prevRetry = retry;
        retry = null;
        if (prevRetry != null) {
            prevRetry.run();
        }
    }

    @NonNull
    public MutableLiveData<NetworkState> getInitialLoad() {
        return initialLoad;
    }

    @NonNull
    public MutableLiveData<NetworkState> getNetworkState() {
        return networkState;
    }
}