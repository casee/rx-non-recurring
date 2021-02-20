package com.db.abacus.feed.skipped;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.flowables.ConnectableFlowable;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class UnrepeatableWithSkipped<T, R> {

    private final Function<T, R> evaluate;
    private final Comparator<R> comparator;

    private final long initialDelay;
    private final long period;
    private final TimeUnit unit;

    private final Map<T, FeedWithSkipped<R>> flowables;

    public UnrepeatableWithSkipped(Function<T, R> evaluate, Comparator<T> requestComparator, Comparator<R> responseComparator,
                                   long initialDelay, long period, TimeUnit unit) {
        this.evaluate = evaluate;
        this.comparator = responseComparator;
        this.initialDelay = initialDelay;
        this.period = period;
        this.unit = unit;
        this.flowables = new TreeMap<>(requestComparator);
    }

    public FeedWithSkipped<R> feed(T request) {
        return flowables.computeIfAbsent(request, this::newFlowablePair);
    }

    private FeedWithSkipped<R> newFlowablePair(T r) {
        AtomicReference<R> prev = new AtomicReference<>();
        Flowable<R> source = Flowable.interval(initialDelay, period, unit)
                .doOnTerminate(() -> this.flowables.remove(r))
                .map(a -> evaluate.apply(r));
        ConnectableFlowable<R> cf = source.publish();

        Flowable<R> feed = cf.filter(a -> prev.get() == null || comparator.compare(a, prev.get()) != 0)
                .doOnNext(prev::set);
        Flowable<R> skipped = cf.filter(a -> prev.get() != null && comparator.compare(a, prev.get()) == 0);

        return new FeedWithSkipped<>(cf, feed, skipped);
    }

}