package swe.second.team_matching_server.domain.auth.model.exception;

import swe.second.team_matching_server.common.enums.ResultCode;
import swe.second.team_matching_server.common.exception.UnauthorizedException;

public class ExpiredTokenException extends UnauthorizedException {

    public ExpiredTokenException() {
        super(ResultCode.EXPIRED_TOKEN);
    }

    public ExpiredTokenException(ResultCode resultCode) {
        super(resultCode);
    }

    public ExpiredTokenException(ResultCode resultCode, String message) {
        super(resultCode, message);
    }

    public ExpiredTokenException(ResultCode resultCode, Throwable cause) {
        super(resultCode, cause);
    }

    public ExpiredTokenException(ResultCode resultCode, String message, Throwable cause) {
        super(resultCode, message, cause);
    }
}
