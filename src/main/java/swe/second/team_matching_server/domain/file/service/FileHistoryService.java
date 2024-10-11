package swe.second.team_matching_server.domain.file.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import swe.second.team_matching_server.domain.file.repository.FileRepository;
import swe.second.team_matching_server.domain.file.model.dto.FileCreateDto;
import swe.second.team_matching_server.domain.file.model.entity.File;
import swe.second.team_matching_server.common.enums.FileFolder;

@Service
public class FileHistoryService {
  private final FileRepository fileRepository;
  private final FileService fileService;

  public FileHistoryService(FileRepository fileRepository, FileService fileService) {
    this.fileRepository = fileRepository;
    this.fileService = fileService;
  }
  
  @Transactional(readOnly = true)
  public List<File> findFilesByHistoryId(Long historyId) {
    return fileRepository.findAllByHistoryId(historyId);
  }

  @Transactional
  public List<File> updateFilesByHistoryId(Long historyId, List<FileCreateDto> newFileCreateDtos, List<String> deletedFileIds) {
    List<FileCreateDto> updatedFileCreateDtos = newFileCreateDtos.stream()
        .map(fileCreateDto -> {fileCreateDto.setFolder(FileFolder.HISTORY); return fileCreateDto;})
        .collect(Collectors.toList());
    
    fileService.saveFiles(updatedFileCreateDtos);
    fileService.deleteFiles(deletedFileIds);

    return findFilesByHistoryId(historyId);
  }
}
