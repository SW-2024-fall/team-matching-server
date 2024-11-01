package swe.second.team_matching_server.domain.file.service;

import org.springframework.stereotype.Service;

import swe.second.team_matching_server.domain.file.model.dto.FileResponse;
import swe.second.team_matching_server.domain.file.model.dto.FileCreateDto;
import swe.second.team_matching_server.domain.file.model.mapper.FileMapper;
import swe.second.team_matching_server.domain.file.model.entity.File;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileFacadeService {
  private final FileMeetingService fileMeetingService;
  private final FileUserService fileUserService;
  private final FileService fileService;
  private final FileMapper fileMapper;

  public FileFacadeService(FileMeetingService fileMeetingService, FileUserService fileUserService,
      FileService fileService, FileMapper fileMapper) {
    this.fileMeetingService = fileMeetingService;
    this.fileUserService = fileUserService;
    this.fileService = fileService;
    this.fileMapper = fileMapper;
  }

  public List<FileResponse> findAllByMeetingId(Long meetingId) {
    return fileMeetingService.findAllByMeetingId(meetingId).stream()
        .map(fileMapper::toDto)
        .collect(Collectors.toList());
  }

  public FileResponse findByUserId(String userId) {
    File file = fileUserService.findByUserId(userId);

    return fileMapper.toDto(file);
  }

  public FileResponse updateFileByUserId(String userId, FileCreateDto fileCreateDto) {
    File file = fileUserService.updateFileByUserId(userId, fileCreateDto);

    return fileMapper.toDto(file);
  }

  public FileResponse save(FileCreateDto fileCreateDto) {
    File file = fileService.save(fileCreateDto);

    return fileMapper.toDto(file);
  }

  public List<FileResponse> saveAll(List<FileCreateDto> fileCreateDtos) {
    List<File> files = fileService.saveAll(fileCreateDtos);

    return files.stream()
        .map(fileMapper::toDto)
        .collect(Collectors.toList());
  }

  // 명시적으로 fileId로 파일을 찾는 경우 없으면 에러 발생
  public FileResponse findById(String fileId) {
    File file = fileService.findFileById(fileId);

    return fileMapper.toDto(file);
  }

  public void delete(String fileId) {
    fileService.delete(fileId);
  }
}
