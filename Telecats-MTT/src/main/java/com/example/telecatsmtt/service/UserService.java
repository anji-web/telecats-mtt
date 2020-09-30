package com.example.telecatsmtt.service;

import com.example.telecatsmtt.entity.UserEntity;
import com.example.telecatsmtt.repository.UserRepository;
import com.example.telecatsmtt.repository.VerifTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private VerificationService verificationService;

    @Autowired
    private VerifTokenRepository verifTokenRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;
 

    private BCryptPasswordEncoder encoder;

    @Transactional
    public UserEntity save(UserEntity userEntity){
        return userRepository.save(userEntity);
    }

    public UserEntity register(UserEntity userEntity){
        UserEntity user = userRepository.save(userEntity);
        user.setEnabled(false);

        Optional<UserEntity> saved = Optional.of(save(user));

        saved.ifPresent(userEntity1 -> {
            try {
                String token = UUID.randomUUID().toString();
                verificationService.save(saved.get(), token);

//                send email verification
                emailService.sendEmail(userEntity1);



            }catch (Exception e){
                e.printStackTrace();
            }

        });

        return saved.get();
    }

}
