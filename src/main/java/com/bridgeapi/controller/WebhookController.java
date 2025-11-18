package com.bridgeapi.controller;

import com.bridgeapi.model.dto.WebhookEvent;
import com.bridgeapi.service.WebhookService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/bridge/webhooks")
@RequiredArgsConstructor
public class WebhookController {

    private final WebhookService webhookService;

    @PostMapping
    public ResponseEntity<String> handleWebhook(
            @RequestBody WebhookEvent event,
            HttpServletRequest request) {

        String sourceIp = getClientIp(request);
        log.info("Received webhook from IP: {} with event type: {}", sourceIp, event.getType());

        if (!webhookService.validateWebhookSource(sourceIp)) {
            log.warn("Webhook rejected from unauthorized IP: {}", sourceIp);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized source");
        }

        try {
            webhookService.handleWebhook(event);
            return ResponseEntity.ok("Webhook processed successfully");
        } catch (Exception e) {
            log.error("Error processing webhook", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing webhook: " + e.getMessage());
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
