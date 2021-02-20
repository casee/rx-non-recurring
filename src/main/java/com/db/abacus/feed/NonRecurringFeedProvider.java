package com.db.abacus.feed;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Flowable;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class NonRecurringFeedProvider <T, R> implements ScheduledFeedProvider<T, R> {

    private final Function<T, R> evaluate;
    private final Comparator<R> resultComparator;

    private final long initialDelay;
    private final long period;
    private final TimeUnit unit;

    private final Map<T, NonRecurringFeed<R>> feedMap;

    public NonRecurringFeedProvider(Function<T, R> evaluate, Comparator<T> keyComparator, Comparator<R> resultComparator,
                                    long initialDelay, long period, TimeUnit unit) {
        this.evaluate = evaluate;
        this.resultComparator = resultComparator;

        this.initialDelay = initialDelay;
        this.period = period;
        this.unit = unit;

        this.feedMap = new TreeMap<>(keyComparator);
    }

    @Override
    public Flowable<R> feed(T key) {
        return feed(key, initialDelay, period, unit);
    }

    @Override
    public Flowable<R> feed(T key, long initialDelay, long period, @NonNull TimeUnit unit) {
        return feedMap.computeIfAbsent(key,
                k -> new NonRecurringFeed<>(() -> evaluate.apply(key), resultComparator, initialDelay, period, unit))
                .get();
    }

    @Override
    public void cancelFeed(T key) {
        NonRecurringFeed<R> nonRecurringFeed = feedMap.get(key);
        if (nonRecurringFeed != null) {
            nonRecurringFeed.stop();
            feedMap.remove(key);
        }
    }

    @Override
    public String toString() {
        return "Unrepeatable{" +
                "feedMap=" + feedMap +
                '}';
    }

}