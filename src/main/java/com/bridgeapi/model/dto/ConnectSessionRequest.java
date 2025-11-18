package com.bridgeapi.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ConnectSessionRequest {

    @JsonProperty("user_uuid")
    private String userUuid;

    @JsonProperty("user_email")
    private String userEmail;

    @JsonProperty("redirect_url")
    private String redirectUrl;

    @JsonProperty("prefill_email")
    private String prefillEmail;
}
