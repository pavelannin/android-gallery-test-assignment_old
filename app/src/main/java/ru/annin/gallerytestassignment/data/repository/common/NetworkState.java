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

package ru.annin.gallerytestassignment.data.repository.common;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * @author Pavel Annin.
 */
public class NetworkState implements Parcelable {

    @NonNull
   public static NetworkState loading() {
        return new NetworkState(Status.RUNNING, null);
    }

    @NonNull
    public static NetworkState loaded() {
        return new NetworkState(Status.SUCCESS, null);
    }

    @NonNull
    public static NetworkState failure(@NonNull Throwable throwable) {
        return new NetworkState(Status.FAILED, throwable);
    }

    public static final Parcelable.Creator<NetworkState> CREATOR = new Parcelable.Creator<NetworkState>() {
        @Override
        public NetworkState createFromParcel(Parcel source) {
            return new NetworkState(source);
        }

        @Override
        public NetworkState[] newArray(int size) {
            return new NetworkState[size];
        }
    };

    private final Status status;
    private final Throwable throwable;

    private NetworkState(@NonNull Status status, @Nullable Throwable throwable) {
        this.status = status;
        this.throwable = throwable;
    }

    private NetworkState(@NonNull Parcel in) {
        status = Status.values()[in.readInt()];
        throwable = (Throwable) in.readSerializable();
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(status.ordinal());
        dest.writeSerializable(this.throwable);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @NonNull
    public Status getStatus() {
        return status;
    }

    @Nullable
    public Throwable getThrowable() {
        return throwable;
    }
}
