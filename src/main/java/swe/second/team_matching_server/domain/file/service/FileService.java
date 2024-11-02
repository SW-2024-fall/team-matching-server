package swe.second.team_matching_server.domain.file.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.AmazonS3;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;

import swe.second.team_matching_server.domain.file.model.entity.File;
import swe.second.team_matching_server.domain.file.repository.FileRepository;
import swe.second.team_matching_server.domain.file.model.dto.FileCreateDto;

import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileService {
  private final FileRepository fileRepository;
  private final AmazonS3 amazonS3;

  @Value("${cloud.aws.s3.bucket}")
  private String bucket;

  public FileService(FileRepository fileRepository, AmazonS3 amazonS3) {
    this.fileRepository = fileRepository;
    this.amazonS3 = amazonS3;
  }

  @Transactional(readOnly = true)
  public File findFileById(String fileId) {
    return fileRepository.findById(fileId)
        .orElseThrow(() -> new RuntimeException("File not found"));
  }

  @Transactional
  public List<File> updateAll(List<File> existingFiles, List<FileCreateDto> updatedFileCreateDtos) {
    existingFiles.forEach(this::delete);
    return saveAll(updatedFileCreateDtos);
  }

  @Transactional
  public File save(FileCreateDto fileCreateDto) {
    String fileId = UUID.randomUUID().toString();
    String filePath = fileCreateDto.getFolder().getFolderName() + '/' + fileId;
    
    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setContentType(fileCreateDto.getFile().getContentType());
    metadata.setContentLength(fileCreateDto.getFile().getSize());

    try {
      amazonS3.putObject(bucket, filePath, fileCreateDto.getFile().getInputStream(), metadata);
    } catch (IOException e) {
      throw new RuntimeException("Failed to upload file to S3", e);
    }

    String url = amazonS3.getUrl(bucket, filePath).toString();

    File file = File.builder()
        .id(fileId)
        .url(url)
        .size(fileCreateDto.getFile().getSize())
        .originalName(fileCreateDto.getFile().getOriginalFilename())
        .mimeType(fileCreateDto.getFile().getContentType())
        .folder(fileCreateDto.getFolder())
        .meeting(fileCreateDto.getMeeting())
        .user(fileCreateDto.getUser())
        .history(fileCreateDto.getHistory())
        .build();
    return fileRepository.save(file);
  }

  public List<File> saveAll(List<FileCreateDto> fileCreateDtos) {
    return fileCreateDtos.stream()
        .map(this::save)
        .collect(Collectors.toList());
  }

  @Transactional
  public void delete(String fileId) {
    File file = findFileById(fileId);
    delete(file);
  }

  @Transactional
  public void delete(File file) {
    String filePath = file.getFolder().getFolderName() + '/' + file.getId();

    amazonS3.deleteObject(bucket, filePath);
    fileRepository.delete(file);
  }

  public void deleteAll(List<String> fileIds) {
    fileIds.forEach(this::delete);
  }
}
