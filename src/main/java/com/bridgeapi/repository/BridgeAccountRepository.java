package com.bridgeapi.repository;

import com.bridgeapi.model.entity.BridgeAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BridgeAccountRepository extends JpaRepository<BridgeAccount, String> {
    Optional<BridgeAccount> findByAccountId(String accountId);
    List<BridgeAccount> findByItemId(String itemId);
}
