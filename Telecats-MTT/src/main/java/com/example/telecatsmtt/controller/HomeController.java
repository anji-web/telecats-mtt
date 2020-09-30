package com.example.telecatsmtt.controller;

import com.example.telecatsmtt.entity.UserEntity;
import com.example.telecatsmtt.entity.VerificationToken;
import com.example.telecatsmtt.repository.UserRepository;
import com.example.telecatsmtt.service.KeycloakService;
import com.example.telecatsmtt.service.UserService;
import com.example.telecatsmtt.service.VerificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/keycloak-api")
@CrossOrigin

public class HomeController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;


    @Autowired
    private VerificationService verificationService;

    @Autowired
    private KeycloakService keycloakService;

    private final static Logger logger = LoggerFactory.getLogger(HomeController.class);

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody UserEntity userEntity){
        try {
            UserEntity newUser = userService.register(userEntity);
            keycloakService.createUserToken(userEntity);
            return new ResponseEntity<UserEntity>(newUser, HttpStatus.CREATED);
        }catch (Exception e){
            logger.error("Create user error : " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/token")
    public ResponseEntity<?> generateToken(@RequestBody UserEntity userEntity){
        String response = null;
        try {
            if (userEntity.getEmail() == null && userEntity.getPassword() == null){
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }else {
                response = keycloakService.getToken(userEntity);
            }

        }catch (Exception e){
            logger.error("Login failed : " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
         return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
