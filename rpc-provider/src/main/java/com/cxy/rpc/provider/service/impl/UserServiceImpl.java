package com.cxy.rpc.provider.service.impl;

import com.cxy.rpc.api.pojo.User;
import com.cxy.rpc.api.service.UserService;
import com.cxy.rpc.server.annotation.RpcService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author cxy
 * @version 1.0
 * @ClassName UserServiceImpl
 */
@RpcService(interfaceClass = UserService.class)
public class UserServiceImpl implements UserService {

    @Override
    public User queryUser() {
        return new User("hwd", "123456", 25);
    }

    @Override
    public List<User> getAllUsers() {
        // 注意：直接使用 Arrays.ArrayList 会导致序列化异常
        return new ArrayList<>(Arrays.asList(new User("xm", "123456", 23),
                new User("hwd", "123456", 23),
                new User("hwd", "123456", 24)));
    }
}
