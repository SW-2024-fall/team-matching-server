package swe.second.team_matching_server.common.exception;

import swe.second.team_matching_server.common.enums.ResultCode;

public class UnauthorizedException extends TeamMatchingException {
    public UnauthorizedException() {
        super(ResultCode.UNAUTHORIZED);
    }

    public UnauthorizedException(ResultCode resultCode) {
        super(resultCode);
    }

    public UnauthorizedException(ResultCode resultCode, String message) {
        super(resultCode, message);
    }

    public UnauthorizedException(ResultCode resultCode, Throwable cause) {
        super(resultCode, cause);
    }

    public UnauthorizedException(ResultCode resultCode, String message, Throwable cause) {
        super(resultCode, message, cause);
    }
}
