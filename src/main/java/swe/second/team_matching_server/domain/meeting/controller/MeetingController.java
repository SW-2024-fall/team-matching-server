package swe.second.team_matching_server.domain.meeting.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/meetings")
public class MeetingController {
  private final MeetingFacadeService meetingFacadeService;

  public MeetingController(MeetingFacadeService meetingFacadeService) {
    this.meetingFacadeService = meetingFacadeService;
  }

  @GetMapping
  public ApiResponse<List<MeetingElement>> findAll(@PageableDefault(size = 30) Pageable pageable,
    @RequestParam(value = "categories", required = false) List<String> categories,
    @RequestParam(value = "type", required = false) String type,
    @RequestParam(value = "min", required = false) Integer min,
    @RequestParam(value = "max", required = false) Integer max) {
      List<MeetingCategory> categoriesEnum = categories == null ? 
        new ArrayList<>() : 
        categories.stream().map(MeetingCategory::from).collect(Collectors.toList());
      MeetingType typeEnum = type == null ? null : MeetingType.from(type);
      int minParticipant = min == null ? 0 : min;
      int maxParticipant = max == null ? 99 : max;

    return ApiResponse.success(meetingFacadeService.findAllWithConditions(pageable, categoriesEnum, typeEnum, minParticipant, maxParticipant));
  }

  @GetMapping("/{meetingId}")
  public ApiResponse<MeetingResponse> findById(@PathVariable Long meetingId) {
    // 추후 token에서 user 정보 가져오기. 지금은 그냥 예시
    String userId = "test";

    return ApiResponse.success(meetingFacadeService.findById(meetingId, userId));
  }

  @PostMapping
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
  public ApiResponse<Void> delete(@PathVariable Long meetingId) {
    // 추후 token에서 user 정보 가져오기. 지금은 그냥 예시
    String userId = "test";

    meetingFacadeService.delete(meetingId, userId);
    return ApiResponse.success();
  }
}
