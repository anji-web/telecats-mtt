package com.example.telecatsmtt.controller;

import com.example.telecatsmtt.entity.UserEntity;
import com.example.telecatsmtt.entity.VerificationToken;
import com.example.telecatsmtt.repository.UserRepository;
import com.example.telecatsmtt.service.EmailService;
import com.example.telecatsmtt.service.KeycloakService;
import com.example.telecatsmtt.service.UserService;
import com.example.telecatsmtt.service.VerificationService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;


@Controller
public class AccountController {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private VerificationService verificationService;

    @Autowired
    private KeycloakService keycloakService;

    @GetMapping("/activation")
    public String activationInfo(@RequestParam("token") String token, Model model){
        VerificationToken verificationToken = verificationService.findByTokenVerif(token);
        if (verificationToken == null){
            model.addAttribute("message", "Your token is invalid");
        }else {
            UserEntity userEntity = verificationToken.getUserEntity();
            if (!userEntity.isEnabled()){
//                get current time
                Date date = new Date(System.currentTimeMillis());
                if (verificationToken.getExpireDate().before(date)){
                    model.addAttribute("message", "Verification token has expired");
                }else {
//                    the token is valid
//                    update the user
                    UserEntity user = userRepository.save(userEntity);
                    user.setEnabled(true);
                    userService.save(userEntity);
                    model.addAttribute("message", "Your email verification success");
                }
            }else {
                model.addAttribute("message", "your account already exist");
            }
        }
        return "view/verifi_activation";
    }


//    @Autowired
//    private EmailService emailService;
//
//    @RequestMapping("send-email")
//    public String sendEmail(UserEntity userEntity){
//        userEntity.setEmail("anjipes2019@gmail.com");
//        try {
//            emailService.sendEmail(userEntity);
//        }catch (MailException mailException){
//            System.out.println("you got error " + mailException.getMessage());
//        }
//
//        return "Please check your email for verification account !!";
//    }
//



    
}
