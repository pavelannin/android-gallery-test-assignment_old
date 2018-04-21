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

package ru.annin.gallerytestassignment.data.repository;

import android.arch.lifecycle.LiveData;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;

/**
 * @author Pavel Annin.
 */
public class Listing<T> {

    private final LiveData<PagedList<T>> pagedList;
    private final LiveData<NetworkState> initialState;
    private final LiveData<NetworkState> networkState;
    private final Runnable refreshRunnable;
    private final Runnable retryRunnable;

    public Listing(@NonNull LiveData<PagedList<T>> pagedList,
                   @NonNull LiveData<NetworkState> initialState,
                   @NonNull LiveData<NetworkState> networkState,
                   @NonNull Runnable refreshRunnable,
                   @NonNull Runnable retryRunnable) {
        this.pagedList = pagedList;
        this.initialState = initialState;
        this.networkState = networkState;
        this.refreshRunnable = refreshRunnable;
        this.retryRunnable = retryRunnable;
    }

    @NonNull
    public LiveData<PagedList<T>> getPagedList() {
        return pagedList;
    }

    @NonNull
    public LiveData<NetworkState> getInitialState() {
        return initialState;
    }

    @NonNull
    public LiveData<NetworkState> getNetworkState() {
        return networkState;
    }

    public void makeRefresh() {
        refreshRunnable.run();
    }

    public void makeRetry() {
        retryRunnable.run();
    }
}