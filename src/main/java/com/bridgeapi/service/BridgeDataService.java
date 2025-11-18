package com.bridgeapi.service;

import com.bridgeapi.model.entity.BridgeAccount;
import com.bridgeapi.model.entity.BridgeItem;
import com.bridgeapi.model.entity.BridgeTransaction;
import com.bridgeapi.repository.BridgeAccountRepository;
import com.bridgeapi.repository.BridgeItemRepository;
import com.bridgeapi.repository.BridgeTransactionRepository;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BridgeDataService {

    private final BridgeApiService bridgeApiService;
    private final BridgeItemRepository itemRepository;
    private final BridgeAccountRepository accountRepository;
    private final BridgeTransactionRepository transactionRepository;

    @Transactional
    public Mono<Void> syncUserData(String userUuid, String accessToken) {
        log.info("Starting data sync for user: {}", userUuid);

        return Mono.zip(
                syncItems(userUuid, accessToken),
                syncAccounts(accessToken),
                syncTransactions(accessToken, null)
        ).then();
    }

    @Transactional
    public Mono<List<BridgeItem>> syncItems(String userUuid, String accessToken) {
        return bridgeApiService.getItems(accessToken)
                .map(response -> {
                    List<BridgeItem> items = new ArrayList<>();
                    JsonNode resources = response.get("resources");

                    if (resources != null && resources.isArray()) {
                        resources.forEach(itemNode -> {
                            BridgeItem item = new BridgeItem();
                            item.setItemId(String.valueOf(itemNode.get("id").asInt()));
                            item.setUserUuid(userUuid);
                            item.setStatus(itemNode.get("status").asText());

                            if (itemNode.has("status_code_info")) {
                                item.setStatusCodeInfo(itemNode.get("status_code_info").asText());
                            }
                            if (itemNode.has("status_code_description")) {
                                item.setStatusCodeDescription(itemNode.get("status_code_description").asText());
                            }

                            BridgeItem savedItem = itemRepository.save(item);
                            items.add(savedItem);
                            log.info("Item saved: {}", savedItem.getItemId());
                        });
                    }

                    return items;
                });
    }

    @Transactional
    public Mono<List<BridgeAccount>> syncAccounts(String accessToken) {
        return bridgeApiService.getAccounts(accessToken)
                .map(response -> {
                    List<BridgeAccount> accounts = new ArrayList<>();
                    JsonNode resources = response.get("resources");

                    if (resources != null && resources.isArray()) {
                        resources.forEach(accountNode -> {
                            BridgeAccount account = new BridgeAccount();
                            account.setAccountId(String.valueOf(accountNode.get("id").asInt()));
                            account.setItemId(String.valueOf(accountNode.get("item_id").asInt()));
                            account.setName(accountNode.get("name").asText());
                            account.setBalance(BigDecimal.valueOf(accountNode.get("balance").asDouble()));
                            account.setCurrency(accountNode.get("currency").asText());
                            account.setType(accountNode.get("type").asText());
                            account.setStatus(accountNode.get("status").asText());

                            if (accountNode.has("iban") && !accountNode.get("iban").isNull()) {
                                account.setIban(accountNode.get("iban").asText());
                            }

                            BridgeAccount savedAccount = accountRepository.save(account);
                            accounts.add(savedAccount);
                            log.info("Account saved: {}", savedAccount.getAccountId());
                        });
                    }

                    return accounts;
                });
    }

    @Transactional
    public Mono<List<BridgeTransaction>> syncTransactions(String accessToken, String since) {
        return bridgeApiService.getTransactions(accessToken, since)
                .map(response -> {
                    List<BridgeTransaction> transactions = new ArrayList<>();
                    JsonNode resources = response.get("resources");

                    if (resources != null && resources.isArray()) {
                        resources.forEach(txNode -> {
                            BridgeTransaction transaction = new BridgeTransaction();
                            transaction.setTransactionId(String.valueOf(txNode.get("id").asInt()));
                            transaction.setAccountId(String.valueOf(txNode.get("account_id").asInt()));
                            transaction.setDescription(txNode.get("description").asText());
                            transaction.setAmount(BigDecimal.valueOf(txNode.get("amount").asDouble()));
                            transaction.setCurrency(txNode.get("currency").asText());
                            transaction.setDate(LocalDate.parse(txNode.get("date").asText()));

                            if (txNode.has("operation_type")) {
                                transaction.setOperationType(txNode.get("operation_type").asText());
                            }
                            if (txNode.has("category_id")) {
                                transaction.setCategoryId(txNode.get("category_id").asInt());
                            }
                            if (txNode.has("is_deleted")) {
                                transaction.setIsDeleted(txNode.get("is_deleted").asBoolean());
                            }

                            BridgeTransaction savedTx = transactionRepository.save(transaction);
                            transactions.add(savedTx);
                        });

                        log.info("Saved {} transactions", transactions.size());
                    }

                    return transactions;
                });
    }

    public List<BridgeAccount> getAccountsByItemId(String itemId) {
        return accountRepository.findByItemId(itemId);
    }

    public List<BridgeTransaction> getTransactionsByAccountId(String accountId) {
        return transactionRepository.findByAccountId(accountId);
    }

    public List<BridgeItem> getItemsByUserUuid(String userUuid) {
        return itemRepository.findByUserUuid(userUuid);
    }
}
