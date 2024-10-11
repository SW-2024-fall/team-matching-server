package swe.second.team_matching_server.domain.file.model.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import swe.second.team_matching_server.common.enums.FileFolder;

import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class FileCreateDto {
  @NonNull
  private MultipartFile file;

  private String id;
  private String meta;
  private FileFolder folder;
  private String url;
}
