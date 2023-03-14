package com.example.springsecurity.client.event.listener;

import com.example.springsecurity.client.entity.User;
import com.example.springsecurity.client.event.RegistrationCompleteEvent;
import com.example.springsecurity.client.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent> {

    @Autowired
    private UserService userService;

    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        userService.saveVerificationTokenForUser(token, user);

        String url = event.getApplicationUrl() + "/verifyRegistration?token=" + token;

        log.info("Click the link to verify your account: " + url);
    }
}
