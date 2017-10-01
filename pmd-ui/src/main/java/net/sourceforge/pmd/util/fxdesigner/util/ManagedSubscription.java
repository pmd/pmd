/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.reactfx.EventStream;
import org.reactfx.EventStreams;
import org.reactfx.Subscription;

import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;

/**
 * Updates the compilation unit of an AST manager periodically.
 *
 * @param <U> Type of the observed events
 * @param <V> Return type of the task
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public abstract class ManagedSubscription<U, V> {

    final ObservableValue<?> observed;
    final Callable<V> taskJob;
    final Consumer<V> successHandler;
    final Consumer<? super Throwable> errorHandler;
    final Predicate<? super U> filterTest;
    private int delay;


    private boolean isRunning;
    private ExecutorService executorService;
    private Subscription subscription;


    private ManagedSubscription(ObservableValue<?> observed,
                                Callable<V> taskJob,
                                Consumer<V> successHandler,
                                Consumer<? super Throwable> errorHandler,
                                Predicate<? super U> filterTest,
                                int delay) {

        this.observed = observed;
        this.taskJob = taskJob;
        this.successHandler = successHandler;
        this.errorHandler = errorHandler;
        this.filterTest = filterTest;
        this.delay = delay;
    }


    /**
     * Subscribes to the events of the observable. Starts a thread for async handling. No-op if it's already running.
     */
    public synchronized void subscribe() {
        if (!isRunning) { // do we need synchronization?
            isRunning = true;
            executorService = Executors.newSingleThreadExecutor();
            subscription = getEventStream().filter(filterTest)
                                           .successionEnds(Duration.ofMillis(delay))
                                           .supplyTask(() -> supplyTask(taskJob))
                                           .awaitLatest(EventStreams.valuesOf(observed))
                                           .filterMap(t -> {
                                               if (t.isSuccess()) {
                                                   return Optional.ofNullable(t.get());
                                               } else {
                                                   errorHandler.accept(t.getFailure());
                                                   return Optional.empty();
                                               }
                                           })
                                           .subscribe(successHandler);
        }
    }


    public synchronized void unsubscribe() {
        if (isRunning) {
            isRunning = false;
            subscription.unsubscribe();
            executorService.shutdown();
        }
    }


    protected abstract EventStream<U> getEventStream();


    private Task<V> supplyTask(Callable<V> callable) {
        Task<V> task = new Task<V>() {
            @Override
            protected V call() throws Exception {
                return callable.call();
            }
        };

        if (!executorService.isShutdown()) {
            executorService.execute(task);
        }
        return task;
    }


    private abstract static class ManagedSubscriptionBuilder<U, V> {

        ObservableValue<U> observed;
        int delay;
        Callable<V> taskJob;
        Consumer<V> successHandler;
        Consumer<? super Throwable> errorHandler;
        Predicate<? super U> filterTest;


        public ManagedSubscriptionBuilder<U, V> observed(ObservableValue<U> val) {
            Objects.requireNonNull(val);
            observed = val;
            return this;
        }


        public ManagedSubscriptionBuilder<U, V> taskSupplier(Callable<V> val) {
            Objects.requireNonNull(val);
            taskJob = val;
            return this;
        }


        public ManagedSubscriptionBuilder<U, V> successHandler(Consumer<V> val) {
            Objects.requireNonNull(val);
            successHandler = val;
            return this;
        }


        public ManagedSubscriptionBuilder<U, V> errorHandler(Consumer<? super Throwable> val) {
            Objects.requireNonNull(val);
            errorHandler = val;
            return this;
        }


        public ManagedSubscriptionBuilder<U, V> filter(Predicate<U> val) {
            Objects.requireNonNull(val);
            filterTest = val;
            return this;
        }


        public ManagedSubscriptionBuilder<U, V> delay(int val) {
            if (val < 0) {
                throw new IllegalArgumentException();
            }
            delay = val;
            return this;
        }


        public abstract ManagedSubscription<U, V> create();


    }


}
