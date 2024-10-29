package swe.second.team_matching_server.common.exception;

import swe.second.team_matching_server.common.enums.ResultCode;

public class InternalServerException extends TeamMatchingException {
    public InternalServerException() {
        super(ResultCode.INTERNAL_SERVER_ERROR);
    }

    public InternalServerException(ResultCode resultCode) {
        super(resultCode);
    }

    public InternalServerException(ResultCode resultCode, String message) {
        super(resultCode, message);
    }

    public InternalServerException(ResultCode resultCode, Throwable cause) {
        super(resultCode, cause);
    }

    public InternalServerException(ResultCode resultCode, String message, Throwable cause) {
        super(resultCode, message, cause);
    }
}
