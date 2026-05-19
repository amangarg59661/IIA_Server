package com.astro.exception;

import com.astro.constant.AppConstant;
import com.astro.util.ResponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.stream.Collectors;

@ControllerAdvice
public class ExceptionHelper {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionHelper.class);

    /** Bean Validation failures (@Valid on @RequestBody). */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationException(
            MethodArgumentNotValidException ex, WebRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        logger.warn("Validation failed [{}]: {}", request.getDescription(false), message);
        ErrorDetails errorDetails = new ErrorDetails(
                400, AppConstant.ERROR_TYPE_CODE_VALIDATION, "VALIDATION", message);
        return new ResponseEntity<>(ResponseBuilder.getErrorResponse(errorDetails), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<Object> handleInvalidInputException(
            InvalidInputException ex, WebRequest request) {
        logger.warn("Invalid input [{}]: {}", request.getDescription(false), ex.getMessage());
        return new ResponseEntity<>(ResponseBuilder.getErrorResponse(ex.getErrorDetails()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Object> handleUnauthorizedException(
            UnauthorizedException ex, WebRequest request) {
        logger.warn("Unauthorized [{}]: {}", request.getDescription(false), ex.getMessage());
        return new ResponseEntity<>(ResponseBuilder.getErrorResponse(ex.getErrorDetails()), HttpStatus.UNAUTHORIZED);
    }

    /**
     * BusinessException carries its own HTTP status code in ErrorDetails.
     * We honour that code rather than always returning 500.
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Object> handleBusinessException(
            BusinessException ex, WebRequest request) {
        ErrorDetails details = ex.getErrorDetails();
        int code = details != null ? details.getErrorCode() : 500;
        HttpStatus httpStatus = HttpStatus.resolve(code);
        if (httpStatus == null) httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        String msg = details != null ? details.getMessage() : "unknown";
        if (httpStatus.is5xxServerError()) {
            logger.error("Business error [{}]: {}", request.getDescription(false), msg, ex);
        } else {
            logger.warn("Business rule violation [{}]: {}", request.getDescription(false), msg);
        }
        return new ResponseEntity<>(ResponseBuilder.getErrorResponse(details), httpStatus);
    }

    @ExceptionHandler(FilesNotFoundException.class)
    public ResponseEntity<Object> handleFilesNotFoundException(
            FilesNotFoundException ex, WebRequest request) {
        logger.warn("File not found [{}]: {}", request.getDescription(false), ex.getMessage());
        return new ResponseEntity<>(ResponseBuilder.getErrorResponse(ex.getErrorDetails()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Object> handleMaxUploadSizeException(
            MaxUploadSizeExceededException ex, WebRequest request) {
        logger.warn("Upload size exceeded [{}]", request.getDescription(false));
        ErrorDetails errorDetails = new ErrorDetails(
                413, AppConstant.ERROR_TYPE_CODE_VALIDATION,
                "FILE_TOO_LARGE",
                "Maximum upload size exceeded. Single file limit: 50MB, Total request limit: 200MB");
        return new ResponseEntity<>(ResponseBuilder.getErrorResponse(errorDetails), HttpStatus.PAYLOAD_TOO_LARGE);
    }

    @ExceptionHandler(EmailNotSentException.class)
    public ResponseEntity<Object> handleEmailNotSentException(
            EmailNotSentException ex, WebRequest request) {
        logger.error("Email not sent [{}]: {}", request.getDescription(false), ex.getMessage(), ex);
        return new ResponseEntity<>(ResponseBuilder.getErrorResponse(ex.getErrorDetails()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception ex, WebRequest request) {
        logger.error("Unexpected error [{}]: {}", request.getDescription(false), ex.getMessage(), ex);
        ErrorDetails errorDetails = new ErrorDetails(
                AppConstant.INTER_SERVER_ERROR,
                AppConstant.ERROR_TYPE_CODE_INTERNAL,
                AppConstant.ERROR_TYPE_ERROR,
                ex.getMessage());
        return new ResponseEntity<>(ResponseBuilder.getErrorResponse(errorDetails), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
