package com.example.websocket;

import com.example.websocket.model.RequestWebSocket;
import io.reactivex.Flowable;
import org.springframework.web.socket.WebSocketSession;

public interface MarketWebSocket {
    Flowable<String> getMessagePublishProcessor();
    void sendMessage(RequestWebSocket requestWebSocket);
    Flowable<MarketStatus> sessionStatus();
    void close();
    WebSocketSession getWebSocketSession();

    enum MarketStatus {
        STARTING,
        DISCONNECTED,
        CONNECTED
    }
}
