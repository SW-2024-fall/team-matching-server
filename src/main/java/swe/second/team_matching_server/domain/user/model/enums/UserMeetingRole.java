package swe.second.team_matching_server.domain.user.model.enums;

public enum UserMeetingRole {
  LEADER("리더"),
  CO_LEADER("부리더"),
  MEMBER("멤버"),
  REQUESTED("신청"),
  EXTERNAL("외부인");

  private final String role;

  UserMeetingRole(String role) {
    this.role = role;
  }

  public String getRole() {
    return role;
  }
}
