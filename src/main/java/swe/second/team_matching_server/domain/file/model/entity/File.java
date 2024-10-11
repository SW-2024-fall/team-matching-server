package swe.second.team_matching_server.domain.file.model.entity;

import swe.second.team_matching_server.common.entity.Base;
import swe.second.team_matching_server.common.enums.FileFolder;
import swe.second.team_matching_server.domain.user.model.entity.User;
import swe.second.team_matching_server.domain.meeting.model.entity.Meeting;
import swe.second.team_matching_server.domain.history.model.entity.History;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import org.hibernate.annotations.Filter;


@Entity
@Table(name = "files")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(callSuper = true, of = {"id", "originalName", "folder", "size", "mimeType", "meta"})
@Filter(name = "deletedFileFilter", condition = "deleted_at is null")
public class File extends Base {
  @Id
  private String id;

  @Column(nullable = false)
  private String originalName;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private FileFolder folder;
  
  @Column(nullable = false)
  private String mimeType;

  @Column(nullable = false)
  private Long size; // bytes 단위 사이즈

  @Column(nullable = true)
  private String meta;

  @Column(nullable = false)
  private String url;

  @ManyToOne
  @JoinColumn(name = "meeting_id", nullable = true)
  private Meeting meeting;

  @OneToOne
  @JoinColumn(name = "user_id", nullable = true)
  private User user;

  @ManyToOne
  @JoinColumn(name = "history_id", nullable = true)
  private History history;
}
