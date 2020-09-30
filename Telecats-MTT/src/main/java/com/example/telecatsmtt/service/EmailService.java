package com.example.telecatsmtt.service;

import com.example.telecatsmtt.entity.UserEntity;
import com.example.telecatsmtt.entity.VerificationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.UUID;


@Service
public class EmailService {

    @Autowired
    private VerificationService verificationService;

    private JavaMailSender javaMailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }




    public void sendEmail(UserEntity userEntity) throws MessagingException {
        VerificationToken verificationToken = verificationService.findByUser(userEntity);

        if (verificationToken != null){
            String token = verificationToken.getVerificationToken();
            Context context = new Context();
            context.setVariable("title", "Verify your email address");
            context.setVariable("link", "http://localhost:9092/activation?token=" + token);

//            create html engine
            String body = templateEngine.process("view/activation", context);

//            send email verification
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(userEntity.getEmail());
            helper.setSubject("Email address verification");
            helper.setText(body, true);

            javaMailSender.send(message);
        }
    }

}
