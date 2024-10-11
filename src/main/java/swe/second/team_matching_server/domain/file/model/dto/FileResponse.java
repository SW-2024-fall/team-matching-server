package swe.second.team_matching_server.domain.file.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileResponse {
  private String id;
  private String originalName;
  private String url;
  private String mimeType;
  private Long size;
  private String meta;
}
