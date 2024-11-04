package swe.second.team_matching_server.domain.meeting.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import swe.second.team_matching_server.common.dto.ApiResponse;
import swe.second.team_matching_server.domain.history.model.dto.HistoryElement;
import swe.second.team_matching_server.domain.history.service.HistoryService;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@Tag(name = "MeetingHistory", description = "모임 히스토리 API")
@RestController
@RequestMapping("/api/meetings/{meetingId}/histories")
public class MeetingHistoryController {
    private final HistoryService historyService;

    public MeetingHistoryController(HistoryService historyService) {
        this.historyService = historyService;
    }

    @GetMapping
    @Operation(summary = "모임 활동내역 조회", description = "모임 활동내역을 조회합니다.")
    public ApiResponse<List<HistoryElement>> findAllByMeetingId(
        @PathVariable Long meetingId, Pageable pageable) {
        // 추후 token에서 user 정보를 가져오도록 수정해야함
        String userId = "test";

        return ApiResponse.success(historyService.findAllByMeetingId(pageable, meetingId, userId));
    }
}
