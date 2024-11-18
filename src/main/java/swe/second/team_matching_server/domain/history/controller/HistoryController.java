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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.data.domain.Pageable;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "History", description = "모임 활동 내역 API")
@RestController
@RequestMapping("/api/histories")
public class HistoryController {
    private final HistoryService historyService;
    
    public HistoryController(HistoryService historyService) {
        this.historyService = historyService;
    }

    @GetMapping
    @Operation(summary = "유저의 활동내역 조회 (메인 피드)", description = "유저의 활동내역을 조회합니다.")
    public ApiResponse<List<HistoryElement>> findAll(Pageable pageable, @AuthenticationPrincipal UserDetails userDetails) {
        String userEmail = userDetails.getUsername();
        log.info("userEmail: {}", userEmail);

        return ApiResponse.success(historyService.findAllByUserEmail(pageable, userEmail));
    }

    @GetMapping("/{historyId}")
    @Operation(summary = "활동내역 상세 조회", description = "활동내역을 상세 조회합니다.")
    public ApiResponse<HistoryResponse> getHistoryById(@PathVariable Long historyId, @AuthenticationPrincipal UserDetails userDetails) {
        String userEmail = userDetails.getUsername();

        return ApiResponse.success(historyService.findByIdAndUserEmail(historyId, userEmail));
    }

    @PostMapping
    @Operation(summary = "모임의 활동내역 생성", description = "모임의 활동내역을 생성합니다. (리더/부리더만 가능)")
    @Parameters({
        @Parameter(name = "history", description = "활동내역 생성 정보", required = true),
        @Parameter(name = "files", description = "활동내역 파일", required = false)
    })
    public ApiResponse<HistoryResponse> save(@RequestPart HistoryCreateDto history, 
        @RequestParam(value = "files", required = false) List<MultipartFile> files,
        @AuthenticationPrincipal UserDetails userDetails) {
        String userEmail = userDetails.getUsername();

        if (files != null && files.size() > 5) {
            throw new FileMaxCountExceededException();
        }

        return ApiResponse.success(historyService.saveWithUserEmail(history, files, userEmail));
    }

    @PutMapping("/{historyId}")
    @Operation(summary = "활동내역 수정", description = "활동내역을 수정합니다. (리더/부리더만 가능)")
    @Parameters({
        @Parameter(name = "history", description = "활동내역 수정 정보", required = true),
        @Parameter(name = "files", description = "활동내역 파일", required = false)
    })
    public ApiResponse<HistoryResponse> update(@PathVariable Long historyId, 
        @RequestPart HistoryUpdateDto history,
        @RequestParam(value = "files", required = false) List<MultipartFile> files,
        @AuthenticationPrincipal UserDetails userDetails) {
        String userEmail = userDetails.getUsername();

        if (files != null && files.size() > 5) {
            throw new FileMaxCountExceededException();
        }

        return ApiResponse.success(historyService.updateWithUserEmail(historyId, history, files, userEmail));
    }

    @DeleteMapping("/{historyId}")
    @Operation(summary = "활동내역 삭제", description = "활동내역을 삭제합니다. (리더/부리더만 가능)")
    public ApiResponse<?> delete(@PathVariable Long historyId, @AuthenticationPrincipal UserDetails userDetails) {
        String userEmail = userDetails.getUsername();

        historyService.deleteWithUserEmail(historyId, userEmail);
        return ApiResponse.success();
    }
}
