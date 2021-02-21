package com.db.abacus.feed;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Flowable;

import java.util.concurrent.TimeUnit;

public interface ScheduledFeedProvider<T, R> {

    Flowable<R> feed(T key);
    Flowable<R> feed(T key, long initialDelay, long period, @NonNull TimeUnit unit);
    void disposeFeed(T key);

}