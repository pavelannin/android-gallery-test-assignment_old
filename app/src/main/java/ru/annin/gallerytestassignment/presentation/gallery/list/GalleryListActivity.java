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
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import ru.annin.gallerytestassignment.R;
import ru.annin.gallerytestassignment.data.entity.Photo;
import ru.annin.gallerytestassignment.data.repository.common.NetworkState;
import ru.annin.gallerytestassignment.presentation.common.alert.ErrorDialogFragment;
import ru.annin.gallerytestassignment.presentation.gallery.GallerySharedElementEnterCallback;
import ru.annin.gallerytestassignment.presentation.gallery.GalleryViewModelFactory;
import ru.annin.gallerytestassignment.presentation.gallery.detail.GalleryDetailActivity;
import timber.log.Timber;

/**
 * @author Pavel Annin.
 */
public class GalleryListActivity extends AppCompatActivity implements ErrorDialogFragment.OnErrorDialogInteraction {

    private static final int ERC_INITIAL = 1;
    private static final int ERC_MORE = 2;

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

        viewHolder = new GalleryListViewHolder(findViewById(R.id.main_container));
        subscribe(viewHolder);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(GalleryListViewModel.class);
        subscribe(viewModel);

        if (savedInstanceState == null) {
            viewModel.loadGallery("Android");
        }
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
        ActivityCompat.postponeEnterTransition(this);
        if (data != null && data.getExtras() != null
                && data.getExtras().containsKey(GalleryDetailActivity.EXTRA_POSITION)) {

            final int position = data.getExtras().getInt(GalleryDetailActivity.EXTRA_POSITION);
            viewHolder.setPhotoPosition(position);
            final View sharedElement = viewHolder.getSharedElementById(position);

            if (sharedElement != null) {
                final GallerySharedElementEnterCallback callback = new GallerySharedElementEnterCallback();
                callback.setListSharedElement(sharedElement);
                ActivityCompat.setExitSharedElementCallback(this, callback);
            }
        }
        viewHolder.setOnPreDrawPhotoListListener(() -> ActivityCompat.startPostponedEnterTransition(this));
    }

    @Override
    public void onErrorResult(int requestCode, int resultCode) {
        if (resultCode == ErrorDialogFragment.RESULT_RETRY) {
            switch (requestCode) {
                case ERC_INITIAL:
                    viewModel.refresh();
                    break;
                case ERC_MORE:
                    viewModel.retryRequest();
                    break;
                default:
                    throw new IllegalArgumentException(String.format("Unknown request code, %d", requestCode));
            }
        } else if (resultCode == ErrorDialogFragment.RESULT_EXIT) {
            finish();
        } else {
            throw new IllegalArgumentException(String.format("Unknown result code, %d", resultCode));
        }
    }

    private void openGalleryDetail(int position, @NonNull final View sharedElement) {
        GalleryDetailActivity.launch(this, position, sharedElement);
    }

    private void openError(@NonNull Throwable throwable, int requestCode) {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(ErrorDialogFragment.TAG);
        if (fragment != null && fragment instanceof DialogFragment) {
            final DialogFragment dialogFragment = (DialogFragment) fragment;
            dialogFragment.dismiss();
        }
        ErrorDialogFragment.newInstance(throwable, requestCode)
                .show(getSupportFragmentManager(), ErrorDialogFragment.TAG);
    }

    private void subscribe(@NonNull GalleryListViewHolder viewHolder) {
        viewHolder.setListener(new GalleryListViewHolder.OnClickListener() {
            @Override
            public void onRefresh() {
                viewModel.refresh();
            }

            @Override
            public void onPhotoClick(int position, @NonNull Photo photo, @NonNull View sharedElement) {
                openGalleryDetail(position, sharedElement);
            }
        });
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
                        if (state.getThrowable() != null) { openError(state.getThrowable(), ERC_INITIAL); }
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
                        if (state.getThrowable() != null) { openError(state.getThrowable(), ERC_MORE); }
                        viewHolder.getGallery().toggleFooterProgressIndicatorVisible(false);
                        Timber.w(state.getThrowable());
                        break;
                }
            }
        };
        viewModel.getNetworkStateLiveData().observe(this, networkStateObserver);

        final Observer<PagedList<Photo>> photosObserver = pagedList -> viewHolder.getGallery().submitList(pagedList);
        viewModel.getPagedListLiveData().observe(this, photosObserver);
    }
}
