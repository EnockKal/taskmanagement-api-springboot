package com.enock.taskmanagementapispringboot.services;

import com.enock.taskmanagementapispringboot.dtos.userDTO.UserResponse;
import com.enock.taskmanagementapispringboot.entities.User;
import com.enock.taskmanagementapispringboot.mappers.UserMapper;
import com.enock.taskmanagementapispringboot.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return userMapper.mapToUserResponseList(users);
    }

//    public UserResponse getUsersByUsername(String username) {
//        User user = userRepository.getUsersByUsername(username).;
//
//        if (user.getUsername().equals(username)) {
//            return userMapper.mapToUserResponse(user);
//        }
//        return
//    }
}
