package com.db.abacus.feed;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.functions.Predicate;
import io.reactivex.rxjava3.functions.Supplier;

import java.util.Comparator;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class NonRecurringFeed <T> {

    private final AtomicReference<T> prev = new AtomicReference<>();
    private final AtomicBoolean stopped = new AtomicBoolean();
    private final Flowable<T> flowable;

    public NonRecurringFeed(Supplier<T> supplier, Comparator<T> comparator,
                            long initialDelay, long period, @NonNull TimeUnit unit) {
        this.flowable = Flowable.interval(initialDelay, period, unit)
                .takeUntil((Predicate<? super Long>)  a -> stopped.get())
                .map(a -> supplier.get())
                .filter(a -> prev.get() == null || comparator.compare(a, prev.get()) != 0)
                .doAfterNext(prev::set)
                .share();
    }

    public Flowable<T> get() {
        return this.flowable;
    }

    public void stop() {
        this.stopped.set(true);
    }

}