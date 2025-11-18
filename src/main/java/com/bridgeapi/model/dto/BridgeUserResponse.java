package com.bridgeapi.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BridgeUserResponse {
    private String uuid;
    private String email;

    @JsonProperty("external_user_id")
    private String externalUserId;
}
