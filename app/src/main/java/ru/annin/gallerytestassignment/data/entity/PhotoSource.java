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

package ru.annin.gallerytestassignment.data.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Pavel Annin.
 */
public class PhotoSource implements Parcelable {

    public static final Parcelable.Creator<PhotoSource> CREATOR = new Parcelable.Creator<PhotoSource>() {
        @Override
        public PhotoSource createFromParcel(Parcel in) {
            return new PhotoSource(in);
        }

        @Override
        public PhotoSource[] newArray(int size) {
            return new PhotoSource[size];
        }
    };

    @JsonProperty(value = "raw", required = true)
    private String raw;

    @JsonProperty(value = "full", required = true)
    private String full;

    @JsonProperty(value = "regular", required = true)
    private String regular;

    @JsonProperty(value = "small", required = true)
    private String small;

    @JsonProperty(value = "thumb", required = true)
    private String thumb;

    public PhotoSource() { /* Empty constructor. */ }

    private PhotoSource(@NonNull Parcel in) {
        raw = in.readString();
        full = in.readString();
        regular = in.readString();
        small = in.readString();
        thumb = in.readString();
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(raw);
        dest.writeString(full);
        dest.writeString(regular);
        dest.writeString(small);
        dest.writeString(thumb);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getRaw() {
        return raw;
    }

    @NonNull
    public String getFull() {
        return full;
    }

    @NonNull
    public String getRegular() {
        return regular;
    }

    @NonNull
    public String getSmall() {
        return small;
    }

    @NonNull
    public String getThumb() {
        return thumb;
    }
}