package swe.second.team_matching_server.domain.history.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityNotFoundException;
import swe.second.team_matching_server.common.enums.FileFolder;
import swe.second.team_matching_server.domain.file.model.dto.FileCreateDto;
import swe.second.team_matching_server.domain.file.model.entity.File;
import swe.second.team_matching_server.domain.file.service.FileService;
import swe.second.team_matching_server.domain.history.model.dto.HistoryCreateDto;
import swe.second.team_matching_server.domain.history.model.entity.History;
import swe.second.team_matching_server.domain.history.repository.HistoryRepository;

@Service
@Transactional(readOnly = true)
public class HistoryService {
    private final HistoryRepository historyRepository;
    private final FileService fileService;

    public HistoryService(HistoryRepository historyRepository, FileService fileService) {
        this.historyRepository = historyRepository;
        this.fileService = fileService;
    }

    public Page<History> getHistoriesByMeetingId(Long meetingId, Pageable pageable) {
        return historyRepository.findAllByMeetingId(meetingId, pageable);
    }

    public History getHistoryById(Long historyId) {
        return historyRepository.findById(historyId).orElseThrow(() -> new RuntimeException("History not found"));
    }

    @Transactional
    public void createHistory(List<MultipartFile> photos, HistoryCreateDto historyCreateDto) {
        List<FileCreateDto> fileCreateDtos = photos.stream()
        .map(photo -> FileCreateDto.builder()
            .file(photo).meta(null).folder(FileFolder.HISTORY).build())
            .collect(Collectors.toList());

        List<File> savedPhotos = fileService.saveFiles(fileCreateDtos);

        History history = History.builder()
        // .user(user)
        // .meeting(meeting)
        .title(historyCreateDto.getTitle())
        .content(historyCreateDto.getContent())
        .isPublic(historyCreateDto.isPublic())
        .date(historyCreateDto.getDate())
        .location(historyCreateDto.getLocation())
        .photos(savedPhotos)
        // .attendanceHistories(attendanceHistories)
        .build();
        
        historyRepository.save(history);
    }

    @Transactional
    public void updateHistory(@PathVariable Long historyId, @RequestPart(name="newFiles", required=false) List<MultipartFile> newFiles, @RequestPart(name="deletedFileIds", required=false) List<String> deletedFileIds, @RequestPart("history") HistoryCreateDto historyCreateDto) {
        History history = historyRepository.getHistoryById(historyId).orElsethrow(() -> new EntityNotFoundException("History not found with id: " + historyId));

        History history = convertToEntity(historyCreateDto);
        historyRepository.save(history);
    }

    @Transactional
    public void deleteHistory(Long historyId) {
        historyRepository.deleteById(historyId);
    }

    private History convertToEntity(HistoryCreateDto historyCreateDto) {
        History history = History.builder()
        .title(historyCreateDto.getTitle())
        .content(historyCreateDto.getContent())
        .isPublic(historyCreateDto.isPublic())
        .date(historyCreateDto.getDate())
        .location(historyCreateDto.getLocation())
        .build();
        return history;
    }
}

