package com.od.helloclient;

import com.google.gson.annotations.SerializedName;

public class Headers {
    @SerializedName("correlation_id")
    private String correlationId;

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }
}
