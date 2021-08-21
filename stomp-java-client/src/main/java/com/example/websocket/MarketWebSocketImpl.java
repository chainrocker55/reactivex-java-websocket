package com.example.websocket;

import com.example.websocket.model.RequestWebSocket;
import com.google.gson.Gson;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.subjects.BehaviorSubject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.util.concurrent.ExecutionException;

public class MarketWebSocketImpl implements MarketWebSocket {
    private static final Logger LOGGER = LoggerFactory.getLogger(MarketWebSocketImpl.class);
    private WebSocketSession webSocketSession;
    private static URI URL = URI.create("wss://exchange-data-service.cryptosrvc.com");
    private final BehaviorSubject<MarketStatus> sessionStatusBehaviorSubject = BehaviorSubject.createDefault(MarketStatus.STARTING);
    private final PublishProcessor<String> messagePublishProcessor = PublishProcessor.create();
    private final Gson gson;

    public MarketWebSocketImpl(Gson gson) {
        this.gson = gson;
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        WebSocketClient webSocketClient = new StandardWebSocketClient();

        try {
            webSocketSession = webSocketClient.doHandshake(new TextWebSocketHandler() {
                @Override
                public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
                    sessionStatusBehaviorSubject.onNext(MarketStatus.DISCONNECTED);
                    sessionStatusBehaviorSubject.onError(sessionDisconnectedException());
                    messagePublishProcessor.onError(sessionDisconnectedException());
                    LOGGER.info("session call error");
                }

                @Override
                public void handleTextMessage(WebSocketSession session, TextMessage message) {
                    messagePublishProcessor.onNext(message.getPayload());
                    LOGGER.info("Established connection - {}" , message.getPayload());

                }
                @Override
                public void afterConnectionEstablished(WebSocketSession session) throws IOException {
                    LOGGER.info("Established connection - {}" , session);
                    sessionStatusBehaviorSubject.onNext(MarketStatus.CONNECTED);
                }

                @Override
                public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
                    LOGGER.error("Unsupported payload format error {}: ", exception.getMessage());
                    //messagePublishProcessor.onError(exception);
                    //sessionStatusBehaviorSubject.onNext(ShiftMarketStatus.DISCONNECTED);
                }
            },headers, URL).get();

            webSocketSession.setTextMessageSizeLimit(Integer.MAX_VALUE);

        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("Cannot connecting {}", e.getMessage());
            //messagePublishProcessor.onError(e);
            sessionStatusBehaviorSubject.onNext(MarketStatus.DISCONNECTED);
            sessionStatusBehaviorSubject.onError(e);
        }



    }
    @Override
    public void sendMessage(RequestWebSocket requestWebSocket) {
        String json = gson.toJson(requestWebSocket);
        TextMessage message = new TextMessage(json);
        try {
            webSocketSession.sendMessage(message);
        } catch (IOException e) {
            LOGGER.warn("Unsupported payload format {} error {}: ",message, e.getMessage());
        }
    }
    @Override
    public Flowable<String> getMessagePublishProcessor() {
        return messagePublishProcessor;
    }


    @Override
    public Flowable<MarketStatus> sessionStatus() {
        return sessionStatusBehaviorSubject.toFlowable(BackpressureStrategy.LATEST);
    }

    private Throwable sessionDisconnectedException() {

        return new ConnectException("Session is disconnected");
    }
    @Override
    public void close() {
        try {
            webSocketSession.close(CloseStatus.NORMAL);
            sessionStatusBehaviorSubject.onComplete();
        } catch (IOException e) {
            sessionStatusBehaviorSubject.onNext(MarketStatus.DISCONNECTED);
        }
    }

    public WebSocketSession getWebSocketSession() {
        return webSocketSession;
    }
}
