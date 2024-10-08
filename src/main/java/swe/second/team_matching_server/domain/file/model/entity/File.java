package swe.second.team_matching_server.domain.file.model.entity;

import swe.second.team_matching_server.common.entity.Base;
import swe.second.team_matching_server.common.enums.FileFolder;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Entity
@Table(name = "files")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class File extends Base {
  @Id
  private String id;

  @Column(nullable = false)
  private String originalName;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private FileFolder folderName;

  @Column(nullable = false)
  private String fileName;

  @Column(nullable = false)
  private String mimeType;

  @Column(nullable = false)
  private Long size; // bytes 단위 사이즈

  @Column(nullable = true)
  private String meta;

  @Column(nullable = false)
  private String url;
}