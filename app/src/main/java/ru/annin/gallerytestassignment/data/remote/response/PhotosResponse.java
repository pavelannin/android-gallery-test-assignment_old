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

package ru.annin.gallerytestassignment.data.remote.response;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import ru.annin.gallerytestassignment.data.entity.Photo;

/**
 * @author Pavel Annin.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PhotosResponse implements Parcelable {

    public static final Parcelable.Creator<PhotosResponse> CREATOR = new Parcelable.Creator<PhotosResponse>() {
        @Override
        public PhotosResponse createFromParcel(Parcel source) {
            return new PhotosResponse(source);
        }

        @Override
        public PhotosResponse[] newArray(int size) {
            return new PhotosResponse[size];
        }
    };

    @JsonProperty(value = "total", required = true)
    private int total;

    @JsonProperty(value = "total_pages", required = true)
    private int totalPages;

    @JsonProperty(value = "results", required = true)
    private List<Photo> photos;

    public PhotosResponse() { /* Empty constructor. */ }

    private PhotosResponse(@NonNull Parcel in) {
        total = in.readInt();
        totalPages = in.readInt();
        photos = in.createTypedArrayList(Photo.CREATOR);
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(total);
        dest.writeInt(totalPages);
        dest.writeTypedList(photos);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public int getTotal() {
        return total;
    }

    public int getTotalPages() {
        return totalPages;
    }

    @NonNull
    public List<Photo> getPhotos() {
        return photos;
    }
}