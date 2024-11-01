package swe.second.team_matching_server.domain.meeting.model.enums;

public enum MeetingMemberApplicationMethod {
    FIRST_COME_FIRST_SERVED("선착순"),
    LEADER_ACCEPT("모임장 수락");

    private final String description;

    MeetingMemberApplicationMethod(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
