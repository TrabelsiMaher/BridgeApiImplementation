package com.bridgeapi.service;

import com.bridgeapi.model.dto.BridgeUserResponse;
import com.bridgeapi.model.dto.CreateUserRequest;
import com.bridgeapi.model.entity.BridgeUser;
import com.bridgeapi.repository.BridgeUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class BridgeUserService {

    private final BridgeUserRepository userRepository;
    private final BridgeApiService bridgeApiService;

    @Transactional
    public Mono<BridgeUser> createUser(CreateUserRequest request) {
        log.info("Creating user with email: {}", request.getEmail());

        return userRepository.findByEmail(request.getEmail())
                .map(Mono::just)
                .orElseGet(() -> bridgeApiService.createUser(request)
                        .map(this::mapToEntity)
                        .doOnSuccess(user -> {
                            BridgeUser savedUser = userRepository.save(user);
                            log.info("User saved to database: {}", savedUser.getBridgeUuid());
                        }));
    }

    public BridgeUser getUserByUuid(String uuid) {
        return userRepository.findByBridgeUuid(uuid)
                .orElseThrow(() -> new RuntimeException("User not found with uuid: " + uuid));
    }

    public BridgeUser getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    private BridgeUser mapToEntity(BridgeUserResponse response) {
        BridgeUser user = new BridgeUser();
        user.setBridgeUuid(response.getUuid());
        user.setEmail(response.getEmail());
        user.setExternalUserId(response.getExternalUserId());
        return user;
    }
}
