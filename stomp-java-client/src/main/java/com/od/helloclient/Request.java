package com.od.helloclient;

import com.google.gson.FieldNamingStrategy;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Field;
import java.util.Map;

public class Request {
    private String type;
    private String destination;
    @SerializedName("correlation_id")
    private String correlationId;
    private Map<String, String> body;

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

