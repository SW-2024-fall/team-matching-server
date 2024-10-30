package swe.second.team_matching_server.domain.meeting.model.enums;

import swe.second.team_matching_server.domain.meeting.model.exception.MeetingInvalidMeetingTypeException;

public enum MeetingType {
  REGULAR("regular"),
  ONE_TIME("one_time");

  private final String type;

  MeetingType(String type) {
    this.type = type;
  }

  public static MeetingType from(String value) {
    try {
        return MeetingType.valueOf(value);
    } catch (IllegalArgumentException e) {
        throw new MeetingInvalidMeetingTypeException();
    }
  }

  public String getType() {
    return type;
  }
}
