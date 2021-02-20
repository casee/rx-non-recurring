package com.db.abacus.feed.skipped;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.flowables.ConnectableFlowable;

public class FeedWithSkipped<T> {

    private final ConnectableFlowable<T> connectable;
    private final Flowable<T> feed;
    private final Flowable<T> skipped;

    public FeedWithSkipped(ConnectableFlowable<T> connectable, Flowable<T> feed, Flowable<T> skipped) {
        this.connectable = connectable;
        this.feed = feed;
        this.skipped = skipped;
    }

    public void run() {
        this.connectable.connect();
    }

    public Flowable<T> feed() {
        return feed;
    }

    public Flowable<T> skipped() {
        return skipped;
    }

}