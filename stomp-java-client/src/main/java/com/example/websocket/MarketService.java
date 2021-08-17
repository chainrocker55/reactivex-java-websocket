package com.example.websocket;

public class MarketService {
//    disposable =  Flowable.fromCallable(() -> {
//        logger.info("Building new shiftmarkets Session from factory");
//        return shiftMarketWebSocketFactory.createSession();
//    })
//            .doOnSubscribe(subscription -> logger.info("Subscribing to new shiftmarkets Session"))
//            .doOnNext(this.shiftMarketSession::set)
//                .switchMap(ShiftMarketWebSocket::sessionStatus)
//                .map(status -> {
//        logger.info("shiftmarkets has changed status to {}", status);
//        switch (status) {
//            case CONNECTED:
//                return ServiceStatus.OK;
//            case DISCONNECTED:
//                shiftMarketSession.get().close();
//            case STARTING:
//            default:
//                return ServiceStatus.DOWN;
//        }
//    })
//            .filter(ServiceStatus.OK::equals)
//                .compose(new RetryTransformer<>(
//            attempt -> {
//        Duration duration = Duration.ofSeconds(new BigDecimal(2).pow(Math.min(attempt - 1, 6)).intValue());
//        logger.info("Retrying shiftmarket connection in {}", duration);
//        return Optional.of(duration);
//    }, error -> false, ioScheduler))
//            .subscribe();
//    Flowable.just(shiftMarketSession.get()).take(1).delay(10,TimeUnit.SECONDS).subscribe(e->e.close());
}
