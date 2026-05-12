package com.acShop.exception;

import com.acShop.pojo.Result;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidFileTypeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result handleInvalidFileType(InvalidFileTypeException ex) {
        return Result.error(ex.getMessage());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    public Result handleMaxUploadSize(MaxUploadSizeExceededException ex) {
        return Result.error("File exceeds the 5MB limit.");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result handleGeneral(Exception ex) {
        return Result.error("Unexpected error: " + ex.getMessage());
    }
}
