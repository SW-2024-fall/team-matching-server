package swe.second.team_matching_server.core.auth.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;
import swe.second.team_matching_server.domain.user.model.entity.User;
import swe.second.team_matching_server.domain.user.model.enums.Major;
import swe.second.team_matching_server.domain.file.model.entity.File;
import swe.second.team_matching_server.domain.meeting.model.enums.MeetingCategory;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDto {
    private String email;
    private String password;
    private String username;
    private Major major;
    private String studentId;
    private String phoneNumber;
    private File profileImage;
    private List<MeetingCategory> categories;

    public UserRequestDto(String email, String password, String username, Major major, String studentId, String phoneNumber, List<MeetingCategory> categories) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.major = major;
        this.studentId = studentId;
        this.phoneNumber = phoneNumber;
        this.categories = categories;
    }

    public void setProfileImage(File profileImage) {
        this.profileImage = profileImage;
    }

    public User toUser(PasswordEncoder passwordEncoder) {
        User user = User.builder()
                .email(this.email)
                .password(passwordEncoder.encode(this.password))  // 비밀번호 암호화
                .username(this.username)
                .major(this.major)
                .studentId(this.studentId)
                .phoneNumber(this.phoneNumber)
                .profileImage(this.profileImage)
                .build();

        user.setCategories(this.categories);
        return user;
    }
}
