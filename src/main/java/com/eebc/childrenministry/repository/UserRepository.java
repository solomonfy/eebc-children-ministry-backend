package com.eebc.childrenministry.repository;

import com.eebc.childrenministry.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    List<User> findByRole(String role);

    List<User> findByStatus(String status);

    List<User> findByRoleAndStatus(String role, String status);

    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);

    Optional<User> findByUserName(String userName);

    // Used for generating unique usernames like kim.john001, kim.john002 ...
    List<User> findByUserNameStartingWith(String prefix);

    Optional<User> findByEmailOrPhone(String email, String phone);

    Optional<User> findByEmailOrUserName(String email, String userName);

    boolean existsByEmail(String email);

    boolean existsByUserName(String userName);

    boolean existsByPhone(String phone);
}