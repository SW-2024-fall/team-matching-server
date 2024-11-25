package swe.second.team_matching_server.domain.auth.model.exception;

import swe.second.team_matching_server.common.enums.ResultCode;
import swe.second.team_matching_server.common.exception.UnauthorizedException;

public class InvalidSignatureException extends UnauthorizedException {

    public InvalidSignatureException() {
        super(ResultCode.INVALID_SIGNATURE);
    }

    public InvalidSignatureException(ResultCode resultCode) {
        super(resultCode);
    }

    public InvalidSignatureException(ResultCode resultCode, String message) {
        super(resultCode, message);
    }

    public InvalidSignatureException(ResultCode resultCode, Throwable cause) {
        super(resultCode, cause);
    }

    public InvalidSignatureException(ResultCode resultCode, String message, Throwable cause) {
        super(resultCode, message, cause);
    }
}
