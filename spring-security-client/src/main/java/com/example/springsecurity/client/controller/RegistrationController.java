package com.example.springsecurity.client.controller;

import com.example.springsecurity.client.entity.User;
import com.example.springsecurity.client.event.RegistrationCompleteEvent;
import com.example.springsecurity.client.model.UserModel;
import com.example.springsecurity.client.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegistrationController {

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @PostMapping("/register")
    public String registerUser(@RequestBody UserModel userModel) {
        User registeredUser = userService.registerUser(userModel);
        eventPublisher.publishEvent(new RegistrationCompleteEvent(
                registeredUser,
                "url")
        );
        return "User registered successfully";
    }
}
