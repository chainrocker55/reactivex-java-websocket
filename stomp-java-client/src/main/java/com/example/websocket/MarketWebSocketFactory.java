package com.example.websocket;

import com.example.websocket.config.MarketConfig;
import com.google.gson.Gson;

import javax.inject.Inject;

public class MarketWebSocketFactory {
    private final Gson gson;
    private final MarketConfig config;

    @Inject
    public MarketWebSocketFactory(MarketConfig config, Gson gson) {
        this.config = config;
        this.gson = gson;

    }
    public MarketWebSocketImpl createSession(){
        return  new MarketWebSocketImpl(config, gson);
    }
}
