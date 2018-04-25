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

package ru.annin.gallerytestassignment.di;

import android.content.Context;
import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.annin.gallerytestassignment.BuildConfig;
import ru.annin.gallerytestassignment.GalleryApplication;
import ru.annin.gallerytestassignment.data.mapper.PhotoMapper;
import ru.annin.gallerytestassignment.data.remote.UnsplashApi;
import ru.annin.gallerytestassignment.data.repository.PhotoRepository;
import ru.annin.gallerytestassignment.data.repository.inMemory.PhotoByPageRepository;
import ru.annin.gallerytestassignment.domain.GalleryUseCase;

/**
 * @author Pavel Annin.
 */
@Module
public class ApplicationModule {

    @Provides
    @NonNull
    public Context provideContext(@NonNull GalleryApplication application) {
        return application.getApplicationContext();
    }

    @Singleton
    @Provides
    @NonNull
    public UnsplashApi provideUnsplashApi() {
        return new UnsplashApi(BuildConfig.DEBUG, BuildConfig.UNSPLASH_BASE_URL, BuildConfig.UNSPLASH_TOKEN);
    }

    @Provides
    @NonNull
    public PhotoMapper providePhotoMapper() {
        return new PhotoMapper();
    }

    @Singleton
    @Provides
    @NonNull
    public PhotoRepository providePhotoRepository(@NonNull UnsplashApi api, @NonNull PhotoMapper mapper) {
        return new PhotoByPageRepository(api, mapper);
    }

    @Singleton
    @Provides
    @NonNull
    public GalleryUseCase provideGalleryUseCase(@NonNull PhotoRepository photoRepository) {
        return new GalleryUseCase(photoRepository);
    }
}