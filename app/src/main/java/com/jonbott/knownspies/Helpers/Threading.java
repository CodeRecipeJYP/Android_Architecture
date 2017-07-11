package com.jonbott.knownspies.Helpers;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Callable;

import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by j on 4/25/17.
 */

public class Threading {

    public static void dispatchMain(Action block) {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(() -> {
            try {
                block.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static <T> Disposable async(Callable<T> task) {
        return async(task, null, null, Schedulers.io());
    }

    public static <T> Disposable async(Callable<T> task, Consumer<T> finished) {
        return async(task, finished, null, Schedulers.io());
    }

    public static <T> Disposable async(Callable<T> task, Consumer<T> finished, Consumer<Throwable> onError) {
        return async(task, finished, onError, Schedulers.io());
    }

    public static <T> Disposable async(Callable<T> task, Consumer<T> finished, Consumer<Throwable> onError, Scheduler scheduler) {
        finished = finished != null ? finished
                                    : (a) -> {};
        onError = onError != null ? onError
                                  : throwable -> {};

        return Single.fromCallable(task)
                .subscribeOn(scheduler)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(finished, onError);
    }

}
