package com.bridgeapi.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class WebhookEvent {

    private String type;

    @JsonProperty("item_id")
    private Integer itemId;

    @JsonProperty("user_uuid")
    private String userUuid;

    private String status;

    @JsonProperty("status_code")
    private String statusCode;

    @JsonProperty("status_code_info")
    private String statusCodeInfo;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("account_id")
    private Integer accountId;

    private Double balance;

    @JsonProperty("account_types")
    private List<String> accountTypes;

    @JsonProperty("full_refresh")
    private Boolean fullRefresh;

    @JsonProperty("refresh_trigger")
    private String refreshTrigger;

    @JsonProperty("authentication_expires_at")
    private String authenticationExpiresAt;

    @JsonProperty("data_access")
    private Object dataAccess;

    @JsonProperty("nb_deleted_transactions")
    private Integer nbDeletedTransactions;

    @JsonProperty("nb_new_transactions")
    private Integer nbNewTransactions;

    @JsonProperty("nb_updated_transactions")
    private Integer nbUpdatedTransactions;

    private Object data;
}
