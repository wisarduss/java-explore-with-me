package ru.practicum.exception;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static ru.practicum.utils.Constants.DATE_TIME_PATTERN;

@RestControllerAdvice
public class ErrorHandler {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
    private static final String OBJECT_NOT_FOUND_REASON = "The required object was not found.";
    private static final String UPDATE_CONFLICT_REASON = "For the requested operation the conditions are not met.";
    private static final String REQUEST_VALIDATION_REASON = "Incorrectly made request.";
    private static final String CONFLICT_REASON = "Integrity constraint has been violated.";

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(IdNotFoundException.class)
    public ErrorResponse handleValidationExceptions(IdNotFoundException ex) {
        return ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.name())
                .reason(OBJECT_NOT_FOUND_REASON)
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now().format(formatter))
                .build();
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ConstraintViolationException.class)
    public ErrorResponse handleValidationExceptions(ConstraintViolationException ex) {
        return ErrorResponse.builder()
                .status(HttpStatus.CONFLICT.name())
                .reason(CONFLICT_REASON)
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now().format(formatter))
                .build();
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ConflictException.class)
    public ErrorResponse handleValidationExceptions(ConflictException ex) {
        return ErrorResponse.builder()
                .status(HttpStatus.CONFLICT.name())
                .reason(UPDATE_CONFLICT_REASON)
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now().format(formatter))
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleValidationExceptions(MethodArgumentNotValidException ex) {
        return ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.name())
                .reason(REQUEST_VALIDATION_REASON)
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now().format(formatter))
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ErrorResponse handleTypeMismatchExceptions(RuntimeException ex) {
        return ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.name())
                .reason(REQUEST_VALIDATION_REASON)
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now().format(formatter))
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidationException.class)
    public ErrorResponse handleValidationExceptions(ValidationException ex) {
        return ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.name())
                .reason(REQUEST_VALIDATION_REASON)
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now().format(formatter))
                .build();
    }
}
