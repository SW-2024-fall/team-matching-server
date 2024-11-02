package swe.second.team_matching_server.domain.user.model.exception;

import swe.second.team_matching_server.common.enums.ResultCode;
import swe.second.team_matching_server.common.exception.NotFoundException;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException() {
        super(ResultCode.USER_NOT_FOUND);
    }
}
