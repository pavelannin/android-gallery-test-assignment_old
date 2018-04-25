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

/**
 * @author Pavel Annin.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PhotoResponse implements Parcelable {

    public static final Parcelable.Creator<PhotoResponse> CREATOR = new Parcelable.Creator<PhotoResponse>() {
        @Override
        public PhotoResponse createFromParcel(Parcel source) {
            return new PhotoResponse(source);
        }

        @Override
        public PhotoResponse[] newArray(int size) {
            return new PhotoResponse[size];
        }
    };


    @JsonProperty(value = "id", required = true)
    private String id;

    @JsonProperty(value = "width", required = true)
    private int width;

    @JsonProperty(value = "height", required = true)
    private int height;

    @JsonProperty(value = "urls", required = true)
    private PhotoSourceResponse src;


    public PhotoResponse() { /* Empty constructor. */ }

    private PhotoResponse(@NonNull Parcel in) {
        id = in.readString();
        width = in.readInt();
        height = in.readInt();

        src = in.readParcelable(PhotoSourceResponse.class.getClassLoader());
    }


    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeInt(width);
        dest.writeInt(height);
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

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @NonNull
    public PhotoSourceResponse getSrc() {
        return src;
    }
}