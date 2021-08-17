package com.example.websocket;

import com.example.websocket.model.RequestWebSocket;
import io.reactivex.Flowable;

public interface MarketWebSocket {
    Flowable<String> getMessagePublishProcessor();
    void sendMessage(RequestWebSocket requestWebSocket);
    Flowable<MarketStatus> sessionStatus();
    void close();

    enum MarketStatus {
        STARTING,
        DISCONNECTED,
        CONNECTED
    }
}
