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

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import ru.annin.gallerytestassignment.R;
import ru.annin.gallerytestassignment.data.entity.Photo;
import ru.annin.gallerytestassignment.data.repository.NetworkState;
import ru.annin.gallerytestassignment.presentation.gallery.GalleryViewModelFactory;
import ru.annin.gallerytestassignment.presentation.gallery.detail.GalleryDetailActivity;
import ru.annin.gallerytestassignment.utils.LiveDataUtil;
import timber.log.Timber;

/**
 * @author Pavel Annin.
 */
public class GalleryListActivity extends AppCompatActivity {

    private static final int RC_GALLERY_DETAIL = 1;

    public static void launch(@NonNull Context context) {
        final Intent intent = new Intent(context, GalleryListActivity.class);
        context.startActivity(intent);
    }


    @Inject
    GalleryViewModelFactory viewModelFactory;
    private GalleryListViewModel viewModel;
    private GalleryListViewHolder viewHolder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_list);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(GalleryListViewModel.class);
        viewHolder = new GalleryListViewHolder(findViewById(R.id.main_container));

        viewHolder.setListener(new GalleryListViewHolder.OnClickListener() {
            @Override
            public void onRefresh() {
                viewModel.refresh();
            }

            @Override
            public void onPhotoClick(int position, @NonNull Photo photo) {
                openGalleryDetail(position, photo);
            }
        });

        subscribe(viewModel);
        if (savedInstanceState == null) {
            viewModel.loadGallery("Android");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RC_GALLERY_DETAIL:
                if (resultCode == RESULT_OK && data != null && data.getExtras() != null
                        && data.getExtras().containsKey(GalleryDetailActivity.EXTRA_POSITION)) {
                    final int position = data.getExtras().getInt(GalleryDetailActivity.EXTRA_POSITION);
                    viewHolder.setPhotoPosition(position);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);

        }
    }

    private void openGalleryDetail(int position, @NonNull Photo photo) {
        GalleryDetailActivity.launch(this, position, RC_GALLERY_DETAIL);
    }

    private void subscribe(@NonNull GalleryListViewModel viewModel) {
        final Observer<NetworkState> initialStateObserver = state -> {
            if (state != null) {
                switch (state.getStatus()) {
                    case RUNNING:
                        viewHolder.toggleRefreshingVisible(true);
                        break;
                    case SUCCESS:
                        viewHolder.toggleRefreshingVisible(false);
                        break;
                    case FAILED:
                        viewHolder.toggleRefreshingVisible(false);
                        Timber.w(state.getThrowable());
                        break;
                }
            }
        };
        viewModel.getInitialStateLiveData().observe(this, initialStateObserver);

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

        final Observer<PagedList<Photo>> photosObserver = pagedList -> viewHolder.getGallery().submitList(pagedList);
        LiveDataUtil.reObserve(viewModel.getPagedListLiveData(), this, photosObserver);
    }
}
