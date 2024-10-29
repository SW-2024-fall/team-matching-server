package swe.second.team_matching_server.common.exception;

import swe.second.team_matching_server.common.enums.ResultCode;

public class NotFoundException extends TeamMatchingException {
    public NotFoundException(ResultCode resultCode) {
        super(resultCode);
    }

    public NotFoundException(ResultCode resultCode, String message) {
        super(resultCode, message);
    }

    public NotFoundException(ResultCode resultCode, Throwable cause) {
        super(resultCode, cause);
    }

    public NotFoundException(ResultCode resultCode, String message, Throwable cause) {
        super(resultCode, message, cause);
    }
}
