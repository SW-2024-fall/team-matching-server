package swe.second.team_matching_server.common.enums;

public enum FileFolder {
  DEFAULT("default"),
  MEETING("meeting"),
  USER("user"),
  FILE("file"),
  HISTORY("history");

  private final String folderName;

  FileFolder(String folderName) {
    this.folderName = folderName;
  }

  public String getFolderName() {
    return folderName;
  }
}
