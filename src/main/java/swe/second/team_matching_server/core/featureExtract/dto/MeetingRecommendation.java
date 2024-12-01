package swe.second.team_matching_server.core.featureExtract.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MeetingRecommendation {

    private final Long meetingId;
    private final String reason;

    @Override
    public String toString() {
        return String.format("모임 ID: %d - %s", meetingId, reason);
    }
}