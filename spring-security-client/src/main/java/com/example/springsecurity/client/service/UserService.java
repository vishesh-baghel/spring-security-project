package com.example.springsecurity.client.service;

import com.example.springsecurity.client.entity.User;
import com.example.springsecurity.client.model.UserModel;

public interface UserService {
    User registerUser(UserModel userModel);
}
