package com.bridgeapi.controller;

import com.bridgeapi.model.dto.AuthTokenResponse;
import com.bridgeapi.model.dto.ConnectSessionRequest;
import com.bridgeapi.model.dto.ConnectSessionResponse;
import com.bridgeapi.model.dto.CreateUserRequest;
import com.bridgeapi.model.entity.BridgeUser;
import com.bridgeapi.service.BridgeApiService;
import com.bridgeapi.service.BridgeUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/bridge/users")
@RequiredArgsConstructor
public class BridgeUserController {

    private final BridgeUserService userService;
    private final BridgeApiService bridgeApiService;

    @PostMapping
    public Mono<ResponseEntity<BridgeUser>> createUser(@Valid @RequestBody CreateUserRequest request) {
        log.info("Received request to create user: {}", request.getEmail());
        return userService.createUser(request)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<BridgeUser> getUserByUuid(@PathVariable String uuid) {
        log.info("Fetching user by uuid: {}", uuid);
        return ResponseEntity.ok(userService.getUserByUuid(uuid));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<BridgeUser> getUserByEmail(@PathVariable String email) {
        log.info("Fetching user by email: {}", email);
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @PostMapping("/{uuid}/auth-token")
    public Mono<ResponseEntity<AuthTokenResponse>> generateAuthToken(@PathVariable String uuid) {
        log.info("Generating auth token for user: {}", uuid);
        return bridgeApiService.generateAuthToken(uuid)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/connect-session")
    public Mono<ResponseEntity<ConnectSessionResponse>> createConnectSession(
            @Valid @RequestBody ConnectSessionRequest request,
            @RequestHeader("Authorization") String authorization) {

        log.info("Creating connect session for user: {}", request.getUserUuid());

        String accessToken = authorization.replace("Bearer ", "");

        return bridgeApiService.createConnectSession(request, accessToken)
                .map(ResponseEntity::ok);
    }
}
