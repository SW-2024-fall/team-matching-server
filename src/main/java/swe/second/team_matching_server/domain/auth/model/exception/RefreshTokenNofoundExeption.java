package swe.second.team_matching_server.domain.auth.model.exception;

import swe.second.team_matching_server.common.enums.ResultCode;
import swe.second.team_matching_server.common.exception.NotFoundException;

public class RefreshTokenNofoundExeption extends NotFoundException {

    public RefreshTokenNofoundExeption() {
        super(ResultCode.REFRESH_TOKEN_NOT_FOUND);
    }

    public RefreshTokenNofoundExeption(ResultCode resultCode) {
        super(resultCode);
    }

    public RefreshTokenNofoundExeption(ResultCode resultCode, String message) {
        super(resultCode, message);
    }

    public RefreshTokenNofoundExeption(ResultCode resultCode, Throwable cause) {
        super(resultCode, cause);
    }

    public RefreshTokenNofoundExeption(ResultCode resultCode, String message, Throwable cause) {
        super(resultCode, message, cause);
    }
}
