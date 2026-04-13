package com.university.repository.student;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

import com.university.entity.Users;

public interface UserRepository extends JpaRepository<Users, UUID> {

    Optional<Users> findByUserName(String userName);

}
