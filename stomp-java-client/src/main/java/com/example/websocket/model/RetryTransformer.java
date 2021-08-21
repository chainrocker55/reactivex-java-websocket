package com.example.websocket.model;

import io.reactivex.*;
import io.reactivex.schedulers.Schedulers;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class RetryTransformer<T> implements ObservableTransformer<T, T>, FlowableTransformer<T, T> {
    private static final Logger logger = LoggerFactory.getLogger(RetryTransformer.class);
    private final Function<Integer, Optional<Duration>> delayFn;
    private final Function<Throwable, Boolean> isTerminalError;
    private final Scheduler scheduler;

    public RetryTransformer(Function<Integer, Optional<Duration>> delayFn, Function<Throwable, Boolean> isTerminalError, Scheduler scheduler) {
        this.delayFn = delayFn;
        this.isTerminalError = isTerminalError;
        this.scheduler = scheduler;
    }

    public RetryTransformer(Function<Integer, Optional<Duration>> delayFn, Function<Throwable, Boolean> isTerminalError) {
        this(delayFn, isTerminalError, Schedulers.computation());
    }
    @Override
    public ObservableSource<T> apply(Observable<T> upstream) {
        return observer -> {
            final AtomicInteger retries = new AtomicInteger(0);

            upstream
                    .doOnNext(ignore -> retries.lazySet(0))
                    .retryWhen(errors -> errors.flatMap(error -> {
                        logger.debug("Error on upstream, intercepting {}", error.getMessage());
                        if (isTerminalError.apply(error)) {
                            logger.debug("Error {} is terminal, not retrying", error.getMessage());
                            return Observable.<T>error(error);
                        } else {
                            return delayFn.apply(retries.incrementAndGet())
                                    .map(retryDelay -> {
                                        logger.debug("Retrying in {}", retryDelay);
                                        return Observable.timer(retryDelay.getSeconds(), TimeUnit.SECONDS, scheduler);
                                    }).orElse(Observable.error(error));
                        }
                    }))
                    .subscribe(observer);
        };

    }

    @Override
    public Publisher<T> apply(Flowable<T> upstream) {
        return subscriber -> {
            final AtomicInteger retries = new AtomicInteger(0);

            upstream
                    .doOnNext(ignore -> retries.lazySet(0))
                    .retryWhen(errors -> errors.flatMap(error -> {
                        logger.debug("Error on upstream, intercepting {}", error.getMessage());
                        if (isTerminalError.apply(error)) {
                            logger.debug("Error {} is terminal, not retrying", error.getMessage());
                            return Flowable.<T>error(error);
                        } else {
                            return delayFn.apply(retries.incrementAndGet())
                                    .map(retryDelay -> {
                                        logger.debug("Retrying in {}", retryDelay);
                                        return Flowable.timer(retryDelay.getSeconds(), TimeUnit.SECONDS, scheduler);
                                    }).orElse(Flowable.error(error));
                        }
                    }))
                    .subscribe(subscriber);
        };
    }
}
