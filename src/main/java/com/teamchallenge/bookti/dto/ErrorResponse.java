package com.teamchallenge.bookti.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ErrorResponse {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ssa")
    private LocalDateTime timestamp;

    @JsonProperty("status_code")
    private Integer statusCode;

    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> details;

    public ErrorResponse(Integer statusCode, String message) {
        this.timestamp = LocalDateTime.now();
        this.statusCode = statusCode;
        this.message = message;
        this.details = null;
    }

    public ErrorResponse(Integer statusCode, String message, List<String> details) {
        this.timestamp = LocalDateTime.now();
        this.statusCode = statusCode;
        this.message = message;
        this.details = details;
    }
}
