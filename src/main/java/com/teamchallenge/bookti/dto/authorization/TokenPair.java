package com.teamchallenge.bookti.dto.authorization;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
public class TokenPair {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ssa")
    private LocalDateTime timestamp;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("refresh_token")
    private String refreshToken;
}
