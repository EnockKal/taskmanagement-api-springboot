package com.enock.taskmanagementapispringboot.repository;

import com.enock.taskmanagementapispringboot.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User getUserByUsername(String username);
    User findByEmail(String email);
}
