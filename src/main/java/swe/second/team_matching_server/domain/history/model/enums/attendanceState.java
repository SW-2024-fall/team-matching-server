package swe.second.team_matching_server.domain.history.model.enums;

import java.util.Arrays;
import swe.second.team_matching_server.common.exception.IllegalArgumentException;

public enum AttendanceState {
  ATTENDED("출석"),
  ABSENT("불참"),
  LATE("지각"),
  TRUANCY("무단결석");

  private final String description;

  AttendanceState(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  public static AttendanceState of(String description) {
    return Arrays.stream(AttendanceState.values())
        .filter(state -> state.getDescription().equals(description))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 출석 상태입니다. " + description));
  }
}
