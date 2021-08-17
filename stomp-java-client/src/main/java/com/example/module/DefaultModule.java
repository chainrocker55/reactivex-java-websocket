package com.example.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class DefaultModule extends AbstractModule {
    @Override
    protected void configure() {
       // install(new OkHttpClientModule());
    }

    @Provides
    @Singleton
    public Config config() {
        return ConfigFactory.load().resolve();
    }
//
//    @Provides
//    @Singleton
//    public ShiftMarketWebSocketFactory shiftMarketWebSocketFactory(ShiftMarketConfig config, Gson gson) {
//        return new ShiftMarketWebSocketFactory(config, gson);
//    }
//    @Provides
//    @Singleton
//    public ShiftMarketService shiftMarketServiceImpl(ShiftMarketConfig config, ShiftMarketOpenApiService shiftMarketOpenApiService, ShiftMarketOpenApiV2Service shiftMarketOpenApiV2Service, ShiftMarketWebSocketFactory shiftMarketWebSocketFactory, Gson gson) {
//        return new ShiftMarketServiceImpl(config, shiftMarketOpenApiService, shiftMarketOpenApiV2Service, shiftMarketWebSocketFactory, gson);
//    }
//    @Provides
//    @Singleton
//    public ShiftMarketOpenApiService shiftMarketOpenApiService(ShiftMarketConfig config, OkHttpClient okHttpClient, ShiftMarketAuthenticationHandler tokenRefreshInterceptor) {
//        ShiftMarketConfig.OKHttpConfig okHttpConfig = config.getOkHttpConfig();
//        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//        OkHttpClient.Builder okHttpClientBuilder = okHttpClient.newBuilder()
//                .connectTimeout(okHttpConfig.getConnectTimeout().getSeconds(), TimeUnit.SECONDS)
//                .writeTimeout(okHttpConfig.getWriteTimeout().getSeconds(), TimeUnit.SECONDS)
//                .readTimeout(okHttpConfig.getReadTimeout().getSeconds(), TimeUnit.SECONDS)
//                .addInterceptor(interceptor)
//                .authenticator(tokenRefreshInterceptor)
//                .addInterceptor(tokenRefreshInterceptor);
//        Retrofit retrofit = (new retrofit2.Retrofit.Builder())
//                .baseUrl(config.getOpenApiUrl())
//                .client(okHttpClientBuilder.build())
//                .addConverterFactory(GsonConverterFactory.create())
//                .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
//                .build();
//        return retrofit.create(ShiftMarketOpenApiService.class);
//    }
}
