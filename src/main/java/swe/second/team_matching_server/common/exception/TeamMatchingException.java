package swe.second.team_matching_server.common.exception;

import swe.second.team_matching_server.common.enums.ResultCode;

public class TeamMatchingException extends RuntimeException {
    private final ResultCode resultCode;

    protected TeamMatchingException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.resultCode = resultCode;
    }

    protected TeamMatchingException(ResultCode resultCode, String message) {
        super(message);
        this.resultCode = resultCode;
    }

    protected TeamMatchingException(ResultCode resultCode, Throwable cause) {
        super(cause);
        this.resultCode = resultCode;
    }

    protected TeamMatchingException(ResultCode resultCode, String message, Throwable cause) {
        super(message, cause);
        this.resultCode = resultCode;
    }

    public ResultCode getResultCode() {
    return resultCode;
    }
}
