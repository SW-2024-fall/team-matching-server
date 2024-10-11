package swe.second.team_matching_server.domain.file.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.AmazonS3;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;

import swe.second.team_matching_server.domain.file.model.mapper.FileMapper;
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
  private final FileMapper fileMapper;

  @Value("${cloud.aws.s3.bucket}")
  private String bucket;

  public FileService(FileRepository fileRepository, AmazonS3 amazonS3, FileMapper fileMapper) {
    this.fileRepository = fileRepository;
    this.amazonS3 = amazonS3;
    this.fileMapper = fileMapper;
  }

  @Transactional(readOnly = true)
  public File findFileById(String fileId) {
    return fileRepository.findById(fileId)
        .orElseThrow(() -> new RuntimeException("File not found"));
  }

  @Transactional
  public File saveFile(FileCreateDto fileCreateDto) {
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

    File file = fileMapper.toEntity(fileCreateDto, fileId, url);
    return fileRepository.save(file);
  }

  public List<File> saveFiles(List<FileCreateDto> fileCreateDtos) {
    return fileCreateDtos.stream()
        .map(this::saveFile)
        .collect(Collectors.toList());
  }

  @Transactional
  public void deleteFile(String fileId) {
    File file = findFileById(fileId);
    String filePath = file.getFolder().getFolderName() + '/' + file.getId();

    amazonS3.deleteObject(bucket, filePath);
    fileRepository.delete(file);
  }

  public void deleteFiles(List<String> fileIds) {
    fileIds.forEach(this::deleteFile);
  }
}
