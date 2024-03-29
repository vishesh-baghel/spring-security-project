package com.example.springsecurity.client.controller;

import com.example.springsecurity.client.entity.User;
import com.example.springsecurity.client.entity.VerificationToken;
import com.example.springsecurity.client.event.RegistrationCompleteEvent;
import com.example.springsecurity.client.model.PasswordModel;
import com.example.springsecurity.client.model.UserModel;
import com.example.springsecurity.client.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
public class RegistrationController {

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @PostMapping("/register")
    public String registerUser(@RequestBody UserModel userModel, final HttpServletRequest request) {
        User registeredUser = userService.registerUser(userModel);
        eventPublisher.publishEvent(new RegistrationCompleteEvent(
                registeredUser,
                applicationUrl(request)
        ));
        return "User registered successfully";
    }

    @GetMapping("/verifyRegistration")
    public String verifyRegistration(@RequestParam("token") String token) {
        String result = userService.validateVerificationToken(token);
        if (result.equalsIgnoreCase("valid")) {
            return "Your account has been verified successfully";
        }
        return "Your account verification failed. Please try again";
    }

    @GetMapping("/resendRegistrationToken")
    public String resendVerificationToken(@RequestParam("token") String existingToken, HttpServletRequest request) {
        VerificationToken newToken = userService.generateNewVerificationToken(existingToken);
        User user = newToken.getUser();
//        eventPublisher.publishEvent(new RegistrationCompleteEvent(
//                user,
//                applicationUrl(null)
//        ));
        resendVerificationTokenMail(user, applicationUrl(request), newToken);
        return "Verification link sent and new token generated successfully";
    }

    @PostMapping("/resetPassword")
    public String resetPassword(@RequestBody PasswordModel passwordModel, HttpServletRequest request) {
        User user = userService.findUserByEmail(passwordModel.getEmail());
        String url = "";
        if (user != null) {
            String token = UUID.randomUUID().toString();
            userService.createPasswordResetTokenForUser(user, token);
            url = passwordResetTokenMail(user, applicationUrl(request), token);
        }
        return url;
    }

    @PostMapping("/savePassword")
    public String savePassword(@RequestParam("token") String token, @RequestBody PasswordModel passwordModel) {
        String result = userService.validatePasswordResetToken(token);

        if (!result.equalsIgnoreCase("valid")) {
            return "Invalid token";
        }
        Optional<User> user = userService.getUserByPasswordResetToken(token);
        if (user.isPresent()) {
            userService.changePassword(user.get(), passwordModel.getNewPassword());
            return "Password reset successfully";
        } else {
            return "Invalid token";
        }

    }

    @PostMapping("/changePassword")
    public String changePassword(@RequestBody PasswordModel passwordModel) {
        User user = userService.findUserByEmail(passwordModel.getEmail());
        if (!userService.checkIfValidOldPassword(user, passwordModel.getOldPassword())) {
            return "Invalid old password";
        }
        userService.changePassword(user, passwordModel.getNewPassword());
        return "Password changed successfully";
    }

    private String passwordResetTokenMail(User user, String applicationUrl, String token) {
        String url = applicationUrl + "/savePassword?token=" + token;
        log.info("Click the link to reset your password: " + url);
        return url;
    }

    private void resendVerificationTokenMail(User user, String applicationUrl, VerificationToken newToken) {
        String url = applicationUrl + "/verifyRegistration?token=" + newToken.getToken();
        log.info("Click the link to verify your account: " + url);
    }

    private String applicationUrl(HttpServletRequest request) {
        return "http://" + request.getServerName()
                + ":" + request.getServerPort() + request.getContextPath();
    }
}
