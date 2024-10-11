package swe.second.team_matching_server.common.enums;

public enum ResultCode {
  SUCCESS("성공"),

  BAD_REQUEST("잘못된 요청입니다."),
  UNAUTHORIZED("인증되지 않은 요청입니다."),
  FORBIDDEN("권한이 없습니다."),
  NOT_FOUND("존재하지 않는 리소스입니다."),
  INTERNAL_SERVER_ERROR("서버 오류가 발생했습니다.");

  private final String message;

  ResultCode(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}
