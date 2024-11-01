package swe.second.team_matching_server.domain.file.model.mapper;

import swe.second.team_matching_server.domain.file.model.dto.FileCreateDto;
import swe.second.team_matching_server.domain.file.model.entity.File;
import swe.second.team_matching_server.domain.file.model.dto.FileResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FileMapper {
  @Mapping(target = "id", expression = "java(id)")
  @Mapping(target = "url", expression = "java(url)")
  @Mapping(target = "size", expression = "java(fileCreateDto.getFile().getSize())")
  @Mapping(target = "originalName", expression = "java(fileCreateDto.getFile().getOriginalFilename())")
  @Mapping(target = "mimeType", expression = "java(fileCreateDto.getFile().getContentType())")
  File toEntity(FileCreateDto fileCreateDto, String id, String url);

  FileResponse toFileResponse(File file);
}
