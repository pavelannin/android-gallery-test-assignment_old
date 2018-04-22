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

package ru.annin.gallerytestassignment.presentation.gallery.list;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;

import ru.annin.gallerytestassignment.data.entity.Photo;
import ru.annin.gallerytestassignment.data.repository.Listing;
import ru.annin.gallerytestassignment.data.repository.NetworkState;
import ru.annin.gallerytestassignment.domain.GalleryUseCase;

/**
 * @author Pavel Annin.
 */
public class GalleryListViewModel extends ViewModel {

    private static final int PAGE_SIZE = 10;

    private final MutableLiveData<String> queryLiveData;
    private final LiveData<Listing<Photo>> listingLiveData;

    public GalleryListViewModel(@NonNull GalleryUseCase useCase) {
        queryLiveData = new MutableLiveData<>();
        listingLiveData = Transformations.switchMap(queryLiveData, input -> useCase.fetchPhoto(input, PAGE_SIZE));
    }

    public void loadGallery(@NonNull String query) {
        queryLiveData.setValue(query);
    }

    public void refresh() {
        final Listing<Photo> listing = listingLiveData.getValue();
        if (listing != null) {
            listing.makeRefresh();
        }
    }

    public void retryRequest() {
        final Listing<Photo> listing = listingLiveData.getValue();
        if (listing != null) {
            listing.makeRetry();
        }
    }

    @NonNull
    public LiveData<PagedList<Photo>> getPagedListLiveData() {
        return Transformations.switchMap(listingLiveData, Listing::getPagedList);
    }

    @NonNull
    public LiveData<NetworkState> getInitialStateLiveData() {
        return Transformations.switchMap(listingLiveData, Listing::getInitialState);
    }

    @NonNull
    public LiveData<NetworkState> getNetworkStateLiveData() {
        return Transformations.switchMap(listingLiveData, Listing::getNetworkState);
    }
}