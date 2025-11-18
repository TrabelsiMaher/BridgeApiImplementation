package com.bridgeapi.repository;

import com.bridgeapi.model.entity.BridgeUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface BridgeUserRepository extends JpaRepository<BridgeUser, String> {
    Optional<BridgeUser> findByBridgeUuid(String bridgeUuid);
    Optional<BridgeUser> findByEmail(String email);
    Optional<BridgeUser> findByExternalUserId(String externalUserId);
}
