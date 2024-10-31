package swe.second.team_matching_server.domain.file.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import swe.second.team_matching_server.domain.file.repository.FileRepository;
import swe.second.team_matching_server.domain.file.model.entity.File;
import swe.second.team_matching_server.domain.file.model.dto.FileCreateDto;
import swe.second.team_matching_server.common.enums.FileFolder;
import swe.second.team_matching_server.domain.file.model.exception.FileNotFoundException;

@Service
public class FileUserService {
  private final FileRepository fileRepository;
  private final FileService fileService;
  private final File defaultProfileImage;

  public FileUserService(FileRepository fileRepository, FileService fileService) {
    this.fileRepository = fileRepository;
    this.fileService = fileService;
    this.defaultProfileImage = fileRepository.findById("ed7eee25-0b03-4789-9e48-1a7226eb5850")
        .orElseThrow(FileNotFoundException::new);
  }

  public File getDefaultProfileImage() {
    return defaultProfileImage;
  }

  public File findByUserId(String userId) {
    return fileRepository.findByUserId(userId).orElseThrow(FileNotFoundException::new);
  }

  @Transactional
  public File updateFileByUserId(String userId, FileCreateDto fileCreateDto) {
    File file = findByUserId(userId);

    if (file.getUser() != null) {
      fileService.delete(file);
    }

    fileCreateDto.setFolder(FileFolder.USER);
    return fileService.save(fileCreateDto);
  }
}
