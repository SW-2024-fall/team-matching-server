package swe.second.team_matching_server.domain.comment.model.dto;


import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentCreateDto {
    private Long meetingId;
    private String userId;
    private String content;
    private Long parentCommentId;
}
