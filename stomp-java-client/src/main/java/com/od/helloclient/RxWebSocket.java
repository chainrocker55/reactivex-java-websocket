package com.od.helloclient;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import org.apache.log4j.Logger;
import org.reactivestreams.Publisher;
import org.springframework.web.socket.*;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.ConnectException;
import java.net.URI;
import java.sql.Timestamp;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class RxWebSocket {

    private static Logger LOGGER = Logger.getLogger(RxWebSocket.class);
    private static String URL = URI.create("wss://exchange-data-service.cryptosrvc.com.com").toString();
    private static WebSocketSession webSocketSession;
    private final BehaviorSubject<String> contextIdBehaviourSubject = BehaviorSubject.create();
    private static final BehaviorSubject<MarketStatus> sessionStatusBehaviorSubject = BehaviorSubject.createDefault(MarketStatus.STARTING);
    private static final PublishProcessor<String> messagePublishProcessor = PublishProcessor.create();
    private static final Publisher<String> publisher = null;
    private static Gson gson = new Gson();
    private final AtomicReference<WebSocketSession> shiftMarketSession = new AtomicReference<>();


    public static WebSocketSession connect() throws ExecutionException, InterruptedException {
        try {
            WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
            WebSocketClient webSocketClient = new StandardWebSocketClient();

            webSocketSession = webSocketClient.doHandshake(new TextWebSocketHandler() {

                @Override
                public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
                    sessionStatusBehaviorSubject.onNext(MarketStatus.DISCONNECTED);
                }


                @Override
                protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
                    LOGGER.warn("Unsupported - this message type");
                }

                @Override
                public void handleTextMessage(WebSocketSession session, TextMessage message) {
                    messagePublishProcessor.onNext(message.getPayload());
                    //LOGGER.info(message.getPayload());
                }
                @Override
                public void afterConnectionEstablished(WebSocketSession session) throws IOException {
                    LOGGER.info("Established connection - " + session);
                    // messagePublishProcessor.onNext("Connected");

                }
                @Override
                public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
                    LOGGER.error("Cannot connecting to {}", exception);
                }
            }, headers, URI.create(URL)).get();

            webSocketSession.setTextMessageSizeLimit(Integer.MAX_VALUE);


        }catch (Exception e){
            sessionStatusBehaviorSubject.onNext(MarketStatus.DISCONNECTED);
            sessionStatusBehaviorSubject.onError(e);
        }
        return webSocketSession;
    }

    public static void sendMessage(List<Request> requests) throws IOException {
        for (Request request : requests) {
            sendMessage(request);
        }
    }

    public static void sendMessage(Request requestWebSocket) {
        String json = gson.toJson(requestWebSocket);
        TextMessage message = new TextMessage(json);

        try {
            webSocketSession.sendMessage(message);
            LOGGER.info("Send Message");
        } catch (IOException e) {
            LOGGER.error("Error {}", e);
        }
    }

    public Flowable<String> getMessagePublishProcessor() {
        return messagePublishProcessor;
    }


    public Single<String> contextIdSingle() {
        return contextIdBehaviourSubject.take(1).singleOrError();
    }


    public static Flowable<MarketStatus> sessionStatus() {
        return sessionStatusBehaviorSubject.toFlowable(BackpressureStrategy.MISSING);
    }


    public static void close() {
        try {
            webSocketSession.close(CloseStatus.NORMAL);
            sessionStatusBehaviorSubject.onNext(MarketStatus.DISCONNECTED);
            sessionStatusBehaviorSubject.onComplete();

            messagePublishProcessor.onComplete();
        } catch (IOException e) {
            sessionStatusBehaviorSubject.onNext(MarketStatus.DISCONNECTED);
        }
    }
    private Throwable sessionDisconnectedException() {
        return new ConnectException("Session is disconnected");
    }


    public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {

        PublishProcessor<String> processor = PublishProcessor.create();

        connect();

        String destination = "/topic/DEMO/historical-bars/BTCUSD/minute";
        Request request1 = new Request();
        request1.setType("subscribe");
        request1.setDestination(destination);

        Request request2 = new Request();
        request2.setType("get");
        request2.setCorrelationId(UUID.randomUUID().toString());
        request2.setDestination(destination);
        Map<String, String> body = new HashMap<>();

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        body.put("start_time","1628074203000");
        body.put("end_time",String.valueOf(timestamp.getTime()));
        request2.setBody(body);

        String destination2 = "/topic/DEMO/current-bars/BTCUSD/minute";
        Request request3 = new Request();
        request3.setType("subscribe");
        request3.setDestination(destination2);

        String destination3 = "/topic/DEMO/current-bars/BTCUSD/minute5";
        Request request4 = new Request();
        request4.setType("subscribe");
        request4.setDestination(destination3);


        //sendMessage(Arrays.asList(request1, request2));
        //Thread.sleep(500);
        Type collectionTypeHistory = new TypeToken<ResponseWebSocket<List<ChartPayload>>>(){}.getType();
        Flowable<ResponseWebSocket<List<ChartPayload>>> resultHistory = messagePublishProcessor
               .subscribeOn(Schedulers.io())
               .observeOn(Schedulers.computation())
               .doOnSubscribe(s-> sendMessage(Arrays.asList(request1, request2)))
                .onBackpressureLatest()
                .map(res->gson.fromJson(res, ResponseWebSocket.class))

                .filter(res-> res.getSource().equals(request1.getDestination())&&res.getHeaders().getCorrelationId().equals(request2.getCorrelationId()))
               .doOnError(throwable -> LOGGER.error(throwable.getMessage()))
                .map(res -> gson.fromJson(gson.toJson(res), collectionTypeHistory));

        //resultHistory.subscribe(res -> LOGGER.info(res.getSource()+ " " +res.getHeaders().getCorrelationId()));

        //sendMessage(request4);


        sendMessage(request3);


        Type collectionTypeCurrent = new TypeToken<ResponseWebSocket<ChartPayload>>(){}.getType();
        Flowable<ResponseWebSocket<ChartPayload>> resultCurrent = messagePublishProcessor
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .doOnSubscribe(s-> {
                    LOGGER.info("doOnSubscribe");
//                    Boolean result = messagePublishProcessor
//                            .map(res->gson.fromJson(res, ResponseWebSocket.class))
//                            .timeout(2, TimeUnit.SECONDS)
//                            .take(1)
//                            .any(e -> request3.getDestination().equals(e.getSource()))
//                            .onErrorReturn(e -> false)
//                            .blockingGet();

//                    if(!result){
//                        LOGGER.info("do if no destination");
//                        sendMessage(request3);
//                    }

                })
                .onBackpressureLatest()
                .map(res->gson.fromJson(res, ResponseWebSocket.class))
                .filter(res-> res.getSource().equals(request3.getDestination()))
                .doOnError(throwable -> LOGGER.error(throwable.getMessage()))
                .map(res -> gson.fromJson(gson.toJson(res), collectionTypeCurrent));

        resultCurrent.throttleLast(1, TimeUnit.SECONDS).subscribe(res -> {
            LOGGER.info("Receive data");
           // LOGGER.info(gson.toJson(res));
        });
//        disposable =  Flowable.fromCallable(() -> {
//            logger.info("Building new shiftmarkets Session from factory");
//            return shiftMarketWebSocketFactory.createSession();
//        })
//                .doOnSubscribe(subscription -> logger.info("Subscribing to new shiftmarkets Session"))
//                .doOnNext(this.shiftMarketSession::set)
//                .switchMap(ShiftMarketWebSocket::sessionStatus)
//                .map(status -> {
//                    logger.info("shiftmarkets has changed status to {}", status);
//                    switch (status) {
//                        case CONNECTED:
//                            return ServiceStatus.OK;
//                        case DISCONNECTED:
//                            shiftMarketSession.get().close();
//                        case STARTING:
//                        default:
//                            return ServiceStatus.DOWN;
//                    }
//                })
//                .filter(ServiceStatus.OK::equals)
//                .compose(new RetryTransformer<>(
//                        attempt -> {
//                            Duration duration = Duration.ofSeconds(new BigDecimal(2).pow(Math.min(attempt - 1, 6)).intValue());
//                            logger.info("Retrying shiftmarket connection in {}", duration);
//                            return Optional.of(duration);
//                        }, error -> false, ioScheduler))
//                .subscribe();
//
//        Flowable.just(shiftMarketSession.get()).take(1).delay(10, TimeUnit.SECONDS).subscribe(e->e.close());


        Flowable.fromPublisher(sessionStatus()).compose(new RetryTransformer<>(
                attempt -> {
                    Duration duration = Duration.ofSeconds(new BigDecimal(2).pow(Math.min(attempt - 1, 6)).intValue());
                    LOGGER.info("Retrying shiftmarket connection in "+duration);
                    return Optional.of(duration);
                }, error -> false, Schedulers.io()))
                .subscribe();
//        Flowable.fromCallable(()->webSocketSession.isOpen())
//                .takeUntil(e)
//                .subscribe(e -> {
//            LOGGER.info("check connection "+e);
//
//           if(!e){
//               LOGGER.info("Retry connection");
//               connect();
//           }
//        });




        Thread.sleep(4000);
        webSocketSession.close();
        LOGGER.info("Close connection");
        Thread.sleep(60000);
        webSocketSession.close();
    }



}
