package swe.second.team_matching_server.domain.history.controller;

import swe.second.team_matching_server.domain.history.service.HistoryService;
import swe.second.team_matching_server.common.dto.ResponseDto;
import swe.second.team_matching_server.domain.history.model.dto.HistoryDto;
import swe.second.team_matching_server.domain.history.model.entity.History;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/history")
public class HistoryController {
    private final HistoryService historyService;
    
    public HistoryController(HistoryService historyService) {
        this.historyService = historyService;
    }

    @GetMapping
    public ResponseDto<List<History>> getHistory(@RequestParam Long meetingId) {
        return historyService.getHistory(meetingId);
    }

    @GetMapping("/{historyId}")
    public ResponseDto<History> getHistoryById(@PathVariable Long historyId) {
        return historyService.getHistoryById(historyId);
    }

    @PostMapping
    public ResponseDto<History> createHistory(@RequestBody HistoryDto historyDto) {
        return historyService.createHistory(historyDto);
    }

    @PutMapping
    public ResponseDto<History> updateHistory(@RequestBody HistoryDto historyDto) {
        return historyService.updateHistory(historyDto);
    }

    @DeleteMapping
    public ResponseDto<History> deleteHistory(@RequestParam Long historyId) {
        return historyService.deleteHistory(historyId);
    }
}
