package swe.second.team_matching_server.meeting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import swe.second.team_matching_server.domain.meeting.model.entity.Meeting;
import swe.second.team_matching_server.domain.meeting.repository.MeetingRepository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;

import swe.second.team_matching_server.domain.meeting.model.enums.MeetingType;
import swe.second.team_matching_server.domain.meeting.model.enums.MeetingCategory;

public class MeetingRepositoryTest {

  @Autowired
  private TestEntityManager testEntityManager;

  @Autowired
  private MeetingRepository meetingRepository;

  private Meeting testMeeting;

  @BeforeEach
  public void setUp() {
    testMeeting = Meeting.builder()
      .name("testMeeting")
      .title("testTitle")
      .content("testContent")
      .location("testLocation")
      .days(Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY))
      .startDate(LocalDate.now())
      .endDate(LocalDate.now().plusDays(1))
      .minParticipant((byte) 1)
      .maxParticipant((byte) 10)
      .startTime(LocalTime.now())
      .endTime(LocalTime.now().plusHours(1))
      .type(MeetingType.REGULAR)
      .categories(List.of(MeetingCategory.EXERCISE, MeetingCategory.DANCE))
      .features(List.of("feature1", "feature2"))
      .analyzedFeatures(null)
      .analyzedIntroduction(null)
      .build();
  }

  @Test
  public void testSave() {
    Meeting savedMeeting = meetingRepository.save(testMeeting);
    
    assertThat(savedMeeting).isNotNull();
    assertThat(savedMeeting.getId()).isNotNull();
  }

  @Test
  public void testFindById() {
    testEntityManager.persist(testMeeting);

    Meeting foundMeeting = meetingRepository.findById(testMeeting.getId()).orElseThrow();
    
    assertThat(testMeeting.getId()).isEqualTo(foundMeeting.getId());
  }

  @Test
  public void testFindAllByUserId() {

  }
}
