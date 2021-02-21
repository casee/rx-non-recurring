package com.db.abacus.feed;

import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.TestScheduler;
import io.reactivex.rxjava3.subscribers.TestSubscriber;
import org.junit.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;

public class NonRecurringFeedProviderTest {

    @Test
    public void createsNonRecurrentFeedForThreeSubscribers() {
        List<Integer> sequence = Arrays.asList(1, 2, 2, 2, 3, 4, 4, 4, 4, 5, 4, 7);
        TestScheduler scheduler = new TestScheduler();
        TestSubscriber<Integer> subscriber = new TestSubscriber<>();
        ScheduledFeedProvider<Integer, Integer> feedProvider = feedProvider(sequence.iterator(), scheduler);

        feedProvider.feed(1).subscribe(subscriber);

        scheduler.advanceTimeBy(12, TimeUnit.SECONDS);
        subscriber.assertNoErrors();
        subscriber.assertValues(11, 12, 13, 14, 15, 14, 17);
    }

    private ScheduledFeedProvider<Integer, Integer> feedProvider(Iterator<Integer> sequence, Scheduler scheduler) {
        return new NonRecurringFeedProvider<>(
                a -> a*10 + sequence.next(),
                Comparator.comparing(Integer::intValue),
                Comparator.comparing(Integer::intValue),
                1, 1, SECONDS, scheduler);

    }

}