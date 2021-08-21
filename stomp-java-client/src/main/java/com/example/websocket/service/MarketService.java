package com.example.websocket.service;

import com.example.websocket.MarketWebSocket;
import com.example.websocket.MarketWebSocketFactory;
import com.example.websocket.model.RequestWebSocket;
import com.example.websocket.model.RetryTransformer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.od.helloclient.ChartPayload;
import com.od.helloclient.ResponseWebSocket;
import com.od.helloclient.RxWebSocket;
import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class MarketService {
    private Logger logger = Logger.getLogger(RxWebSocket.class);
    private final AtomicReference<MarketWebSocket> currentChartSession = new AtomicReference<>();
    private Gson gson = new Gson();
    private final MarketWebSocketFactory marketWebSocketFactory = new MarketWebSocketFactory();
    private Disposable disposable;

    public <T> FlowableTransformer<T, T> applySchedulers() {
        return observable -> observable.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation());
    }
    public Disposable listenerCurrentChartSession(){
        return  Flowable.fromCallable(() -> {
            logger.info("Building new current chart websocket session from factory");
            return marketWebSocketFactory.createCurrentChartSession();
        })
                .doOnSubscribe(subscription -> logger.info("Subscribing to new current chart websocket session"))
                .doOnNext(currentChartSession::set)
                .switchMap(MarketWebSocket::sessionStatus)
                .filter(MarketWebSocket.MarketStatus.CONNECTED::equals)
                .compose(new RetryTransformer<>(
                        attempt -> {
                            Duration duration = Duration.ofSeconds(new BigDecimal(2).pow(Math.min(attempt - 1, 6)).intValue());
                            logger.info("Retrying current chart websocket session connection in "+ duration);
                            return Optional.of(duration);
                        }, error -> false, Schedulers.io()))
                .subscribe();
    }

    public Flowable<ResponseWebSocket<ChartPayload>> applyCurrentChartMessageFlowable() {
        Type collectionTypeCurrent = new TypeToken<ResponseWebSocket<ChartPayload>>(){}.getType();
        return currentChartSession.get().getMessagePublishProcessor()
                .onBackpressureLatest()
                .compose(applySchedulers())
                .map(res -> gson.fromJson(res, collectionTypeCurrent));
    }
    public void start(){
        String destination = "/topic/DEMO/current-bars/BTCUSD/minute5";
        RequestWebSocket requestWebSocket = new RequestWebSocket("subscribe", destination);
        disposable = listenerCurrentChartSession();

        Disposable disposable2 = applyCurrentChartMessageFlowable()
                .doOnSubscribe(e->{
                    logger.info("On subscribe "+currentChartSession.get().sessionStatus().blockingFirst() +" "+ currentChartSession.get().getWebSocketSession().isOpen() +" "+currentChartSession.get().getWebSocketSession().getId());
                })
                .doOnError(e->{
                    logger.error(e.getMessage());
                })
                .repeatWhen(attempts ->
                    {
                        return attempts.zipWith(Flowable.range(1, 5), (n, i) -> i).flatMap(i -> {
                            logger.info("delay retry by " + i + " second");
                            return Flowable.timer(i, TimeUnit.SECONDS);
                        });
                    })
                .doOnCancel(this::dispose)
                .subscribe();
                //.subscribe(e->logger.info(gson.toJson(e.getPayload())));








        //Flowable.just(currentChartSession.get()).take(1).delay(7, TimeUnit.SECONDS).subscribe(e->e.close());
        Flowable.just(disposable2).take(1).delay(7, TimeUnit.SECONDS).subscribe(e->e.dispose());
    }


    public AtomicReference<MarketWebSocket> getCurrentChartSession() {
        return currentChartSession;
    }
    public void dispose() {
        if (!disposable.isDisposed()) {
            disposable.dispose();
            currentChartSession.get().close();
        }
    }

    public static void main(String[] args) throws InterruptedException, IOException {

       MarketService service = new MarketService();

       service.start();

       Thread.sleep(600000);
    }

}
