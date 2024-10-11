package swe.second.team_matching_server.domain.file.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

import swe.second.team_matching_server.common.enums.FileFolder;
import swe.second.team_matching_server.common.enums.ResultCode;
import swe.second.team_matching_server.domain.file.model.dto.FileCreateDto;
import swe.second.team_matching_server.domain.file.service.FileFacadeService;
import swe.second.team_matching_server.common.dto.ApiResponse;
import swe.second.team_matching_server.domain.file.model.dto.FileResponse;

@RestController
@RequestMapping("/api/files")
public class FileController {
  private final FileFacadeService fileFacadeService;

  public FileController(FileFacadeService fileFacadeService) {
    this.fileFacadeService = fileFacadeService;
  }

  @GetMapping("/{fileId}")
  public ApiResponse<FileResponse> getFile(@PathVariable String fileId) {
    try {
      return ApiResponse.success(fileFacadeService.findById(fileId));
    } catch (Exception e) {
      return ApiResponse.failure(ResultCode.NOT_FOUND);
    }
  }

  @PostMapping
  public ApiResponse<FileResponse> createFile(@RequestParam("file") MultipartFile file, @RequestParam(value = "meta", required = false) String meta, @RequestParam(value = "folder", required = false) String folder) {
    FileFolder fileFolder;
    if (folder != null) {
      fileFolder = FileFolder.valueOf(folder);
    } else {
      fileFolder = FileFolder.DEFAULT;
    }
    
    FileCreateDto fileCreateDto = FileCreateDto.builder()
      .file(file)
      .meta(meta)
      .folder(fileFolder)
      .build();

    return ApiResponse.success(fileFacadeService.saveFile(fileCreateDto));
  }

  @PostMapping("/batch")
  public ApiResponse<List<FileResponse>> createFiles(@RequestParam("files") List<MultipartFile> files, @RequestParam(value = "meta", required = false) String meta, @RequestParam(value = "folder", required = false) String folder) {
    FileFolder fileFolder;
    if (folder != null) {
      fileFolder = FileFolder.valueOf(folder);
    } else {
      fileFolder = FileFolder.DEFAULT;
    }
    
    List<FileCreateDto> fileCreateDtos = files.stream()
      .map(file -> FileCreateDto.builder()
        .file(file)
        .meta(meta)
        .folder(fileFolder)
        .build())
      .collect(Collectors.toList());

    return ApiResponse.success(fileFacadeService.saveFiles(fileCreateDtos));
  }
  
  @DeleteMapping("/{fileId}")
  public ApiResponse<Void> deleteFile(@PathVariable String fileId) {
    fileFacadeService.deleteFile(fileId);
    return ApiResponse.success();
  }
}
