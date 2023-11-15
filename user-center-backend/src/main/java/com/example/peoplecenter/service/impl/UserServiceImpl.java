package com.example.peoplecenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.peoplecenter.common.ErrorCode;
import com.example.peoplecenter.exception.BusinessException;
import com.example.peoplecenter.mapper.UserMapper;
import com.example.peoplecenter.model.User;
import com.example.peoplecenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.peoplecenter.constant.UserContant.USER_LOGIN_STATE;

/**
* @author PC
* @description 针对表【user】的数据库操作Service实现
* @createDate 2023-11-08 20:45:11
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{

    @Resource
    UserMapper userMapper;

    /**
     * 盐值，混淆密码
     */
    private final String SALT = "LOVE";


    /**
     * 注册
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param checkPassword 校验密码
     * @return
     */
    @Override
    public long userRegist(String userAccount, String userPassword, String checkPassword ,String planetCode) {
//       非空
        if (StringUtils.isAnyBlank(userAccount,userPassword,checkPassword,planetCode)) {
            System.out.println("is null");
            throw  new BusinessException(ErrorCode.PARAM_ERROR,"参数为空");
        }
//      账户不小于4位
        if (userAccount.length() < 4){
            System.out.println("userAccount xiaoyu 4");
            throw  new BusinessException(ErrorCode.PARAM_ERROR,"用户账户过短");

        }
        //      密码不小于8位
        if (userPassword.length() < 8 || checkPassword.length() < 8){
            System.out.println("userPassword xiaoyu 8 or checkPassword xiaoyu 8");
            throw  new BusinessException(ErrorCode.PARAM_ERROR,"用户密码过短");

        }
        if (planetCode.length() > 5){
            throw  new BusinessException(ErrorCode.PARAM_ERROR,"星球编号过长");
        }

        //     账户不能含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if(matcher.find()){
            System.out.println("userAccount is novalid");
            throw  new BusinessException(ErrorCode.PARAM_ERROR,"账号不合法");
        }

        //      密码与校验密码不相同
        if (!userPassword.equals(checkPassword)){
            System.out.println(" checkPassword  and userPassword are no same");
            throw  new BusinessException(ErrorCode.PARAM_ERROR,"密码与校验密码不对");
        }
        //      账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = userMapper.selectCount(queryWrapper);
        System.out.println("count:"+count);
        if (count > 0) {
            System.out.println(" userAccount  is same");
            throw  new BusinessException(ErrorCode.PARAM_ERROR,"账户重复");
        }
        //      星球编号不能重复
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("planetCode", planetCode);
        count = userMapper.selectCount(queryWrapper);
        System.out.println("count:"+count);
        if (count > 0) {
            System.out.println(" planetCode  is same");
            throw  new BusinessException(ErrorCode.PARAM_ERROR,"星球编号重复");
        }

//        密码加密
        String newpassword= DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
//        插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(newpassword);
        user.setPlanetCode(planetCode);
        boolean saveResult = this.save(user);
        if (!saveResult){
            System.out.println(" save  is false");
            throw  new BusinessException(ErrorCode.PARAM_ERROR,"保存失败");
        }
        return user.getId();
    }

    /**
     * 登录
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param request 请求
     * @return 脱敏后user对象
     */
    @Override
    public User userLogin(String userAccount, String userPassword,HttpServletRequest request) {
        //非空
        if (StringUtils.isAnyBlank(userAccount,userPassword)){
            throw  new BusinessException(ErrorCode.PARAM_ERROR,"参数为空");
        }
        //      账户不小于4位
        if (userAccount.length() < 4){
            throw  new BusinessException(ErrorCode.PARAM_ERROR,"用户账户过短");
        }
        //      密码不小于8位
        if (userPassword.length() < 8 ){
            throw  new BusinessException(ErrorCode.PARAM_ERROR,"用户密码过短");
        }
        //     账户不能含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if(matcher.find()){
            throw  new BusinessException(ErrorCode.PARAM_ERROR,"账号不合法");
        }
//      密码加密
        String newpassword= DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
//      查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        queryWrapper.eq("userPassword",newpassword);
        User user =userMapper.selectOne(queryWrapper);
        if (user == null){
            log.info(" user login false");
            throw  new BusinessException(ErrorCode.PARAM_ERROR,"账号重复");
        }
//        用户脱敏
        User safetyUser = getSafetyUser(user);
//        记录用户的状态
        request.getSession().setAttribute(USER_LOGIN_STATE,safetyUser);
        return safetyUser;
    }

    /**
     * 用户脱敏
     * @param originUser
     * @return
     */
    @Override
    public User getSafetyUser(User originUser){
        if (originUser == null){
            throw  new BusinessException(ErrorCode.NULL_ERROR,"账号不存在");
        }
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setPlanetCode(originUser.getPlanetCode());
        safetyUser.setUserStatus(originUser.getUserStatus());
        safetyUser.setCreateTime(originUser.getCreateTime());
        safetyUser.setUpdateTime(originUser.getUpdateTime());
        return safetyUser;
    }

    @Override
    public int userlogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        throw  new BusinessException(ErrorCode.SUCCESS,"注销成功");
    }

}




