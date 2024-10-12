package swe.second.team_matching_server.domain.history.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import swe.second.team_matching_server.common.dto.ApiResponse;
import swe.second.team_matching_server.common.enums.ResultCode;
import swe.second.team_matching_server.domain.history.model.dto.HistoryCreateDto;
import swe.second.team_matching_server.domain.history.model.entity.History;
import swe.second.team_matching_server.domain.history.service.HistoryService;

@RestController
@RequestMapping("/api/history")
public class HistoryController {
    private final HistoryService historyService;
    
    public HistoryController(HistoryService historyService) {
        this.historyService = historyService;
    }

    @PostMapping
    public ResponseEntity<String> createHistory(
        @RequestPart("photos") List<MultipartFile> photos,
        @RequestPart("history") HistoryCreateDto historyCreateDto) {
    try {
        historyService.createHistory(photos, historyCreateDto);
        return ResponseEntity.ok("History created successfully");
    } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to create history: " + e.getMessage());
        }
    }

    @GetMapping
    public ApiResponse<List<History>> getHistoriesByMeetingId(@RequestParam Long meetingId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "30") int size) {
        
        Page<History> histories = historyService.getHistoriesByMeetingId(meetingId, PageRequest.of(page, size));
        return ApiResponse.success(histories);
    }

    @GetMapping("/{historyId}")
    public ApiResponse<History> getHistoryById(@PathVariable Long historyId) {
        return ApiResponse.success(historyService.getHistoryById(historyId));
    }

    @PutMapping("/{historyId}")
    public ApiResponse<History> updateHistory(@PathVariable Long historyId, @RequestPart(name="newFiles", required=false) List<MultipartFile> newFiles, @RequestPart(name="deletedFileIds", required=false) List<String> deletedFileIds, @RequestPart("history") HistoryCreateDto historyCreateDto) {
        try {
            historyService.updateHistory(historyId, newFiles, deletedFileIds, historyCreateDto);
            return ApiResponse.success(historyService.updateHistory(historyCreateDto));
        } catch (Exception e) {
            return ApiResponse.failure(ResultCode.BAD_REQUEST);
        }
    }

    // @PutMapping("/{historyId}")
    // public ApiResponse<History> updateHistory(@PathVariable Long historyId, 
    // @RequestPart("newFiles") List<MultipartFile> newFiles,
    // @RequestPart(value = 'deletedFileIds', required=false) List<Long> deletedFileIds, 
    // @RequestPart("history") HistoryCreateDto historyCreateDto) {
    //         try {
    //             historyService.updateHistory(historyId, newFiles, deletedFileIds, historyCreateDto);
    //             return ApiResponse.success(historyService.updateHistory(historyCreateDto));
    //         } catch (Exception e) {
    //             return ApiResponse.failure(ResultCode.BAD_REQUEST);
    //         }
    // }

    @DeleteMapping
    public ApiResponse<String> deleteHistory(@RequestParam Long historyId) {
        historyService.deleteHistory(historyId);
        return ApiResponse.success("History deleted successfully");
    }
}
