package swe.second.team_matching_server.domain.comment.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import swe.second.team_matching_server.common.dto.ApiResponse;

import swe.second.team_matching_server.domain.comment.model.dto.CommentResponse;
import swe.second.team_matching_server.domain.comment.service.CommentService;
import swe.second.team_matching_server.domain.comment.model.dto.CommentCreateDto;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public ApiResponse<List<CommentResponse>> getComments(@RequestParam Long meetingId) {
        List<CommentResponse> comments = commentService.findAllByMeetingId(meetingId);

        return ApiResponse.success(comments);
    }

    @PostMapping
    public ApiResponse<CommentResponse> createComment(@RequestBody CommentCreateDto commentCreateDto) {
        // TODO: 인증 추가
        String userId = "test";
        commentCreateDto.setUserId(userId);
        CommentResponse commentResponse = commentService.createComment(commentCreateDto);

        return ApiResponse.success(commentResponse);
    }

    @PutMapping("/{commentId}")
    public ApiResponse<CommentResponse> updateComment(@PathVariable Long commentId, @RequestBody CommentCreateDto commentCreateDto) {
        // TODO: 인증 추가
        String userId = "test";
        commentCreateDto.setUserId(userId);
        CommentResponse commentResponse = commentService.updateComment(commentId, commentCreateDto);

        return ApiResponse.success(commentResponse);
    }

    @DeleteMapping("/{commentId}")
    public ApiResponse<Void> deleteComment(@PathVariable Long commentId) {
        // TODO: 인증 추가
        String userId = "test";
        commentService.deleteComment(commentId, userId);

        return ApiResponse.success();
    }
}
