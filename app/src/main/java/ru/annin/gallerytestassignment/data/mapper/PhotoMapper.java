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

package ru.annin.gallerytestassignment.data.mapper;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import ru.annin.gallerytestassignment.data.entity.Photo;
import ru.annin.gallerytestassignment.data.remote.response.PhotoResponse;

/**
 * @author Pavel Annin.
 */
public class PhotoMapper {

    private static final int WIDTH = 200;

    @NonNull
    public Photo toPhoto(@NonNull PhotoResponse response) {
        final int height = (int) ((WIDTH / (float) response.getWidth()) * response.getHeight());
        return new Photo(response.getId(), WIDTH, height, response.getSrc().getThumb());
    }

    @NonNull
    public List<Photo> toPhotos(@NonNull List<PhotoResponse> responses) {
        final List<Photo> photos = new ArrayList<>();
        for (final PhotoResponse response : responses) {
            photos.add(toPhoto(response));
        }
        return photos;
    }
}
