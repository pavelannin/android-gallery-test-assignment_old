package ru.annin.gallerytestassignment.utils;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;

/**
 * @author Pavel Annin.
 */
public class LiveDataUtil {

    public static <T> void reObserve(@NonNull LiveData<T> liveData, @NonNull LifecycleOwner owner, @NonNull Observer<T> observer) {
        // https://medium.com/@BladeCoder/architecture-components-pitfalls-part-1-9300dd969808
        liveData.removeObserver(observer);
        liveData.observe(owner, observer);
    }
}
