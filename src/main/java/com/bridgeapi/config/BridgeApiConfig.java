package com.bridgeapi.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "bridge.api")
public class BridgeApiConfig {
    private String baseUrl;
    private String clientId;
    private String clientSecret;
    private String version;
}
