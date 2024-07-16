package ru.practicum.exception;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class ErrorResponse {

    private String status;
    private String reason;
    private String message;
    private String timestamp;
}
