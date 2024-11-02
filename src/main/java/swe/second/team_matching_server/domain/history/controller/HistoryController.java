package swe.second.team_matching_server.domain.history.controller;

import swe.second.team_matching_server.domain.history.service.HistoryService;
import swe.second.team_matching_server.common.dto.ApiResponse;
import swe.second.team_matching_server.domain.history.model.dto.HistoryCreateDto;
import swe.second.team_matching_server.domain.history.model.dto.HistoryElement;
import swe.second.team_matching_server.domain.history.model.dto.HistoryResponse;
import swe.second.team_matching_server.domain.history.model.dto.HistoryUpdateDto;
import swe.second.team_matching_server.domain.file.model.exception.FileMaxCountExceededException;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.data.domain.Pageable;


@RestController
@RequestMapping("/api/histories")
public class HistoryController {
    private final HistoryService historyService;
    
    public HistoryController(HistoryService historyService) {
        this.historyService = historyService;
    }

    @GetMapping
    public ApiResponse<List<HistoryElement>> findAll(Pageable pageable) {
        // 추후 token에서 user 정보를 가져오도록 수정해야함
        String userId = "test";

        return ApiResponse.success(historyService.findAllByUserId(pageable, userId));
    }

    @GetMapping("/{historyId}")
    public ApiResponse<HistoryResponse> getHistoryById(@PathVariable Long historyId) {
        // 추후 token에서 user 정보를 가져오도록 수정해야함
        String userId = "test";

        return ApiResponse.success(historyService.findById(historyId, userId));
    }

    @PostMapping
    public ApiResponse<HistoryResponse> save(@RequestPart HistoryCreateDto history, 
        @RequestParam(value = "files", required = false) List<MultipartFile> files) {
        // 추후 token에서 user 정보를 가져오도록 수정해야함
        String userId = "test";

        if (files != null && files.size() > 5) {
            throw new FileMaxCountExceededException();
        }

        return ApiResponse.success(historyService.save(history, files, userId));
    }

    @PutMapping("/{historyId}")
    public ApiResponse<HistoryResponse> update(@PathVariable Long historyId, 
        @RequestPart HistoryUpdateDto history,
        @RequestParam(value = "files", required = false) List<MultipartFile> files) {
        // 추후 token에서 user 정보를 가져오도록 수정해야함
        String userId = "test";

        if (files != null && files.size() > 5) {
            throw new FileMaxCountExceededException();
        }

        return ApiResponse.success(historyService.update(historyId, history, files, userId));
    }

    @DeleteMapping("/{historyId}")
    public ApiResponse<?> delete(@PathVariable Long historyId) {
        // 추후 token에서 user 정보를 가져오도록 수정해야함
        String userId = "test";

        historyService.delete(historyId, userId);
        return ApiResponse.success();
    }
}
