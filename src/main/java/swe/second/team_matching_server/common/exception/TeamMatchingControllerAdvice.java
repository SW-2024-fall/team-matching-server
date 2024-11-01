package swe.second.team_matching_server.common.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

import swe.second.team_matching_server.common.dto.ApiResponse;
import swe.second.team_matching_server.common.enums.ResultCode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class TeamMatchingControllerAdvice {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<?> handleNotFoundException(NotFoundException e) {
        log.error("handleNotFoundException: {}", e.getMessage());
        return ApiResponse.failure(e.getResultCode());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("handleIllegalArgumentException: {}", e.getMessage());
        return ApiResponse.failure(e.getResultCode());
    }
    
    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleBadRequestException(BadRequestException e) {
        log.error("handleBadRequestException: {}", e.getMessage());
        return ApiResponse.failure(e.getResultCode());
    }
    
    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<?> handleUnauthorizedException(UnauthorizedException e) {
        log.error("handleUnauthorizedException: {}", e.getMessage());
        return ApiResponse.failure(e.getResultCode());
    }
    
    @ExceptionHandler(NoPermissionException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiResponse<?> handleNoPermissionException(NoPermissionException e) {
        log.error("handleNoPermissionException: {}", e.getMessage());
        return ApiResponse.failure(e.getResultCode());
    }

    @ExceptionHandler(InternalServerErrorException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<?> handleInternalServerErrorException(InternalServerErrorException e) {
        log.error("handleInternalServerErrorException: {}", e.getMessage());
        return ApiResponse.failure(e.getResultCode());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<?> handleException(Exception e) {
        log.error("handleException: {}", e.getMessage(), e);
        return ApiResponse.failure(ResultCode.INTERNAL_SERVER_ERROR);
    }
}
