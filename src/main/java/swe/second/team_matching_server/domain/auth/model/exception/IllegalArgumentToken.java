package swe.second.team_matching_server.domain.auth.model.exception;

import swe.second.team_matching_server.common.enums.ResultCode;
import swe.second.team_matching_server.common.exception.IllegalArgumentException;

public class IllegalArgumentToken extends IllegalArgumentException {

    public IllegalArgumentToken() {
        super(ResultCode.ILLEGAL_ARGUMENT_TOKEN);
    }

    public IllegalArgumentToken(ResultCode resultCode) {
        super(resultCode);
    }

    public IllegalArgumentToken(ResultCode resultCode, String message) {
        super(resultCode, message);
    }

    public IllegalArgumentToken(ResultCode resultCode, Throwable cause) {
        super(resultCode, cause);
    }

    public IllegalArgumentToken(ResultCode resultCode, String message, Throwable cause) {
        super(resultCode, message, cause);
    }
}
