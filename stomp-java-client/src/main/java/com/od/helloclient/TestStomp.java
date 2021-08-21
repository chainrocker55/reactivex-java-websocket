package com.od.helloclient;

import org.apache.log4j.Logger;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.Scanner;

public class TestStomp {
    private static Logger logger = Logger.getLogger(TestStomp.class);
    public static void main(String[] args) {
        StompSessionHandler handler = new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                logger.debug("after connected");
            }

            @Override
            public void handleException(StompSession s, StompCommand c, StompHeaders h, byte[] p, Throwable ex) {
                logger.error("exception", ex);
            }

            @Override
            public void handleTransportError(StompSession session, Throwable exception) {
                logger.error("transportError", exception);
            }
        };

        StandardWebSocketClient webSocketClient = new StandardWebSocketClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(webSocketClient);

        stompClient.connect("wss://exchange-data-service.cryptosrvc.com", handler)
                .addCallback(session -> logger.debug("Got session"), ex -> logger.error("Got error", ex));

        new Scanner(System.in).nextLine();
    }
}
