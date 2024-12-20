package swe.second.team_matching_server.common.enums;

public enum ResultCode {
  SUCCESS("성공"),

  BAD_REQUEST("잘못된 요청입니다."),
  UNAUTHORIZED("인증되지 않은 요청입니다."),
  NO_PERMISSION("권한이 없습니다."),
  NOT_FOUND("존재하지 않는 리소스입니다."),
  INTERNAL_SERVER_ERROR("서버 오류가 발생했습니다."),
  ILLEGAL_ARGUMENT("잘못된 인자입니다."),

  // meeting
  MEETING_NOT_FOUND("해당 모임이 존재하지 않습니다."),  
  MEETING_NO_PERMISSION("해당 작업을 수행할 권한이 없습니다."),
  MEETING_MEMBER_NOT_FOUND("해당 멤버가 존재하지 않습니다."),
  MEETING_REQUEST_NOT_FOUND("해당 신청이 존재하지 않습니다"),
  MEETING_ENDED("모임이 종료되었습니다."),
  MEETING_FULL("모임이 가득 찼습니다."),
  MEETING_INVALID_PARTICIPANTS("최소 참여자 수는 2명 이상, 최대 참여자 수는 99명 이하입니다."),
  MEETING_INVALID_DATE("유효하지 않은 날짜입니다."),
  MEETING_INVALID_CATEGORY("유효하지 않은 카테고리입니다."),
  MEETING_INVALID_MEETING_TYPE("유효하지 않은 모임 유형입니다."),
  MEETING_INVALID_MEETING_MEMBER_ROLE("유효하지 않은 모임 멤버 권한입니다."),
  MEETING_INVALID_DAYS("반복 모임은 최소 하루 이상 반복되어야 합니다."),
  MEETING_LEADER_LEAVE("리더는 탈퇴할 수 없습니다."),

  // user
  USER_NOT_FOUND("해당 유저가 존재하지 않습니다."),
      
  // auth
  REFRESH_TOKEN_NOT_FOUND("리프레시 토큰이 존재하지 않습니다."),
  WRONG_PASSWORD("비밀번호가 일치하지 않습니다."),
  INVALID_TOKEN("유효하지 않은 토큰입니다."),
  EXPIRED_TOKEN("만료된 토큰입니다."),
  INVALID_SIGNATURE("서명 검증 실패"),
  UNSUPPORTED_TOKEN("지원하지 않는 토큰입니다."),
  ILLEGAL_ARGUMENT_TOKEN("토큰이 비어있거나 잘못되었습니다."),

  // file
  FILE_NOT_FOUND("해당 파일이 존재하지 않습니다."),
  FILE_MAX_COUNT_EXCEEDED("파일은 최대 5개까지 업로드 가능합니다."),


  // history
  HISTORY_NO_ACCESS("해당 히스토리에 접근할 권한이 없습니다."),
  HISTORY_NO_PERMISSION("히스토리 생성 및 수정/삭제는 리더 및 부리더만 가능합니다."),
  HISTORY_NOT_FOUND("해당 히스토리가 존재하지 않습니다."),
  HISTORY_INVALID_ATTENDANCE_HISTORY("모든 멤버가 출석 히스토리를 가지고 있어야 합니다."),

  // comment
  COMMENT_NOT_FOUND("해당 댓글이 존재하지 않습니다."),

  // content analyzer
  CONTENT_ANALYZER_ERROR("콘텐츠 분석 오류가 발생했습니다."),
  MEETING_ANALYSIS_ERROR("모임 분석 오류가 발생했습니다."),
  MEMBER_ANALYSIS_ERROR("멤버 분석 오류가 발생했습니다."),
  HISTORY_ANALYSIS_ERROR("히스토리 분석 오류가 발생했습니다.");

  private final String message;

  ResultCode(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}
