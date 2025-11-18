package com.bridgeapi.service;

import com.bridgeapi.model.entity.BridgeAccount;
import com.bridgeapi.repository.BridgeAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountSelectionService {

    private final BridgeAccountRepository accountRepository;

    @Transactional
    public BridgeAccount selectAccount(String accountId) {
        BridgeAccount account = accountRepository.findByAccountId(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found: " + accountId));

        if (account.getIsSelected()) {
            log.info("Account {} is already selected", accountId);
            return account;
        }

        Optional<BridgeAccount> existingSelected = accountRepository.findByItemIdAndIsSelectedTrue(account.getItemId());
        if (existingSelected.isPresent()) {
            throw new RuntimeException("User already has a selected account. Account selection cannot be changed.");
        }

        account.setIsSelected(true);
        BridgeAccount saved = accountRepository.save(account);
        log.info("Account {} selected successfully", accountId);
        return saved;
    }

    public Optional<BridgeAccount> getSelectedAccount(String itemId) {
        return accountRepository.findByItemIdAndIsSelectedTrue(itemId);
    }

    public boolean hasSelectedAccount(String itemId) {
        return accountRepository.findByItemIdAndIsSelectedTrue(itemId).isPresent();
    }
}
