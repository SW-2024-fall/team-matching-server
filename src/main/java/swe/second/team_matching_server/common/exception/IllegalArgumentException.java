package swe.second.team_matching_server.common.exception;

import swe.second.team_matching_server.common.enums.ResultCode;

public class IllegalArgumentException extends TeamMatchingException {
    public IllegalArgumentException(ResultCode resultCode) {
        super(resultCode);
    }

    public IllegalArgumentException(ResultCode resultCode, String message) {
        super(resultCode, message);
    }

    public IllegalArgumentException(ResultCode resultCode, Throwable cause) {
        super(resultCode, cause);
    }

    public IllegalArgumentException(ResultCode resultCode, String message, Throwable cause) {
        super(resultCode, message, cause);
    }
}
