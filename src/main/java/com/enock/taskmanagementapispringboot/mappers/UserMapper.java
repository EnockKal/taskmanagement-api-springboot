package com.enock.taskmanagementapispringboot.mappers;

import com.enock.taskmanagementapispringboot.dtos.userDTO.UserRequest;
import com.enock.taskmanagementapispringboot.dtos.userDTO.UserResponse;
import com.enock.taskmanagementapispringboot.entities.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper {
    public User mapToUser(UserRequest userRequest) {
        if (userRequest == null) {
            return null;
        }

        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setEmail(userRequest.getEmail());

        return user;
    }

    public UserResponse mapToUserResponse(User user) {
        if (user == null) {
            return null;
        }

        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setUsername(user.getUsername());
        userResponse.setEmail(user.getEmail());

        return userResponse;
    }

    public List<UserResponse> mapToUserResponseList(List<User> users) {
        return users.stream().
                map(this::mapToUserResponse).
                toList();
    }
}
