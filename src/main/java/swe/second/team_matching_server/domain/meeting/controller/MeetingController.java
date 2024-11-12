package swe.second.team_matching_server.domain.meeting.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;

import swe.second.team_matching_server.domain.meeting.service.MeetingFacadeService;
import swe.second.team_matching_server.common.dto.ApiResponse;
import swe.second.team_matching_server.domain.meeting.model.dto.MeetingResponse;
import swe.second.team_matching_server.domain.meeting.model.dto.MeetingCreateDto;
import swe.second.team_matching_server.domain.meeting.model.enums.MeetingCategory;
import swe.second.team_matching_server.domain.meeting.model.enums.MeetingType;
import swe.second.team_matching_server.domain.meeting.model.dto.MeetingElement;
import swe.second.team_matching_server.domain.file.model.exception.FileMaxCountExceededException;
import swe.second.team_matching_server.domain.meeting.model.dto.MeetingUpdateDto;

import java.util.List;
import java.util.ArrayList;

@Tag(name = "Meeting", description = "모임 API")
@RestController
@RequestMapping("/api/meetings")
public class MeetingController {
  private final MeetingFacadeService meetingFacadeService;

  public MeetingController(MeetingFacadeService meetingFacadeService) {
    this.meetingFacadeService = meetingFacadeService;
  }

  @GetMapping
  @Operation(summary = "모임 전체 조회", description = "모임 전체를 조회합니다.")
  @Parameters({
    @Parameter(name = "pageable", description = "페이지 정보", required = false),
    @Parameter(name = "categories", description = "모임 카테고리. ", required = false),
    @Parameter(name = "type", description = "모임 타입.", required = false),
    @Parameter(name = "min", description = "최소 참여자 수", required = false),
    @Parameter(name = "max", description = "최대 참여자 수", required = false)
  })
  public ApiResponse<List<MeetingElement>> findAll(@PageableDefault(size = 30) Pageable pageable,
    @RequestParam(value = "categories", required = false) List<MeetingCategory> categories,
    @RequestParam(value = "type", required = false) MeetingType type,
    @RequestParam(value = "min", required = false) Integer min,
    @RequestParam(value = "max", required = false) Integer max) {
      List<MeetingCategory> categoriesEnum = categories == null ? 
        new ArrayList<>() : 
        categories;
      int minParticipant = min == null ? 0 : min;
      int maxParticipant = max == null ? 99 : max;

    return ApiResponse.success(meetingFacadeService.findAllWithConditions(pageable, categoriesEnum, type, minParticipant, maxParticipant));
  }

  @GetMapping("/user")
  @Operation(summary = "유저가 참여한 모임 조회", description = "유저가 참여한 모임을 조회합니다.")
  public ApiResponse<List<MeetingElement>> findAllByUserId() {
    // 추후 token에서 user 정보 가져오기. 지금은 그냥 예시
    String userId = "test";
    
    return ApiResponse.success(meetingFacadeService.findAllByUserId(userId));
  }

  @GetMapping("/{meetingId}")
  @Operation(summary = "모임 상세 조회", description = "모임 상세를 조회합니다.")
  public ApiResponse<MeetingResponse> findById(@PathVariable Long meetingId) {
    // 추후 token에서 user 정보 가져오기. 지금은 그냥 예시
    String userId = "test";

    return ApiResponse.success(meetingFacadeService.findById(meetingId, userId));
  }

  @PostMapping
  @Operation(summary = "모임 생성", description = "모임을 생성합니다.")
  public ApiResponse<MeetingResponse> create(@RequestParam(value = "files", required = false) List<MultipartFile> files, 
    @RequestPart MeetingCreateDto meeting) {
    if (files != null && files.size() > 5) {
      throw new FileMaxCountExceededException();
    }
    
    // 추후 token에서 user 정보 가져오기. 지금은 그냥 예시
    String userId = "test2";

    return ApiResponse.success(meetingFacadeService.create(files, meeting, userId));
  }

  @PutMapping("/{meetingId}")
  @Operation(summary = "모임 수정", description = "모임을 수정합니다.")
  public ApiResponse<MeetingResponse> update(@PathVariable Long meetingId, 
    @RequestParam(value = "files", required = false) List<MultipartFile> files, 
    @RequestPart(value = "meeting", required = true) MeetingUpdateDto meetingUpdateDto) {
    if (files != null && files.size() > 5) {
      throw new FileMaxCountExceededException();
    }
    // 추후 token에서 user 정보 가져오기. 지금은 그냥 예시
    String userId = "test2";

    return ApiResponse.success(meetingFacadeService.update(meetingId, files, meetingUpdateDto, userId));
  }

  @DeleteMapping("/{meetingId}")
  @Operation(summary = "모임 삭제", description = "모임을 삭제합니다.")
  public ApiResponse<Void> delete(@PathVariable Long meetingId) {
    // 추후 token에서 user 정보 가져오기. 지금은 그냥 예시
    String userId = "test";

    meetingFacadeService.delete(meetingId, userId);
    return ApiResponse.success();
  }
}
