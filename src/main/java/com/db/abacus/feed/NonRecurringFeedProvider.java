package com.db.abacus.feed;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.functions.Function;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import static io.reactivex.rxjava3.schedulers.Schedulers.computation;

public class NonRecurringFeedProvider <T, R> implements ScheduledFeedProvider<T, R> {

    private final Function<T, R> evaluate;
    private final Comparator<R> resultComparator;

    private final long initialDelay;
    private final long period;
    private final TimeUnit unit;

    private final Map<T, NonRecurringFeed<R>> feedMap;
    private Scheduler scheduler;

    public NonRecurringFeedProvider(Function<T, R> evaluate, Comparator<T> keyComparator, Comparator<R> resultComparator,
                                    long initialDelay, long period, TimeUnit unit) {
        this(evaluate, keyComparator, resultComparator, initialDelay, period, unit, computation());
    }

    public NonRecurringFeedProvider(Function<T, R> evaluate, Comparator<T> keyComparator, Comparator<R> resultComparator,
                                    long initialDelay, long period, TimeUnit unit, Scheduler scheduler) {
        this.evaluate = evaluate;
        this.resultComparator = resultComparator;

        this.initialDelay = initialDelay;
        this.period = period;
        this.unit = unit;

        this.feedMap = new TreeMap<>(keyComparator);
        this.scheduler = scheduler;
    }

    @Override
    public Flowable<R> feed(T key) {
        return feed(key, initialDelay, period, unit);
    }

    @Override
    public Flowable<R> feed(T key, long initialDelay, long period, @NonNull TimeUnit unit) {
        return feedMap.computeIfAbsent(key,
                k -> new NonRecurringFeed<>(() -> evaluate.apply(key), resultComparator, initialDelay, period, unit, scheduler))
                .get();
    }

    @Override
    public void disposeFeed(T key) {
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