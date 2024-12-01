package swe.second.team_matching_server.core.featureExtract.exception;

import swe.second.team_matching_server.common.exception.TeamMatchingException;

import swe.second.team_matching_server.common.enums.ResultCode;

public class ContentAnalysisException extends TeamMatchingException {

    public ContentAnalysisException() {
        super(ResultCode.CONTENT_ANALYZER_ERROR);
    }

    public ContentAnalysisException(ResultCode resultCode) {
        super(resultCode);
    }

    public ContentAnalysisException(ResultCode resultCode, String message) {
        super(resultCode, message);
    }

    public ContentAnalysisException(ResultCode resultCode, Throwable cause) {
        super(resultCode, cause);
    }

    public ContentAnalysisException(ResultCode resultCode, String message, Throwable cause) {
        super(resultCode, message, cause);
    }
}
