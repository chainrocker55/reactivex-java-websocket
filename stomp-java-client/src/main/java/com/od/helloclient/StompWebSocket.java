package com.od.helloclient;

import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class StompWebSocket {
    private static Logger logger = Logger.getLogger(StompWebSocket.class);
    private final static WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
    public static void main(String[] args) throws InterruptedException, ExecutionException {

//        List<Transport> transports = new ArrayList<>();
//        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
//        transports.add(new RestTemplateXhrTransport(new RestTemplate()));
//
//        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
//        taskScheduler.afterPropertiesSet();
        Gson gson = new Gson();
        URI url = URI.create("wss://exchange-data-service.cryptosrvc.com");
        String req = "{ \"type\" : \"subscribe\", \"destination\": \"/topic/DEMO/orderbook-BTCUSD\" }";
//        String token = "eyJraWQiOiJKOEVZQmVDREtkMXlMY3hldDkxdHJsOUROdjZYXC9WenZGNE9iSmpvWUs5OD0iLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJhYWFjMzQ2Mi0zYTQ5LTRhM2ItOGViYS04MzJkYTUxNTRhZjAiLCJldmVudF9pZCI6ImI2ZmJiYjI5LTYzNmMtNDNmYS05YzMxLTlhZWM2ODAxNjE4ZCIsInRva2VuX3VzZSI6ImFjY2VzcyIsInNjb3BlIjoiYXdzLmNvZ25pdG8uc2lnbmluLnVzZXIuYWRtaW4gb3BlbmlkIHByb2ZpbGUgZW1haWwiLCJhdXRoX3RpbWUiOjE2MjgxNTU0MTAsImlzcyI6Imh0dHBzOlwvXC9jb2duaXRvLWlkcC51cy1lYXN0LTEuYW1hem9uYXdzLmNvbVwvdXMtZWFzdC0xX3BxalZMZVpnSCIsImV4cCI6MTYyODE1OTAxMCwiaWF0IjoxNjI4MTU1NDEwLCJ2ZXJzaW9uIjoyLCJqdGkiOiJmZTRlN2MzZC1jZDEwLTRlNWEtYjJkYi1kZWJjZGExY2ZmOGQiLCJjbGllbnRfaWQiOiI0OGx1NGIxa2Y4MDU4cXAzcXA0bzcxa3F1YyIsInVzZXJuYW1lIjoiYWFhYzM0NjItM2E0OS00YTNiLThlYmEtODMyZGE1MTU0YWYwIn0.U4sy8eYjrMQt48aLicB2uaahA3lT3NMc3a6IVigOMY31uEoSDYWOqFBKbBKbT98J5eYRMI44V_beFI30uG8aF0vPeliG2yIEDombJFh84latLGKczydVNDtatgA6FOGfRnrFUcp_jM44NI5Ltps8_p9w7V_B5H3DFEhn4CIbHOYZ1sdTXkcgeDU8grUIm9_C0hvGH84g-GAbtEI_2-lyguyzUGvJHGEpUKhnQX9-nFiYfnyrszY1rjEFJ0pyBDjE6LJ7qtEZOrMb3La2qXNMKJsoaX21xV6G8cUpJUwazWuuJ_w0H0Y8v5m0sISqNBh_BBi1iEGWISJzFzCgMi22Xw";
//        headers.set("Authorization", "Bearer " + token);
//        headers.set("accept-version","1.1,1.0");
//        headers.set("heart-beat","110000,10000");

        headers.set("Upgrade","websocket");
        headers.setConnection("Upgrade");


//        SockJsClient sockJsClient = new SockJsClient(transports);


        //sockJsClient.setMessageCodec(new Jackson2SockJsMessageCodec());

        WebSocketClient webSocketClient = new StandardWebSocketClient();

        WebSocketStompClient stompClient = new WebSocketStompClient(webSocketClient);
//        stompClient.setDefaultHeartbeat(new long[] {10000,10000});
//        stompClient.setTaskScheduler(taskScheduler);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        stompClient.connect(url.toString(),headers, new SessionHandler()).get();



//
//        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
//        stompClient.setTaskScheduler(taskScheduler); // for heartbeats
//        stompClient.setMessageConverter(new StringMessageConverter());
//        stompClient.connect(stompUrl, new SessionHandler()).get();
        new Scanner(System.in).nextLine();
       // Thread.sleep(60000);
    }
    public static class SessionHandler extends StompSessionHandlerAdapter {
        @Override
        public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
            // I'd expect it to print this out, but it doesn't get here
            logger.info("Connected");
            session.subscribe("/topic/DEMO/orderbook-BTCUSD",this);
            //session.s
        }

        @Override
        public void handleException(StompSession stompSession, StompCommand stompCommand, StompHeaders stompHeaders, byte[] bytes, Throwable throwable) {
            logger.info("Connected");
        }

        @Override
        public void handleTransportError(StompSession stompSession, Throwable throwable) {
            logger.error("Connected {}",throwable);
        }

        @Override
        public Type getPayloadType(StompHeaders stompHeaders) {
            logger.info("Connected");
            return Message.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            //Message msg = (Message) payload;
            logger.info(payload);

        }
    }
}
