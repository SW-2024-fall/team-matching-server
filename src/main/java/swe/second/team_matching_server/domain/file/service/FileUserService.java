package swe.second.team_matching_server.domain.file.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import swe.second.team_matching_server.domain.file.repository.FileRepository;
import swe.second.team_matching_server.domain.file.model.entity.File;
import swe.second.team_matching_server.domain.file.model.dto.FileCreateDto;
import swe.second.team_matching_server.common.enums.FileFolder;

import java.util.Optional;

@Service
public class FileUserService {
  private final FileRepository fileRepository;
  private final FileService fileService;

  public FileUserService(FileRepository fileRepository, FileService fileService) {
    this.fileRepository = fileRepository;
    this.fileService = fileService;
  }

  public Optional<File> findFileByUserId(String userId) {
    return fileRepository.findByUserId(userId);
  }

  @Transactional
  public File updateFileByUserId(String userId, FileCreateDto fileCreateDto) {
    Optional<File> file = findFileByUserId(userId);

    if (file.isPresent()) {
      fileService.deleteFile(file.get().getId());
    }
    fileCreateDto.setFolder(FileFolder.USER);

    return fileService.saveFile(fileCreateDto);
  }
}
