package swe.second.team_matching_server.domain.file.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

import swe.second.team_matching_server.domain.file.repository.FileRepository;
import swe.second.team_matching_server.domain.file.model.dto.FileCreateDto;
import swe.second.team_matching_server.domain.file.model.entity.File;
import swe.second.team_matching_server.common.enums.FileFolder;
import swe.second.team_matching_server.domain.meeting.model.entity.Meeting;

@Service
@Transactional(readOnly = true)
public class FileMeetingService {
  private final FileRepository fileRepository;
  private final FileService fileService;

  public FileMeetingService(FileRepository fileRepository, FileService fileService) {
    this.fileRepository = fileRepository;
    this.fileService = fileService;
  }

  public List<File> findAllByMeetingId(Long meetingId) {
    return fileRepository.findAllByMeetingId(meetingId);
  }

  @Transactional
  public List<File> saveAllByMeeting(Meeting meeting, List<FileCreateDto> fileCreateDtos) {
    if (fileCreateDtos == null) {
      return new ArrayList<>();
    }

    List<FileCreateDto> updatedFileCreateDtos = fileCreateDtos.stream()
        .map(fileCreateDto -> {
          fileCreateDto.setFolder(FileFolder.MEETING); 
          fileCreateDto.setMeeting(meeting); 
          return fileCreateDto;})
        .collect(Collectors.toList());
    return fileService.saveAll(updatedFileCreateDtos);
  }

  @Transactional
  public List<File> updateAllByMeeting(Meeting meeting, List<FileCreateDto> newFileCreateDtos, List<String> deletedFileIds) {
    if (newFileCreateDtos != null) {
      List<FileCreateDto> updatedFileCreateDtos = newFileCreateDtos.stream()
          .map(fileCreateDto -> {
            fileCreateDto.setFolder(FileFolder.MEETING); 
            fileCreateDto.setMeeting(meeting); 
            return fileCreateDto;})
        .collect(Collectors.toList());
    
      fileService.saveAll(updatedFileCreateDtos);
    }

    if (deletedFileIds != null) {
      fileService.deleteAll(deletedFileIds);
    }

    return findAllByMeetingId(meeting.getId());
  }
}
