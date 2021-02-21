package com.db.abacus.feed;

import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.TestScheduler;
import io.reactivex.rxjava3.subscribers.TestSubscriber;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertNotEquals;

public class NonRecurringFeedProviderTest {

    @Test
    public void createsNonRecurrentFeedForThreeSubscribers() throws InterruptedException {
        List<Integer> sequence = Arrays.asList(1, 2, 2, 2, 3, 4, 4, 4, 4, 5, 4, 7);
        TestScheduler scheduler = new TestScheduler();
        TestSubscriber<Integer> subscriber = new TestSubscriber<>();
        ScheduledFeedProvider<Integer, Integer> feedProvider = feedProvider(sequence.iterator(), scheduler);

        feedProvider.feed(1).subscribe(subscriber);

//        subscriber.assertNoErrors();
        subscriber.assertValues(11, 12, 13, 14, 15, 14, 17);
    }

    private void assertNonRecurring(List<Integer> results) {
        AtomicInteger prev = new AtomicInteger(-1);
        for (Integer result : results) {
            assertNotEquals(result.longValue(), prev.get());
            prev.set(result);
        }
    }

    private ScheduledFeedProvider<Integer, Integer> feedProvider(Iterator<Integer> sequence, Scheduler scheduler) {
        return new NonRecurringFeedProvider<>(
                a -> a*10 + sequence.next(),
                Comparator.comparing(Integer::intValue),
                Comparator.comparing(Integer::intValue),
                1, 1, SECONDS, scheduler);

    }

}