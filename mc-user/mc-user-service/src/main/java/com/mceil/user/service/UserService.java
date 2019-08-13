package com.mceil.user.service;


import com.mceil.common.vo.PageResult;
import com.mceil.user.pojo.User;
import com.mceil.user.pojo.UserRole;

public interface UserService {

    //数据校验   用于前端检验用户名和电话号码是否在后台被注册过
    Boolean checkData(String data, Integer type);
    //发送验证码
    void sendCode(String phone);
    //注册
    void register(String phone, String password, String code);
    //查询用户
    User queryUserByUsernameAndPassword(String username, String password);
    //分页查询名字Like关键字的用户
    PageResult<User> queryUserListPage(Integer page, Integer rows, String sortBy, Boolean desc, String key);
    //更新用户权限
    void UpdateUserRole(UserRole userRole);

    void updatePassword(String username, String oldPassword, String newPassword);

    void findPassword(String phone, String password, String code);

    void perfectUser(User user);

    User queryUserById(Long id);

}
