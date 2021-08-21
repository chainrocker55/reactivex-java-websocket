package com.example.websocket;

import com.example.websocket.model.RequestWebSocket;
import com.google.gson.Gson;

public class MarketWebSocketFactory {
    private final Gson gson = new Gson();
    public MarketWebSocketImpl createSession(){
        return  new MarketWebSocketImpl(gson);
    }
    public MarketWebSocketImpl createCurrentChartSession(){

        MarketWebSocketImpl session = createSession();
        String destination = "/topic/DEMO/current-bars/BTCUSD/minute5";
        String destination2 = "/topic/DEMO/current-bars/BTCUSD/minute";
        String destination3 = "/topic/DEMO/current-bars/BTCUSD/minute15";
        RequestWebSocket requestWebSocket = new RequestWebSocket("subscribe", destination);
        session.sendMessage(requestWebSocket);
//        requestWebSocket.setDestination(destination2);
//        session.sendMessage(requestWebSocket);
//        requestWebSocket.setDestination(destination3);
//        session.sendMessage(requestWebSocket);
        return session;
    }
}
