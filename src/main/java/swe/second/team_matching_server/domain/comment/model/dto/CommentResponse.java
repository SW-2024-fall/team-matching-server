package swe.second.team_matching_server.domain.comment.model.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import swe.second.team_matching_server.domain.user.model.dto.UserProfile;
import swe.second.team_matching_server.domain.comment.model.entity.Comment;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {
    private Long id;
    private UserProfile user;
    private String content;
    private byte depth;
    private Long parentCommentId;

    public static CommentResponse from(Comment comment) {
        return CommentResponse.builder()
            .id(comment.getId())
            .user(UserProfile.from(comment.getUser()))
            .content(comment.getContent())
            .depth(comment.getDepth())
            .parentCommentId(comment.getParentComment() != null ? comment.getParentComment().getId() : null)
            .build();
    }
}
