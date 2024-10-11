package swe.second.team_matching_server.common.enums;

public enum FileFolder {
  DEFAULT("team-matching"),
  MEETING("meeting"),
  USER("user"),
  FILE("file"),
  HISTORY("history");

  private final String folderName;

  FileFolder(String folderName) {
    this.folderName = folderName;
  }

  public String getFolderName() {
    if (this == DEFAULT) {
      return DEFAULT.folderName;
    }
    return DEFAULT.folderName + "/" + this.folderName;
  }
}
