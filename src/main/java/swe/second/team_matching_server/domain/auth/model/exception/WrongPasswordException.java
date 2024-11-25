package swe.second.team_matching_server.domain.auth.model.exception;

import swe.second.team_matching_server.common.enums.ResultCode;
import swe.second.team_matching_server.common.exception.BadRequestException;

public class WrongPasswordException extends BadRequestException {

    public WrongPasswordException() {
        super(ResultCode.WRONG_PASSWORD);
    }

    public WrongPasswordException(ResultCode resultCode) {
        super(resultCode);
    }

    public WrongPasswordException(ResultCode resultCode, String message) {
        super(resultCode, message);
    }

    public WrongPasswordException(ResultCode resultCode, Throwable cause) {
        super(resultCode, cause);
    }

    public WrongPasswordException(ResultCode resultCode, String message, Throwable cause) {
        super(resultCode, message, cause);
    }
}
