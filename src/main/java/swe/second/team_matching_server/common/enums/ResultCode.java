package swe.second.team_matching_server.common.enums;

public enum ResultCode {
  SUCCESS("성공"),

  BAD_REQUEST("잘못된 요청입니다."),
  UNAUTHORIZED("인증되지 않은 요청입니다."),
  NO_PERMISSION("권한이 없습니다."),
  NOT_FOUND("존재하지 않는 리소스입니다."),
  INTERNAL_SERVER_ERROR("서버 오류가 발생했습니다."),

  // meeting
  MEETING_NOT_FOUND("해당 모임이 존재하지 않습니다."),  
  MEETING_NO_PERMISSION("해당 작업을 수행할 권한이 없습니다."),
  MEETING_MEMBER_NOT_FOUND("해당 멤버가 존재하지 않습니다."),
  MEETING_REQUEST_NOT_FOUND("해당 신청이 존재하지 않습니다"),
  MEETING_ENDED("모임이 종료되었습니다."),
  MEETING_FULL("모임이 가득 찼습니다.");

  private final String message;

  ResultCode(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}
