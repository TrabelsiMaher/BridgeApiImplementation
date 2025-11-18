package com.bridgeapi.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ConnectSessionResponse {

    private String uuid;

    @JsonProperty("connect_url")
    private String connectUrl;

    @JsonProperty("item_id")
    private Integer itemId;

    private Boolean success;

    @JsonProperty("error_message")
    private String errorMessage;
}
