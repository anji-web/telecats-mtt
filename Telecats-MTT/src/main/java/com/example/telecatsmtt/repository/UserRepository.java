package com.example.telecatsmtt.repository;

import com.example.telecatsmtt.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
}
