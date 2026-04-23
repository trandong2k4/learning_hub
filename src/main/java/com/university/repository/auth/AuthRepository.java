package com.university.repository.auth;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.university.entity.Users;

public interface AuthRepository extends JpaRepository<Users, UUID> {
    Users findByEmail(String keyword);

    boolean existsByUserName(String usersname);

}
