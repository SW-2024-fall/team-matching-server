package swe.second.team_matching_server.core.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import swe.second.team_matching_server.domain.user.model.entity.User;
import swe.second.team_matching_server.domain.user.model.enums.Major;

@Getter
@Setter
@Builder
public class UserRequestDto {
    private String email;
    private String password;
    private String username;
    private Major major;
    private String studentId;
    private String phoneNumber;

    public UserRequestDto(String email, String password, String username, Major major, String studentId, String phoneNumber) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.major = major;
        this.studentId = studentId;
        this.phoneNumber = phoneNumber;
    }

    public User toUser(PasswordEncoder passwordEncoder) {
        return User.builder()
                .email(this.email)
                .password(passwordEncoder.encode(this.password))  // 비밀번호 암호화
                .username(this.username)
                .major(this.major)
                .studentId(this.studentId)
                .phoneNumber(this.phoneNumber)
                .build();
    }

    public UsernamePasswordAuthenticationToken toAuthentication() {
        return new UsernamePasswordAuthenticationToken(this.email, this.password);
    }
}
