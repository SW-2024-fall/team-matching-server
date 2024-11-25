package swe.second.team_matching_server.domain.user.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserFacadeService {

    private final UserService userService;
}
