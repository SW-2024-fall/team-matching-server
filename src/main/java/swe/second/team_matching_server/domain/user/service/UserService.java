package swe.second.team_matching_server.domain.user.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import swe.second.team_matching_server.domain.user.repository.UserRepository;
import swe.second.team_matching_server.domain.user.model.entity.User;
import swe.second.team_matching_server.domain.user.model.exception.UserNotFoundException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User findById(String userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException());
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException());
    }

    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public User update(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public void delete(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException());

        user.delete();
    }
}
