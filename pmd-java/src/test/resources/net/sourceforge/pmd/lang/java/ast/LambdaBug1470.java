package com.sample.test;

import rx.Observable;
import rx.Subscriber;

public class pmdTest {

    private boolean stuff;

    public Observable<Boolean> testSuper() {
        return Observable.create(
            (Subscriber<? super String> subscriber) -> {

                stuff=true;
            })
            .map(authToken -> false);
    }

    public Observable<Boolean> testSuper2() {
        return Observable.create(
            (subscriber) -> {

                stuff=true;
            })
            .map(authToken -> false);
    }
}
