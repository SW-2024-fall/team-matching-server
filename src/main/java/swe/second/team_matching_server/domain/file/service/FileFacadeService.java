package swe.second.team_matching_server.domain.file.service;

import org.springframework.stereotype.Service;

import swe.second.team_matching_server.domain.file.model.dto.FileResponse;
import swe.second.team_matching_server.domain.file.model.dto.FileCreateDto;
import swe.second.team_matching_server.domain.file.model.mapper.FileMapper;
import swe.second.team_matching_server.domain.file.model.entity.File;

import java.util.List;
import java.util.Optional;
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

  public List<FileResponse> findFilesByMeetingId(Long meetingId) {
    return fileMeetingService.findFilesByMeetingId(meetingId).stream()
        .map(fileMapper::toDto)
        .collect(Collectors.toList());
  }

  public List<FileResponse> updateFilesByMeetingId(Long meetingId, List<FileCreateDto> newFileCreateDtos, List<String> deletedFileIds) {
    return fileMeetingService.updateFilesByMeetingId(meetingId, newFileCreateDtos, deletedFileIds).stream()
        .map(fileMapper::toDto)
        .collect(Collectors.toList());
  }

  public Optional<FileResponse> findFileByUserId(String userId) {
    Optional<File> file = fileUserService.findFileByUserId(userId);

    if (file.isPresent()) {
      return Optional.of(fileMapper.toDto(file.get()));
    }

    return Optional.empty();
  }

  public FileResponse updateFileByUserId(String userId, FileCreateDto fileCreateDto) {
    File file = fileUserService.updateFileByUserId(userId, fileCreateDto);

    return fileMapper.toDto(file);
  }

  public FileResponse saveFile(FileCreateDto fileCreateDto) {
    File file = fileService.saveFile(fileCreateDto);

    return fileMapper.toDto(file);
  }

  public List<FileResponse> saveFiles(List<FileCreateDto> fileCreateDtos) {
    List<File> files = fileService.saveFiles(fileCreateDtos);

    return files.stream()
        .map(fileMapper::toDto)
        .collect(Collectors.toList());
  }

  // 명시적으로 fileId로 파일을 찾는 경우 없으면 에러 발생
  public FileResponse findById(String fileId) {
    File file = fileService.findFileById(fileId);

    return fileMapper.toDto(file);
  }

  public void deleteFile(String fileId) {
    fileService.deleteFile(fileId);
  }
}
