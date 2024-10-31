package swe.second.team_matching_server.domain.file.model.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import swe.second.team_matching_server.common.enums.FileFolder;
import swe.second.team_matching_server.domain.meeting.model.entity.Meeting;
import swe.second.team_matching_server.domain.user.model.entity.User;
import swe.second.team_matching_server.domain.history.model.entity.History;

import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class FileCreateDto {
  @NonNull
  private MultipartFile file;

  private String meta;
  private FileFolder folder;

  private Meeting meeting;
  private User user;
  private History history;
}
