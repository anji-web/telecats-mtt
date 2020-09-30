package com.example.telecatsmtt.service;

import com.example.telecatsmtt.entity.UserEntity;
import com.example.telecatsmtt.entity.VerificationToken;
import com.example.telecatsmtt.repository.VerifTokenRepository;
import org.apache.james.mime4j.field.datetime.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class VerificationService {

    @Autowired
    private VerifTokenRepository verifTokenRepository;

    @Transactional
    public VerificationToken findByTokenVerif(String token){
        return verifTokenRepository.findByToken(token);
    }

    @Transactional
    public VerificationToken findByUser(UserEntity userEntity){
        return verifTokenRepository.findByUser(userEntity);
    }

    @Transactional
    public void save(UserEntity userEntity, String verification_token){
        VerificationToken token = new VerificationToken(verification_token, userEntity);
        token.setExpireDate(calculateExpireDate(24*60));

        verifTokenRepository.save(token);
    }


        public Date calculateExpireDate(int expiryTimeMinutes){
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.MINUTE, expiryTimeMinutes);
            return new Date(cal.getTime().getTime());
        }

}
