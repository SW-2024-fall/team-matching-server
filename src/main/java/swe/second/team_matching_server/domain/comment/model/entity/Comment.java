package swe.second.team_matching_server.domain.comment.model.entity;

import swe.second.team_matching_server.domain.user.model.entity.User;
import swe.second.team_matching_server.domain.meeting.model.entity.Meeting;
import swe.second.team_matching_server.common.entity.Base;
import swe.second.team_matching_server.domain.history.model.entity.History;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import org.hibernate.annotations.Filter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comments")
@ToString(callSuper = true, exclude = {"parentComment", "childComments"})
@EqualsAndHashCode(callSuper = true)
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Filter(name = "deletedComment", condition = "deleted_at is null")
public class Comment extends Base{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "meeting_id", nullable = true)
    private Meeting meeting;

    @ManyToOne
    @JoinColumn(name = "history_id", nullable = true)
    private History history;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    @Builder.Default
    private byte depth = 0;

    @ManyToOne
    @JoinColumn(name = "parent_comment_id", nullable = true)
    private Comment parentComment;

    @OneToMany(mappedBy = "parentComment")
    @Builder.Default
    private List<Comment> childComments = new ArrayList<>();

    public void addChildComment(Comment childComment) {
        childComment.setParentComment(this);
        childComment.setDepth((byte) (this.depth + 1));
        this.childComments.add(childComment);
    }

    public void updateContent(String content) {
        this.content = content;
    }

    private void setParentComment(Comment parentComment) {
        this.parentComment = parentComment;
    }

    private void setDepth(byte depth) {
        this.depth = depth;
    }

    @PrePersist
    @PreUpdate
    public void isValidComment() {
        if (this.depth > 2) {
            throw new IllegalArgumentException("Comment(" + this.id + ")'s depth should be less than 2.");
        }
        if (this.content.length() > 1000) {
            this.content = this.content.substring(0, 1000);
        }
    }
}
