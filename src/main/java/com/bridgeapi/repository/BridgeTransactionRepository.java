package com.bridgeapi.repository;

import com.bridgeapi.model.entity.BridgeTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BridgeTransactionRepository extends JpaRepository<BridgeTransaction, String> {
    Optional<BridgeTransaction> findByTransactionId(String transactionId);
    List<BridgeTransaction> findByAccountId(String accountId);
    List<BridgeTransaction> findByAccountIdAndDateAfter(String accountId, LocalDate date);
}
