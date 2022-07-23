package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    //前端页面自动填充验证码了，此处登陆也无需校验。
    @PostMapping("/login")
    public R<User> login(@RequestBody User user, HttpSession session){
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, user.getPhone());
        User user1 = userService.getOne(wrapper);
        if ((user1) == null){
            user1 = new User();
            user1.setPhone(user.getPhone());
            user1.setStatus(1);
            userService.save(user1);
        }
        session.setAttribute("user", user1.getId());
        return R.success(user1);
    }

}
