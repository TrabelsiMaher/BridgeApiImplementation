package com.bridgeapi.controller;

import com.bridgeapi.model.entity.BridgeAccount;
import com.bridgeapi.model.entity.BridgeItem;
import com.bridgeapi.model.entity.BridgeTransaction;
import com.bridgeapi.service.BridgeDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/bridge/data")
@RequiredArgsConstructor
public class BridgeDataController {

    private final BridgeDataService dataService;

    @PostMapping("/sync/{userUuid}")
    public Mono<ResponseEntity<String>> syncUserData(
            @PathVariable String userUuid,
            @RequestHeader("Authorization") String authorization) {

        log.info("Syncing data for user: {}", userUuid);
        String accessToken = authorization.replace("Bearer ", "");

        return dataService.syncUserData(userUuid, accessToken)
                .then(Mono.just(ResponseEntity.ok("Data sync completed successfully")));
    }

    @PostMapping("/sync/accounts")
    public Mono<ResponseEntity<List<BridgeAccount>>> syncAccounts(
            @RequestHeader("Authorization") String authorization) {

        log.info("Syncing accounts");
        String accessToken = authorization.replace("Bearer ", "");

        return dataService.syncAccounts(accessToken)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/sync/transactions")
    public Mono<ResponseEntity<List<BridgeTransaction>>> syncTransactions(
            @RequestHeader("Authorization") String authorization,
            @RequestParam(required = false) String since) {

        log.info("Syncing transactions since: {}", since);
        String accessToken = authorization.replace("Bearer ", "");

        return dataService.syncTransactions(accessToken, since)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/items/{userUuid}")
    public ResponseEntity<List<BridgeItem>> getItems(@PathVariable String userUuid) {
        log.info("Fetching items for user: {}", userUuid);
        return ResponseEntity.ok(dataService.getItemsByUserUuid(userUuid));
    }

    @GetMapping("/accounts/{itemId}")
    public ResponseEntity<List<BridgeAccount>> getAccounts(@PathVariable String itemId) {
        log.info("Fetching accounts for item: {}", itemId);
        return ResponseEntity.ok(dataService.getAccountsByItemId(itemId));
    }

    @GetMapping("/transactions/{accountId}")
    public ResponseEntity<List<BridgeTransaction>> getTransactions(@PathVariable String accountId) {
        log.info("Fetching transactions for account: {}", accountId);
        return ResponseEntity.ok(dataService.getTransactionsByAccountId(accountId));
    }
}
