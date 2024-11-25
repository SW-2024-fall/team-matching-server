package swe.second.team_matching_server.domain.auth.model.exception;

import swe.second.team_matching_server.common.enums.ResultCode;
import swe.second.team_matching_server.common.exception.UnauthorizedException;

public class InvalidTokenException extends UnauthorizedException {

    public InvalidTokenException() {
        super(ResultCode.INVALID_TOKEN);
    }

    public InvalidTokenException(ResultCode resultCode) {
        super(resultCode);
    }

    public InvalidTokenException(ResultCode resultCode, String message) {
        super(resultCode, message);
    }

    public InvalidTokenException(ResultCode resultCode, Throwable cause) {
        super(resultCode, cause);
    }

    public InvalidTokenException(ResultCode resultCode, String message, Throwable cause) {
        super(resultCode, message, cause);
    }
}
