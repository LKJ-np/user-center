package com.example.peoplecenter.service;

import com.example.peoplecenter.model.User;
import com.baomidou.mybatisplus.extension.service.IService;
import javax.servlet.http.HttpServletRequest;

/**
* @author PC
* @description 针对表【user】的数据库操作Service
* @createDate 2023-11-08 20:45:11
*/
public interface UserService extends IService<User> {

    /**
     *
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param checkPassword 校验密码
     * @return 用户id
     */
    long userRegist(String userAccount , String userPassword ,String checkPassword,String planetCode);

    /**
     *
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户脱敏
     * @param originUser
     * @return
     */
    User getSafetyUser(User originUser);

    /**
     * 用戶注銷
     * @param request
     * @return
     */
    int userlogout(HttpServletRequest request);
}
