package com.example.peoplecenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.peoplecenter.common.BaseResponse;
import com.example.peoplecenter.common.ErrorCode;
import com.example.peoplecenter.common.ResultUtil;
import com.example.peoplecenter.exception.BusinessException;
import com.example.peoplecenter.model.User;
import com.example.peoplecenter.model.request.login;
import com.example.peoplecenter.model.request.regist;
import com.example.peoplecenter.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.javassist.util.proxy.RuntimeSupport;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.peoplecenter.common.ErrorCode.*;
import static com.example.peoplecenter.constant.UserContant.ADMIN_ROLE;
import static com.example.peoplecenter.constant.UserContant.USER_LOGIN_STATE;



@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    UserService userService;

    /**
     * 用户注册
     * @param regist
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<Long> UserRegister(@RequestBody regist regist){
        if (regist == null){
           throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        String userAccount = regist.getUserAccount();
        String userPassword = regist.getUserPassword();
        String checkPassword = regist.getCheckPassword();
        String planetCode = regist.getPlanetCode();
        if (StringUtils.isAnyBlank(userAccount,userPassword,checkPassword)){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }

        long result = userService.userRegist(userAccount, userPassword, checkPassword, planetCode);
        return ResultUtil.success(result);
    }

    /**
     * 用户登录
     * @param login
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<User> UserLogin(@RequestBody login login, HttpServletRequest request){
        if (login == null){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        String userAccount = login.getUserAccount();
        String userPassword = login.getUserPassword();

        if (StringUtils.isAnyBlank(userAccount,userPassword)){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtil.success(user);
    }

    /**
     * 用户注銷
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Integer> UserLogout( HttpServletRequest request){
        if (request == null){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        int result = userService.userlogout(request);
        return ResultUtil.success(result);
    }


    /**
     * 获取登录状态
     * @param request
     * @return
     */
    @GetMapping("/current")
    public BaseResponse<User> current(HttpServletRequest request){
        Object userobj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentuser =(User) userobj;
        if (currentuser == null){
            throw new BusinessException(NOT_LOGIN);
        }
        Long id = currentuser.getId();
//        todo 校验用户是否合法
        User safetyUser = userService.getById(id);
        User result = userService.getSafetyUser(safetyUser);
        return ResultUtil.success(result);
    }
    /**
     * 用户查询
     * @param username 用户名字
     * @return
     */
    @GetMapping("/search")
    public BaseResponse <List<User>> usersearch(String username,HttpServletRequest request){
        boolean admin = isAdmin(request);
        if (!admin){
            throw new BusinessException(NO_AUTH,"缺少管理员权限");
        }
        QueryWrapper<User> queryWrapper =new QueryWrapper<>();
        if (StringUtils.isNoneBlank(username)){
            queryWrapper.like("username",username);
        }
        List<User> userList = userService.list(queryWrapper);
        List<User> result = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtil.success(result);
    }
    /**
     * 用户删除
     * @param id 用户id
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse <Boolean> userdelete(@RequestBody int id, HttpServletRequest request){
        boolean admin = isAdmin(request);
        if (!admin){
            throw new BusinessException(NO_AUTH);
        }
        if (id <= 0){
            throw new BusinessException(PARAM_ERROR);
        }
        boolean result = userService.removeById(id);
        return ResultUtil.success(result);
    }

    /**
     * 是否为管理员
     * @param request
     * @return
     */
    private boolean isAdmin(HttpServletRequest request){
        Object userobj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User use =(User) userobj;
        return use != null && use.getUserRole() == ADMIN_ROLE;
    }



}
