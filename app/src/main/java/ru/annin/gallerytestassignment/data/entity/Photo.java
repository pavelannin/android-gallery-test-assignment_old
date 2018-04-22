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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Pavel Annin.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Photo implements Parcelable {

    public static final Parcelable.Creator<Photo> CREATOR = new Parcelable.Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel source) {
            return new Photo(source);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };

    @JsonProperty(value = "id", required = true)
    private String id;

    @JsonProperty(value = "description", required = true)
    private String description;

    @JsonProperty(value = "urls", required = true)
    private PhotoSource src;

    public Photo() { /* Empty constructor. */ }

    private Photo(@NonNull Parcel in) {
        id = in.readString();
        description = in.readString();
        src = in.readParcelable(PhotoSource.class.getClassLoader());
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(description);
        dest.writeParcelable(src, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @NonNull
    public String getId() {
        return id;
    }

    @NonNull
    public String getDescription() {
        return description;
    }

    @NonNull
    public PhotoSource getSrc() {
        return src;
    }
}