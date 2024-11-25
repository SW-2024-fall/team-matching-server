package swe.second.team_matching_server.domain.auth.model.exception;

import swe.second.team_matching_server.common.enums.ResultCode;
import swe.second.team_matching_server.common.exception.BadRequestException;

public class UnsupportedTokenException extends BadRequestException {

    public UnsupportedTokenException() {
        super(ResultCode.UNSUPPORTED_TOKEN);
    }

    public UnsupportedTokenException(ResultCode resultCode) {
        super(resultCode);
    }

    public UnsupportedTokenException(ResultCode resultCode, String message) {
        super(resultCode, message);
    }

    public UnsupportedTokenException(ResultCode resultCode, Throwable cause) {
        super(resultCode, cause);
    }

    public UnsupportedTokenException(ResultCode resultCode, String message, Throwable cause) {
        super(resultCode, message, cause);
    }
}
