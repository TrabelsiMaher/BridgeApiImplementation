package com.bridgeapi.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bridge_items")
public class BridgeItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true, name = "item_id")
    private String itemId;

    @Column(name = "user_uuid", nullable = false)
    private String userUuid;

    @Column(name = "provider_id")
    private Integer providerId;

    @Column(name = "provider_name")
    private String providerName;

    @Column(nullable = false)
    private String status;

    @Column(name = "status_code_info")
    private String statusCodeInfo;

    @Column(name = "status_code_description")
    private String statusCodeDescription;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
