package swe.second.team_matching_server.domain.file.service;

import org.springframework.beans.factory.annotation.Value;
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

  @Value("${user.default.profile.image.id}")
  private String defaultProfileImageId;
  
  public FileUserService(FileRepository fileRepository, FileService fileService) {
    this.fileRepository = fileRepository;
    this.fileService = fileService;
  }

  public File getDefaultProfileImage() {
    return fileRepository.findById(defaultProfileImageId)
        .orElseThrow(FileNotFoundException::new);
  }

  public File findByUserId(String userId) {
    return fileRepository.findByUserId(userId).orElseThrow(FileNotFoundException::new);
  }

  @Transactional
  public File saveProfileImage(FileCreateDto fileCreateDto) {
    fileCreateDto.setFolder(FileFolder.USER);
    return fileService.save(fileCreateDto);
  }

  @Transactional
  public File updateFileByUserId(String userId, FileCreateDto fileCreateDto) {
    try {
      File file = findByUserId(userId);

      if (file.getUser() != null) {
        fileService.delete(file);
      }
    } catch (FileNotFoundException e) {
    }

    fileCreateDto.setFolder(FileFolder.USER);
    return fileService.save(fileCreateDto);
  }
}
