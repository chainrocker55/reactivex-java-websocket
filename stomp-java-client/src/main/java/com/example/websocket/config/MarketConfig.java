package com.example.websocket.config;

import com.google.inject.Inject;
import com.typesafe.config.Config;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MarketConfig {
    private final String openApiUrl;
    private final String openApiV2Url;
    private final String exchange;
    private final OKHttpConfig okHttpConfig;
    private final String authUrl;
    private final String webSocketUrl;
    private final List<String> allowTimeframe;
    private final Set<String> allowTimeframeSet;

    @Inject
    public MarketConfig(Config config) {

        Config shiftConfig = config.getConfig("market");
        this.openApiUrl = shiftConfig.getString("openApiUrl");
        this.openApiV2Url = shiftConfig.getString("openApiV2Url");
        this.authUrl = shiftConfig.getString("authUrl");
        this.exchange = shiftConfig.getString("exchange");
        this.webSocketUrl = shiftConfig.getString("wssUrl");
        this.allowTimeframe = shiftConfig.getStringList("allowTimeframe");
        this.allowTimeframeSet = new HashSet<>(allowTimeframe);
        this.okHttpConfig = new OKHttpConfig(shiftConfig.getConfig("okhttp"));

    }

    public String getOpenApiUrl() {
        return openApiUrl;
    }
    public String getOpenApiV2Url() {
        return openApiV2Url;
    }
    public String getExchange() {
        return exchange;
    }

    public List<String> getAllowTimeframe() {
        return allowTimeframe;
    }

    public String getAuthUrl() {
        return authUrl;
    }

    public Set<String> getAllowTimeframeSet() {
        return allowTimeframeSet;
    }

    public OKHttpConfig getOkHttpConfig() {
        return okHttpConfig;
    }

    public String getWebSocketUrl() {
        return webSocketUrl;
    }

    public static class OKHttpConfig {
        private final Duration connectTimeout;
        private final Duration writeTimeout;
        private final Duration readTimeout;

        public OKHttpConfig(Config config) {
            this.connectTimeout = config.getDuration("connectTimeout");
            this.writeTimeout = config.getDuration("writeTimeout");
            this.readTimeout = config.getDuration("readTimeout");
        }

        public Duration getConnectTimeout() {
            return connectTimeout;
        }

        public Duration getWriteTimeout() {
            return writeTimeout;
        }

        public Duration getReadTimeout() {
            return readTimeout;
        }
    }
}
