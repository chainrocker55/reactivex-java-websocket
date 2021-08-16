package com.od.helloclient;

import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URI;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class WebSocket {
    private static Logger LOGGER = Logger.getLogger(WebSocket.class);
    private static String URL = URI.create("wss://exchange-data-service.cryptosrvc.com").toString();
    private static WebSocketSession webSocketSession;
    public static WebSocketSession connect() throws ExecutionException, InterruptedException {
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        WebSocketClient webSocketClient = new StandardWebSocketClient();

        webSocketSession = webSocketClient.doHandshake(new TextWebSocketHandler() {
            @Override
            public void handleTextMessage(WebSocketSession session, TextMessage message) {
                LOGGER.info("received message - " + message.getPayload());
            }

            @Override
            public void afterConnectionEstablished(WebSocketSession session) throws IOException {
                LOGGER.info("Established connection - " + session);
            }

            @Override
            public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
                LOGGER.error("Cannot connecting to {}", exception);
            }
        }, headers, URI.create(URL)).get();

        webSocketSession.setTextMessageSizeLimit(Integer.MAX_VALUE);
        return webSocketSession;
    }
    public static void sendMessage(Request request) throws IOException {
        Gson gson = new Gson();
        String json = gson.toJson(request);
        TextMessage message = new TextMessage(json);
        webSocketSession.sendMessage(message);
    }
    public static void sendMessage(List<Request> requests) throws IOException {
        for (Request request : requests) {
            sendMessage(request);
        }
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {

        connect();

        String destination = "/topic/DEMO/historical-bars/BTCUSD/minute";
        Request request1 = new Request();
        request1.setType("subscribe");
        request1.setDestination(destination);

        Request request2 = new Request();
        request2.setType("get");
        request2.setCorrelationId("1");
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


        //sendMessage(Arrays.asList(request1, request2));
        sendMessage(request3);

        Thread.sleep(60000);
        webSocketSession.close();
    }
}
