package swe.second.team_matching_server.domain.file.model.exception;

import swe.second.team_matching_server.common.exception.NotFoundException;
import swe.second.team_matching_server.common.enums.ResultCode;

public class FileNotFoundException extends NotFoundException {
  public FileNotFoundException() {
    super(ResultCode.FILE_NOT_FOUND);
  }
}
