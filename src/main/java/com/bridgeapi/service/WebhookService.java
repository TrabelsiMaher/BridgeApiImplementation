package com.bridgeapi.service;

import com.bridgeapi.model.dto.WebhookEvent;
import com.bridgeapi.model.entity.BridgeItem;
import com.bridgeapi.repository.BridgeItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookService {

    private final BridgeItemRepository itemRepository;

    @Transactional
    public void handleWebhook(WebhookEvent event) {
        log.info("Processing webhook event type: {} for item: {}", event.getType(), event.getItemId());

        switch (event.getType()) {
            case "item.status.updated":
                handleItemStatusUpdate(event);
                break;
            case "item.refresh.completed":
                handleItemRefreshCompleted(event);
                break;
            case "item.refresh.failed":
                handleItemRefreshFailed(event);
                break;
            case "item.error":
                handleItemError(event);
                break;
            default:
                log.warn("Unknown webhook event type: {}", event.getType());
        }
    }

    private void handleItemStatusUpdate(WebhookEvent event) {
        log.info("Handling item status update for item: {}", event.getItemId());

        Optional<BridgeItem> itemOpt = itemRepository.findByItemId(String.valueOf(event.getItemId()));

        if (itemOpt.isPresent()) {
            BridgeItem item = itemOpt.get();
            item.setStatus(event.getStatus());
            item.setStatusCodeInfo(event.getStatusCodeInfo());
            itemRepository.save(item);
            log.info("Item status updated successfully");
        } else {
            log.warn("Item not found: {}", event.getItemId());
        }
    }

    private void handleItemRefreshCompleted(WebhookEvent event) {
        log.info("Item refresh completed for item: {}", event.getItemId());
        handleItemStatusUpdate(event);
    }

    private void handleItemRefreshFailed(WebhookEvent event) {
        log.error("Item refresh failed for item: {}", event.getItemId());
        handleItemStatusUpdate(event);
    }

    private void handleItemError(WebhookEvent event) {
        log.error("Item error occurred for item: {}", event.getItemId());
        handleItemStatusUpdate(event);
    }

    public boolean validateWebhookSource(String sourceIp) {
        String[] validIps = {"63.32.31.5", "52.215.247.62", "34.249.92.209"};

        for (String validIp : validIps) {
            if (validIp.equals(sourceIp)) {
                return true;
            }
        }

        log.warn("Invalid webhook source IP: {}", sourceIp);
        return false;
    }
}
