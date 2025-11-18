package com.bridgeapi.repository;

import com.bridgeapi.model.entity.BridgeItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BridgeItemRepository extends JpaRepository<BridgeItem, String> {
    Optional<BridgeItem> findByItemId(String itemId);
    List<BridgeItem> findByUserUuid(String userUuid);
}
