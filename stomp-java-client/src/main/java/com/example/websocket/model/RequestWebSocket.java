package com.example.websocket.model;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class RequestWebSocket {
    private String type;
    private String destination;
    @SerializedName("correlation_id")
    private String correlationId;
    private Map<String, String> body;

    public RequestWebSocket() {
    }

    public RequestWebSocket(String type, String destination) {
        this.type = type;
        this.destination = destination;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public Map<String, String> getBody() {
        return body;
    }

    public void setBody(Map<String, String> body) {
        this.body = body;
    }
}
