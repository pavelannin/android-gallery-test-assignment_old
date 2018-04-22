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

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;

import ru.annin.gallerytestassignment.data.entity.Photo;
import ru.annin.gallerytestassignment.data.remote.UnsplashApi;
import ru.annin.gallerytestassignment.data.repository.Listing;
import ru.annin.gallerytestassignment.data.repository.PhotoRepository;

/**
 * @author Pavel Annin.
 */
public class PhotoByPageRepository implements PhotoRepository {

    private final UnsplashApi api;

    public PhotoByPageRepository(@NonNull UnsplashApi api) {
        this.api = api;
    }

    @MainThread
    @NonNull
    @Override
    public Listing<Photo> listPhoto(@NonNull String query, int pageSize) {
        final PhotoDataSourceFactory factory = new PhotoDataSourceFactory(api, query);
        final LiveData<PagedList<Photo>> livePagedList = new LivePagedListBuilder<>(factory, pageSize)
                .build();
        return new Listing<>(
                livePagedList,
                Transformations.switchMap(factory.getSourceLiveData(), PhotoPageDataSource::getInitialLoad),
                Transformations.switchMap(factory.getSourceLiveData(), PhotoPageDataSource::getNetworkState),
                () -> {
                    final PhotoPageDataSource source = factory.getSourceLiveData().getValue();
                    if (source != null) {
                        source.invalidate();
                    }
                },
                () -> {
                    final PhotoPageDataSource source = factory.getSourceLiveData().getValue();
                    if (source != null) {
                        source.retryFailed();
                    }
                });
    }
}
