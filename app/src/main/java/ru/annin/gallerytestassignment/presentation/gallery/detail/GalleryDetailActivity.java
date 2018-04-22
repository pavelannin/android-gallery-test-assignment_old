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

package ru.annin.gallerytestassignment.presentation.gallery.detail;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import ru.annin.gallerytestassignment.R;
import ru.annin.gallerytestassignment.data.entity.Photo;
import ru.annin.gallerytestassignment.data.repository.NetworkState;
import ru.annin.gallerytestassignment.presentation.gallery.GalleryViewModelFactory;
import timber.log.Timber;

/**
 * @author Pavel Annin.
 */
public class GalleryDetailActivity extends AppCompatActivity {

    public static final String EXTRA_POSITION = "ru.annin.gallerytestassignment.extras.position";

    public static void launch(@NonNull Activity activity, int position, int requestCode) {
        final Intent intent = new Intent(activity, GalleryDetailActivity.class);
        intent.putExtra(EXTRA_POSITION, position);
        activity.startActivityForResult(intent, requestCode);
    }


    @Inject
    GalleryViewModelFactory viewModelFactory;
    private GalleryDetailViewModel viewModel;
    private GalleryDetailViewHolder viewHolder;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_detail);

        final int photoPosition;
        if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().containsKey(EXTRA_POSITION)) {
            photoPosition = getIntent().getExtras().getInt(EXTRA_POSITION);
        } else {
            throw new IllegalArgumentException(String.format("Unknown param by name %s", EXTRA_POSITION));
        }

        viewHolder = new GalleryDetailViewHolder(findViewById(R.id.main_container));
        viewHolder.setListener(() -> finishCompletable(viewHolder.getPhotoPosition()));

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(GalleryDetailViewModel.class);
        subscribe(viewModel, () -> viewHolder.setPhotoPosition(photoPosition));
    }

    @Override
    public void onBackPressed() {
        finishCompletable(viewHolder.getPhotoPosition());
    }

    private void finishCompletable(int position) {
        final Intent intent = new Intent();
        intent.putExtra(EXTRA_POSITION, position);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void subscribe(@NonNull GalleryDetailViewModel viewModel, @NonNull Runnable didInitialLoadRunnable) {
        final Observer<NetworkState> networkStateObserver = state -> {
            if (state != null) {
                switch (state.getStatus()) {
                    case RUNNING:
                        viewHolder.getGallery().toggleFooterProgressIndicatorVisible(true);
                        break;
                    case SUCCESS:
                        viewHolder.getGallery().toggleFooterProgressIndicatorVisible(false);
                        break;
                    case FAILED:
                        viewHolder.getGallery().toggleFooterProgressIndicatorVisible(false);
                        Timber.w(state.getThrowable());
                        break;
                }
            }
        };
        viewModel.getNetworkStateLiveData().observe(this, networkStateObserver);

        final Observer<PagedList<Photo>> photosObserver = pagedList -> {
            final int prevCount = viewHolder.getGallery().getItemCount();
            viewHolder.getGallery().submitList(pagedList);
            if (prevCount == 0) {
                didInitialLoadRunnable.run();
            }
        };
        viewModel.getPagedListLiveData().observe(this, photosObserver);
    }
}
