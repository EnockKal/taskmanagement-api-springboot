package com.enock.taskmanagementapispringboot.services;

import com.enock.taskmanagementapispringboot.dtos.userDTO.UserRequest;
import com.enock.taskmanagementapispringboot.dtos.userDTO.UserResponse;
import com.enock.taskmanagementapispringboot.dtos.userDTO.UserUpdateRequest;
import com.enock.taskmanagementapispringboot.entities.Task;
import com.enock.taskmanagementapispringboot.entities.User;
import com.enock.taskmanagementapispringboot.exceptions.ResourceNotFoundException;
import com.enock.taskmanagementapispringboot.exceptions.UserAlreadyExistsException;
import com.enock.taskmanagementapispringboot.mappers.UserMapper;
import com.enock.taskmanagementapispringboot.repository.TaskRepository;
import com.enock.taskmanagementapispringboot.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final TaskRepository taskRepository;

    public UserService(UserRepository userRepository, UserMapper userMapper, TaskRepository taskRepository) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.taskRepository = taskRepository;
    }

    public UserResponse createUser(UserRequest userRequest) {
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new UserAlreadyExistsException("User with email " + userRequest.getEmail() + " already exists");
        }

        User user = userMapper.mapToUser(userRequest);
        User savedUser = userRepository.save(user);

        return userMapper.mapToUserResponse(savedUser);
    }

    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return userMapper.mapToUserResponseList(users);
    }

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("User with id: " + id + " not found"));

        return userMapper.mapToUserResponse(user);
    }

    public UserResponse updateUser(Long id, UserUpdateRequest userUpdateRequest) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("User with id: " + id + " not found"));

        if (userUpdateRequest.getEmail() != null && !userUpdateRequest.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(userUpdateRequest.getEmail())) {
                throw new UserAlreadyExistsException(
                        "User with email: " + userUpdateRequest.getEmail() + " already exists");
            }

            user.setEmail(userUpdateRequest.getEmail());
        }

        if (userUpdateRequest.getUsername() != null){
            user.setUsername(userUpdateRequest.getUsername());
        }

        User savedUser = userRepository.save(user);

        return userMapper.mapToUserResponse(savedUser);
    }

    public String deleteUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("User with id: " + id + " not found"));

        List<Task> tasks = taskRepository.findByUser(user);
        for (Task task : tasks) {
            task.setUser(null);
        }

        taskRepository.saveAll(tasks);
        userRepository.delete(user);

        return "User with id: " + id + " has been deleted";
    }

    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new ResourceNotFoundException("User with username: " + username + " not found"));

        return userMapper.mapToUserResponse(user);
    }

    public UserResponse findByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new ResourceNotFoundException("User with email: " + email + " not found"));

        return userMapper.mapToUserResponse(user);
    }
}
