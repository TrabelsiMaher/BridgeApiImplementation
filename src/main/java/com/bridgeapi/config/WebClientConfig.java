package com.bridgeapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient(BridgeApiConfig bridgeApiConfig) {
        return WebClient.builder()
                .baseUrl(bridgeApiConfig.getBaseUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Bridge-Version", bridgeApiConfig.getVersion())
                .defaultHeader("Client-Id", bridgeApiConfig.getClientId())
                .defaultHeader("Client-Secret", bridgeApiConfig.getClientSecret())
                .build();
    }
}
