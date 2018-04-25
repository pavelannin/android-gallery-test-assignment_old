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

package ru.annin.gallerytestassignment.presentation.common.alert;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import java.lang.annotation.Retention;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import ru.annin.gallerytestassignment.R;
import ru.annin.gallerytestassignment.data.exception.ApiException;
import ru.annin.gallerytestassignment.utils.BundleUtils;
import ru.annin.gallerytestassignment.utils.ConnectivityFacade;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * @author Pavel Annin.
 */
public class ErrorDialogFragment extends DialogFragment {

    public static final String TAG = ErrorDialogFragment.class.getSimpleName();
    private static final String ARG_ERROR = "ru.annin.gallerytestassignment.args.error";
    private static final String ARG_REQUEST_CODE = "ru.annin.gallerytestassignment.args.request_code";

    public static final int RESULT_RETRY = -1;
    public static final int RESULT_EXIT = -2;

    @Retention(SOURCE)
    @IntDef({RESULT_RETRY, RESULT_EXIT})
    @interface ResultCode { /* Empty. */ }

    public interface OnErrorDialogInteraction {
        void onErrorResult(int requestCode, @ResultCode int resultCode);
    }

    @NonNull
    public static ErrorDialogFragment newInstance(@NonNull Throwable throwable, int requestCode) {
        final Bundle arguments = new Bundle();
        arguments.putSerializable(ARG_ERROR, throwable);
        arguments.putInt(ARG_REQUEST_CODE, requestCode);

        final ErrorDialogFragment fragment = new ErrorDialogFragment();
        fragment.setCancelable(false);
        fragment.setArguments(arguments);
        return fragment;
    }

    private Throwable throwable;
    private int requestCode;
    private OnErrorDialogInteraction listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (OnErrorDialogInteraction) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(String.format("%s must implement OnErrorDialogInteraction", context.toString()));
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        if (savedInstanceState != null && BundleUtils.hasAll(savedInstanceState, ARG_ERROR, ARG_REQUEST_CODE)) {
            throwable = (Throwable) savedInstanceState.getSerializable(ARG_ERROR);
            requestCode = savedInstanceState.getInt(ARG_REQUEST_CODE);
        } else if (getArguments() != null && BundleUtils.hasAll(getArguments(), ARG_ERROR, ARG_REQUEST_CODE)) {
            throwable = (Throwable) getArguments().getSerializable(ARG_ERROR);
            requestCode = getArguments().getInt(ARG_REQUEST_CODE);
        } else {
            throw new IllegalArgumentException("Unknown arguments");
        }

        return new AlertDialog.Builder(requireContext())
                .setMessage(getMessage(throwable))
                .setPositiveButton(R.string.error_action_retry, (dialog, which) -> {
                    if (listener != null) {
                        listener.onErrorResult(requestCode, RESULT_RETRY);
                    }
                    dialog.dismiss();
                })
                .setNegativeButton(R.string.error_action_exit, (dialog, which) -> {
                    if (listener != null) {
                        listener.onErrorResult(requestCode, RESULT_EXIT);
                    }
                    dialog.dismiss();
                })
                .create();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(ARG_ERROR, throwable);
        outState.putInt(ARG_REQUEST_CODE, requestCode);
    }

    @NonNull
    private CharSequence getMessage(@NonNull Throwable throwable) {
        if (throwable instanceof UnknownHostException
                || throwable instanceof ConnectException
                || throwable instanceof SocketTimeoutException) {
            if (ConnectivityFacade.isNetworkAvailable(requireContext())) {
                return getString(R.string.error_message_server_not_available);
            } else {
                return getString(R.string.error_message_internet_not_available);
            }
        } else if (throwable instanceof ApiException) {
            final ApiException apiException = (ApiException) throwable;
            return apiException.getMessage();
        } else {
            return getString(R.string.error_message_unknown);
        }
    }
}