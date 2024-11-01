package swe.second.team_matching_server.domain.meeting.model.enums;

import swe.second.team_matching_server.domain.meeting.model.exception.MeetingInvalidCategoryException;  

public enum MeetingCategory {
  RESEARCH("학술/연구"),
  LITERATURE("인문학/책/글"),
  PHOTOGRAPHY("사진/영상"),
  EXERCISE("운동"),
  LANGUAGE("외국/언어"),
  MUSIC("음악/악기"),
  DANCE("댄스/무용"),
  JOB_SEARCH("면접/취준"),
  FESTIVAL("공연/축제"),
  TRAVEL("캠핑/여행"),
  VOLUNTEER("봉사활동"),
  ENTERTAINMENT("게임/오락"),
  ETC("기타");

  private final String category;

  MeetingCategory(String category) {
    this.category = category;
  }

  public static MeetingCategory from(String value) {
    try {
        return MeetingCategory.valueOf(value);
    } catch (IllegalArgumentException e) {
        throw new MeetingInvalidCategoryException();
    }
}

  public String getCategory() {
    return category;
  }
}
