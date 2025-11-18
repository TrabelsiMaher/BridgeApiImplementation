package com.bridgeapi.service;

import com.bridgeapi.model.dto.*;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class BridgeApiService {

    private final WebClient webClient;

    public Mono<BridgeUserResponse> createUser(CreateUserRequest request) {
        log.info("Creating Bridge user with email: {}", request.getEmail());

        return webClient.post()
                .uri("/aggregation/users")
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatus::isError, clientResponse -> {
                    log.error("Error creating user: {}", clientResponse.statusCode());
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(errorBody -> Mono.error(
                                    new RuntimeException("Bridge API error: " + errorBody)));
                })
                .bodyToMono(BridgeUserResponse.class)
                .doOnSuccess(response -> log.info("User created successfully: {}", response.getUuid()));
    }

    public Mono<AuthTokenResponse> generateAuthToken(String userUuid) {
        log.info("Generating auth token for user: {}", userUuid);

        AuthTokenRequest request = new AuthTokenRequest(userUuid);

        return webClient.post()
                .uri("/aggregation/authorization/token")
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatus::isError, clientResponse -> {
                    log.error("Error generating token: {}", clientResponse.statusCode());
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(errorBody -> Mono.error(
                                    new RuntimeException("Bridge API error: " + errorBody)));
                })
                .bodyToMono(AuthTokenResponse.class)
                .doOnSuccess(response -> log.info("Token generated successfully"));
    }

    public Mono<ConnectSessionResponse> createConnectSession(ConnectSessionRequest request, String accessToken) {
        log.info("Creating connect session for user: {}", request.getUserUuid());

        return webClient.post()
                .uri("/aggregation/connect-sessions")
                .header("Authorization", "Bearer " + accessToken)
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatus::isError, clientResponse -> {
                    log.error("Error creating connect session: {}", clientResponse.statusCode());
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(errorBody -> Mono.error(
                                    new RuntimeException("Bridge API error: " + errorBody)));
                })
                .bodyToMono(ConnectSessionResponse.class)
                .doOnSuccess(response -> log.info("Connect session created: {}", response.getUuid()));
    }

    public Mono<JsonNode> getAccounts(String accessToken) {
        log.info("Fetching accounts from Bridge API");

        return webClient.get()
                .uri("/aggregation/accounts")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .onStatus(HttpStatus::isError, clientResponse -> {
                    log.error("Error fetching accounts: {}", clientResponse.statusCode());
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(errorBody -> Mono.error(
                                    new RuntimeException("Bridge API error: " + errorBody)));
                })
                .bodyToMono(JsonNode.class)
                .doOnSuccess(response -> log.info("Accounts fetched successfully"));
    }

    public Mono<JsonNode> getTransactions(String accessToken, String since) {
        log.info("Fetching transactions from Bridge API");

        String uri = since != null
                ? "/aggregation/transactions?since=" + since
                : "/aggregation/transactions";

        return webClient.get()
                .uri(uri)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .onStatus(HttpStatus::isError, clientResponse -> {
                    log.error("Error fetching transactions: {}", clientResponse.statusCode());
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(errorBody -> Mono.error(
                                    new RuntimeException("Bridge API error: " + errorBody)));
                })
                .bodyToMono(JsonNode.class)
                .doOnSuccess(response -> log.info("Transactions fetched successfully"));
    }

    public Mono<JsonNode> getItems(String accessToken) {
        log.info("Fetching items from Bridge API");

        return webClient.get()
                .uri("/aggregation/items")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .onStatus(HttpStatus::isError, clientResponse -> {
                    log.error("Error fetching items: {}", clientResponse.statusCode());
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(errorBody -> Mono.error(
                                    new RuntimeException("Bridge API error: " + errorBody)));
                })
                .bodyToMono(JsonNode.class)
                .doOnSuccess(response -> log.info("Items fetched successfully"));
    }
}
