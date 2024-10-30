package swe.second.team_matching_server.domain.meeting.model.enums;

import swe.second.team_matching_server.domain.meeting.model.exception.MeetingInvalidMeetingMemberRoleException;

public enum MeetingMemberRole {
  LEADER("리더"),
  CO_LEADER("부리더"),
  MEMBER("멤버"),
  REQUESTED("신청"),
  EXTERNAL("외부인");

  private final String role;

  MeetingMemberRole(String role) {
    this.role = role;
  }

  public static MeetingMemberRole from(String value) {
    try {
        return MeetingMemberRole.valueOf(value);
    } catch (IllegalArgumentException e) {
        throw new MeetingInvalidMeetingMemberRoleException();
    }
  }

  public String getRole() {
    return role;
  }
}
