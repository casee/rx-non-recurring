package com.db.abacus.feed;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;

import java.util.Comparator;

import static java.util.concurrent.TimeUnit.SECONDS;

public class HelloWorld {

    private final Action onCompete = () -> System.out.println("Done");
    private final Consumer<Throwable> onError = Throwable::printStackTrace;

    public static void main(String[] args) throws InterruptedException {
        new HelloWorld().run();
    }

    private void run() throws InterruptedException {
        ScheduledFeedProvider<String, String> feedProvider = new NonRecurringFeedProvider<>(
                a -> {
                    String r = "Hello " + a + "! " + System.currentTimeMillis() % 2;
                    System.out.println("Getting: " + r);
                    return r;
                },
                Comparator.comparing(String::toString),
                Comparator.comparing(String::toString),
                1, 1, SECONDS);

        System.out.println(feedProvider);

        Disposable subscribeRoma1 = feedProvider.feed("Roma")
                .subscribe(a -> onNext(1, a), onError, onCompete);
        Disposable subscribeRoma2 = feedProvider.feed("Roma")
                .subscribe(a -> onNext(2, a), onError, onCompete);
        Disposable subscribeJuly1 = feedProvider.feed("July")
                .subscribe(a -> onNext(3, a), onError, onCompete);
        System.out.println(feedProvider);

        Thread.sleep(5000); // <--- wait for the flow to finish

        subscribeRoma1.dispose();
        System.out.println(feedProvider);

        Thread.sleep(5000); // <--- wait for the flow to finish

        subscribeRoma2.dispose();
        System.out.println(feedProvider);

        Thread.sleep(2000); // <--- wait for the flow to finish

        feedProvider.disposeFeed("Roma");

        Disposable subscribeJuly2 = feedProvider.feed("July")
                .subscribe(a -> onNext(4, a), onError, onCompete);
        System.out.println(feedProvider);

        Thread.sleep(5000); // <--- wait for the flow to finish

        feedProvider.disposeFeed("July");
        System.out.println(feedProvider);

        Thread.sleep(2001); // <--- wait for the flow to finish

        subscribeJuly1.dispose();
        subscribeJuly2.dispose();
        System.out.println(feedProvider);

        Thread.sleep(2000); // <--- wait for the flow to finish
    }

    private void onNext(int index, String a) {
        System.out.println(System.currentTimeMillis() + " " + index + " " + a);
    }

}