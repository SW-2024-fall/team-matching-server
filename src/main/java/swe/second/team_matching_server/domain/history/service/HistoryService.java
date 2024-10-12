package swe.second.team_matching_server.domain.history.service;

import swe.second.team_matching_server.common.dto.ResponseDto;
import swe.second.team_matching_server.domain.history.model.dto.HistoryDto;
import swe.second.team_matching_server.domain.history.model.entity.History;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class HistoryService {
    private final HistoryRepository historyRepository;

    public HistoryService(HistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    public ResponseDto<List<History>> getHistory(Long meetingId) {
        return ResponseDto.success(historyRepository.findByMeetingId(meetingId));
    }
}
