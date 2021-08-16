package com.od.helloclient;

import com.google.gson.annotations.SerializedName;

public class ChartPayload {

    @SerializedName("aggregated_bar")
    private AggregatedBar aggregatedBar;

    public ChartPayload(AggregatedBar aggregatedBar) {
        this.aggregatedBar = aggregatedBar;
    }

    public AggregatedBar getAggregatedBar() {
        return aggregatedBar;
    }

    public void setAggregatedBar(AggregatedBar aggregatedBar) {
        this.aggregatedBar = aggregatedBar;
    }

    public static class AggregatedBar {

        @SerializedName(value = "exchange_id",alternate="id")
        private String exchangeId;
        @SerializedName("open_ask")
        private Double openAsk;
        @SerializedName("close_ask")
        private Double closeAsk;
        @SerializedName("high_ask")
        private Double highAsk;
        @SerializedName("low_ask")
        private Double lowAsk;
        @SerializedName("volume")
        private Double volume;
        @SerializedName("open_bid")
        private Double openBid;
        @SerializedName("close_bid")
        private Double closeBid;
        @SerializedName("high_bid")
        private Double highBid;
        @SerializedName("low_bid")
        private Double lowBid;
        @SerializedName("timestamp")
        private Long timestamp;

        public AggregatedBar() {
        }

        public AggregatedBar(String exchangeId, Double openAsk, Double closeAsk, Double highAsk, Double lowAsk, Double volume, Double openBid, Double closeBid, Double highBid, Double lowBid, Long timestamp) {
            this.exchangeId = exchangeId;
            this.openAsk = openAsk;
            this.closeAsk = closeAsk;
            this.highAsk = highAsk;
            this.lowAsk = lowAsk;
            this.volume = volume;
            this.openBid = openBid;
            this.closeBid = closeBid;
            this.highBid = highBid;
            this.lowBid = lowBid;
            this.timestamp = timestamp;
        }

        public String getExchangeId() {
            return exchangeId;
        }

        public void setExchangeId(String exchangeId) {
            this.exchangeId = exchangeId;
        }

        public Double getOpenAsk() {
            return openAsk;
        }

        public void setOpenAsk(Double openAsk) {
            this.openAsk = openAsk;
        }

        public Double getCloseAsk() {
            return closeAsk;
        }

        public void setCloseAsk(Double closeAsk) {
            this.closeAsk = closeAsk;
        }

        public Double getHighAsk() {
            return highAsk;
        }

        public void setHighAsk(Double highAsk) {
            this.highAsk = highAsk;
        }

        public Double getLowAsk() {
            return lowAsk;
        }

        public void setLowAsk(Double lowAsk) {
            this.lowAsk = lowAsk;
        }

        public Double getVolume() {
            return volume;
        }

        public void setVolume(Double volume) {
            this.volume = volume;
        }

        public Double getOpenBid() {
            return openBid;
        }

        public void setOpenBid(Double openBid) {
            this.openBid = openBid;
        }

        public Double getCloseBid() {
            return closeBid;
        }

        public void setCloseBid(Double closeBid) {
            this.closeBid = closeBid;
        }

        public Double getHighBid() {
            return highBid;
        }

        public void setHighBid(Double highBid) {
            this.highBid = highBid;
        }

        public Double getLowBid() {
            return lowBid;
        }

        public void setLowBid(Double lowBid) {
            this.lowBid = lowBid;
        }

        public Long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Long timestamp) {
            this.timestamp = timestamp;
        }
    }
}
