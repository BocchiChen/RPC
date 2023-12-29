package com.cxy.rpc.api.service;

import com.cxy.rpc.api.pojo.User;

import java.util.List;

public interface UserService {
    User queryUser();

    List<User> getAllUsers();

}
