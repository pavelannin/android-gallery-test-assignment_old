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
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import ru.annin.gallerytestassignment.R;
import ru.annin.gallerytestassignment.data.entity.Photo;
import ru.annin.gallerytestassignment.data.repository.common.NetworkState;
import ru.annin.gallerytestassignment.presentation.common.alert.ErrorDialogFragment;
import ru.annin.gallerytestassignment.presentation.gallery.GallerySharedElementEnterCallback;
import ru.annin.gallerytestassignment.presentation.gallery.GalleryViewModelFactory;
import timber.log.Timber;

/**
 * @author Pavel Annin.
 */
public class GalleryDetailActivity extends AppCompatActivity implements ErrorDialogFragment.OnErrorDialogInteraction {

    public static final String EXTRA_POSITION = "ru.annin.gallerytestassignment.extras.position";
    private static final int ERC_MORE = 1;

    public static void launch(@NonNull Activity activity, int position, @NonNull final View view) {
        final Intent intent = new Intent(activity, GalleryDetailActivity.class);
        intent.putExtra(EXTRA_POSITION, position);

        final Bundle transitionBundle = ActivityOptionsCompat
                .makeSceneTransitionAnimation(activity, view, ViewCompat.getTransitionName(view))
                .toBundle();
        ActivityCompat.startActivity(activity, intent, transitionBundle);
    }

    @Inject
    GalleryViewModelFactory viewModelFactory;
    private GalleryDetailViewModel viewModel;
    private GalleryDetailViewHolder viewHolder;
    private GallerySharedElementEnterCallback sharedElementCallback;
    private int startingPosition;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_detail);

        sharedElementCallback = new GallerySharedElementEnterCallback();
        ActivityCompat.postponeEnterTransition(this);
        ActivityCompat.setEnterSharedElementCallback(this, sharedElementCallback);

        if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().containsKey(EXTRA_POSITION)) {
            startingPosition = getIntent().getExtras().getInt(EXTRA_POSITION);
        } else {
            throw new IllegalArgumentException(String.format("Unknown param by name %s", EXTRA_POSITION));
        }

        viewHolder = new GalleryDetailViewHolder(findViewById(R.id.main_container));
        viewHolder.setListener(() -> finishCompletable(viewHolder.getPhotoPosition()));
        viewHolder.getGallery().setListener((view, position) -> {
            if (position == startingPosition) {
                sharedElementCallback.setDetailSharedElement(view);
                view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        view.getViewTreeObserver().removeOnPreDrawListener(this);
                        ActivityCompat.startPostponedEnterTransition(GalleryDetailActivity.this);
                        return true;
                    }
                });
            }
        });
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(GalleryDetailViewModel.class);
        subscribe(viewModel, () -> viewHolder.setPhotoPosition(startingPosition));
    }

    @Override
    public void onBackPressed() {
        finishCompletable(viewHolder.getPhotoPosition());
    }

    @Override
    public void onErrorResult(int requestCode, int resultCode) {
        if (resultCode == ErrorDialogFragment.RESULT_RETRY) {
            switch (requestCode) {
                case ERC_MORE:
                    viewModel.retryRequest();
                    break;
                default:
                    throw new IllegalArgumentException(String.format("Unknown request code, %d", requestCode));
            }
        } else if (resultCode == ErrorDialogFragment.RESULT_EXIT) {
            finishCompletable(viewHolder.getPhotoPosition());
        } else {
            throw new IllegalArgumentException(String.format("Unknown result code, %d", resultCode));
        }
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


    private void finishCompletable(int position) {
        final View sharedView = viewHolder.getSharedElementByPosition(position);
        if (sharedView != null) {
            sharedElementCallback.setDetailSharedElement(sharedView);
        }
        final Intent intent = new Intent();
        intent.putExtra(EXTRA_POSITION, position);
        setResult(RESULT_OK, intent);
        ActivityCompat.finishAfterTransition(this);

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
                        if (state.getThrowable() != null) { openError(state.getThrowable(), ERC_MORE); }
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
