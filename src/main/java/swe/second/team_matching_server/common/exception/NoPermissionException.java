package swe.second.team_matching_server.common.exception;

import swe.second.team_matching_server.common.enums.ResultCode;

public class NoPermissionException extends TeamMatchingException {
    public NoPermissionException() {
        super(ResultCode.NO_PERMISSION);
    }

    public NoPermissionException(ResultCode resultCode) {
        super(resultCode);
    }

    public NoPermissionException(ResultCode resultCode, String message) {
        super(resultCode, message);
    }

    public NoPermissionException(ResultCode resultCode, Throwable cause) {
        super(resultCode, cause);
    }

    public NoPermissionException(ResultCode resultCode, String message, Throwable cause) {
        super(resultCode, message, cause);
    }
}
