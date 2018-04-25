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

/**
 * @author Pavel Annin.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorResponse implements Parcelable {

    public static final Parcelable.Creator<ErrorResponse> CREATOR = new Parcelable.Creator<ErrorResponse>() {
        @Override
        public ErrorResponse createFromParcel(Parcel source) {
            return new ErrorResponse(source);
        }

        @Override
        public ErrorResponse[] newArray(int size) {
            return new ErrorResponse[size];
        }
    };

    @JsonProperty(value = "errors", required = true)
    private List<String> errors;

    public ErrorResponse() { /* Empty constructor. */ }

    private ErrorResponse(Parcel in) {
        this.errors = in.createStringArrayList();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(this.errors);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @NonNull
    public List<String> getErrors() {
        return errors;
    }
}
