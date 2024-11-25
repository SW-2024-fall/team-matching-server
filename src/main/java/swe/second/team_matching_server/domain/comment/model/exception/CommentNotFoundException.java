package swe.second.team_matching_server.domain.comment.model.exception;

import swe.second.team_matching_server.common.exception.NotFoundException;
import swe.second.team_matching_server.common.enums.ResultCode;

public class CommentNotFoundException extends NotFoundException {
    public CommentNotFoundException() {
        super(ResultCode.COMMENT_NOT_FOUND);
    }
}
