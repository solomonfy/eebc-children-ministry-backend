package com.eebc.childrenministry.service;

import com.eebc.childrenministry.dto.CreateUserRequest;
import com.eebc.childrenministry.entity.User;

import java.util.List;

public interface UserService {

    List<User> getAllUsers();

    List<User> getUsersByRole(String role);

    List<User> getUsersByStatus(String status);

    List<User> getUsersByRoleAndStatus(String role, String status);

    User getUserById(String id);

    User getUserByEmail(String email);

    User getUserByPhone(String phone);

    User getUserByUserName(String userName);

    User getUserByEmailOrPhone(String email, String phone);

    User getUserByEmailOrUserName(String email, String userName);

//    User createUser(User user);

    User updateUser(String id, User user);

    void deleteUser(String id);

    boolean existsByEmail(String email);

    boolean existsByUserName(String userName);

    boolean existsByPhone(String phone);

    User createUser(CreateUserRequest request);
}