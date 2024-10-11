package swe.second.team_matching_server.domain.file.model.mapper;

import swe.second.team_matching_server.domain.file.model.dto.FileCreateDto;
import swe.second.team_matching_server.domain.file.model.entity.File;
import swe.second.team_matching_server.domain.file.model.dto.FileResponse;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FileMapper {
  File toEntity(FileCreateDto fileCreateDto);

  FileResponse toDto(File file);
}
