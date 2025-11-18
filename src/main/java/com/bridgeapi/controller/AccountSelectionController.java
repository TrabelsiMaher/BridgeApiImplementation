package com.bridgeapi.controller;

import com.bridgeapi.model.entity.BridgeAccount;
import com.bridgeapi.service.AccountSelectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/bridge/accounts")
@RequiredArgsConstructor
public class AccountSelectionController {

    private final AccountSelectionService accountSelectionService;

    @PostMapping("/{accountId}/select")
    public ResponseEntity<BridgeAccount> selectAccount(
            @PathVariable String accountId,
            @RequestParam String itemId) {

        log.info("Request to select account: {} for item: {}", accountId, itemId);
        BridgeAccount selectedAccount = accountSelectionService.selectAccount(accountId, itemId);
        return ResponseEntity.ok(selectedAccount);
    }

    @PostMapping("/{accountId}/deselect")
    public ResponseEntity<Map<String, String>> deselectAccount(@PathVariable String accountId) {
        log.info("Request to deselect account: {}", accountId);
        accountSelectionService.deselectAccount(accountId);
        return ResponseEntity.ok(Map.of("message", "Account deselected successfully"));
    }

    @GetMapping("/selected")
    public ResponseEntity<BridgeAccount> getSelectedAccount(@RequestParam String itemId) {
        log.info("Request to get selected account for item: {}", itemId);
        return accountSelectionService.getSelectedAccount(itemId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/available")
    public ResponseEntity<List<BridgeAccount>> getAvailableAccounts(@RequestParam String itemId) {
        log.info("Request to get available accounts for item: {}", itemId);
        List<BridgeAccount> accounts = accountSelectionService.getAvailableAccounts(itemId);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/has-selected")
    public ResponseEntity<Map<String, Boolean>> hasSelectedAccount(@RequestParam String itemId) {
        log.info("Checking if item {} has selected account", itemId);
        boolean hasSelected = accountSelectionService.hasSelectedAccount(itemId);
        return ResponseEntity.ok(Map.of("hasSelected", hasSelected));
    }
}
