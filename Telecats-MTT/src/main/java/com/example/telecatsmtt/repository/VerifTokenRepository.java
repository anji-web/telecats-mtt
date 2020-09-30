package com.example.telecatsmtt.repository;

import com.example.telecatsmtt.entity.UserEntity;
import com.example.telecatsmtt.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VerifTokenRepository extends JpaRepository<VerificationToken , Long> {
    @Query(value = "select * from verif_token where verification_token = ?1", nativeQuery = true)
    VerificationToken findByToken(String verificationToken);

    @Query(value = "select * from verif_token where id_user = ?1", nativeQuery = true)
    VerificationToken findByUser(UserEntity userEntity);
}
