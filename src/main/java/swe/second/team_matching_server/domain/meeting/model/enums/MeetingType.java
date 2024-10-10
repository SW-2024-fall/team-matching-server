package swe.second.team_matching_server.domain.meeting.model.enums;

public enum MeetingType {
  REGULAR("regular"),
  ONE_TIME("one_time");

  private final String type;

  MeetingType(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }
}
