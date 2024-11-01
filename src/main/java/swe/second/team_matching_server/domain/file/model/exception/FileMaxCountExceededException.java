package swe.second.team_matching_server.domain.file.model.exception;

import swe.second.team_matching_server.common.exception.BadRequestException;
import swe.second.team_matching_server.common.enums.ResultCode;
public class FileMaxCountExceededException extends BadRequestException {
    public FileMaxCountExceededException() {
        super(ResultCode.FILE_MAX_COUNT_EXCEEDED);
    }
}
