package com.mceil.user.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mceil.common.enums.ExceptionEnum;
import com.mceil.common.exception.McException;
import com.mceil.common.utils.NumberUtils;
import com.mceil.common.vo.PageResult;
import com.mceil.user.mapper.UserMapper;
import com.mceil.user.mapper.UserRoleMapper;
import com.mceil.user.pojo.User;
import com.mceil.user.pojo.UserRole;
import com.mceil.user.service.UserService;
import com.mceil.utils.CodecUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {


    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private AmqpTemplate amqpTemplate;
    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final Long INIT_ROLE = 2L;
    private static final String KEY_PREFIX = "user:verify:phone:";

    //数据校验   用于前端检验用户名和电话号码是否在后台被注册过
    @Override
    public Boolean checkData(String data, Integer type) {
        User user = new User();
        //判断数据类型
        switch (type) {
            case 1:
                user.setUsername(data);
                break;
            case 2:
                user.setPhone(data);
                break;
            default:
                throw new McException(ExceptionEnum.INVALID_USER_DATA_TYPE);
        }
        return userMapper.selectCount(user) == 0;
    }


    @Override
    //发送验证码
    public void sendCode(String phone) {
        //生成key
        String key = KEY_PREFIX + phone;
        //生成验证码
        String code = NumberUtils.generateCode(6);
        Map<String, String> msg = new HashMap<>();
        msg.put("phone", phone);
        msg.put("code", code);
        //发送验证码
        amqpTemplate.convertAndSend("mc.sms.exchange", "sms.verify.code", msg);
        //保存验证码
        redisTemplate.opsForValue().set(key, code, 1, TimeUnit.MINUTES);

        String cacheCode = redisTemplate.opsForValue().get(KEY_PREFIX + phone);
        System.out.println("验证码："+cacheCode);
    }

    @Override
    public void register(String phone, String password, String code) {
        User user = new User();
        //从redis中取出验证码
        String cacheCode = redisTemplate.opsForValue().get(KEY_PREFIX + phone);
        //校验验证码
        if (!StringUtils.equals(code, cacheCode)) {
            throw new McException(ExceptionEnum.INVALID_VERIFY_CODE);
        }
        user.setPhone(phone);
        //生成盐
        String salt = CodecUtils.generateSalt();
        user.setUsername(user.getPhone());
        user.setSalt(salt);
        // 对密码加密
        user.setPassword(CodecUtils.md5Hex(password, salt));
        //写入数据库
        user.setCreated(new Date());
        userMapper.insert(user);
        //加入权限表
        User one = userMapper.selectOne(user);
        UserRole userRole = new UserRole();
        userRole.setId(user.getId());
        userRole.setRole(INIT_ROLE);
        userRoleMapper.insert(userRole);

    }

    @Override
    public User queryUserByUsernameAndPassword(String username, String password) {
        //查询用户
        User recode = new User();
        recode.setUsername(username);
        User user = userMapper.selectOne(recode);
        //校验
        if (user == null) {
            /*throw new McException(ExceptionEnum.INVALID_USERNAME_PASSWORD);*/
            return null;
        }
        //校验密码
        if (!StringUtils.equals(user.getPassword(), CodecUtils.md5Hex(password, user.getSalt()))) {
            /*throw new McException(ExceptionEnum.INVALID_USERNAME_PASSWORD);*/
            return null;
        }
        UserRole userRole = userRoleMapper.selectByPrimaryKey(user.getId());
        user.setRole(userRole.getRole());
        //用户名和密码正确
        return user;
    }

    @Override
    public PageResult<User> queryUserListPage(Integer page, Integer rows, String sortBy, Boolean desc, String key) {
        //分页
        PageHelper.startPage(page, rows);
        //过滤
        Example example = new Example(User.class);
        if (StringUtils.isNotBlank(key)) {
            //过滤条件
            example.createCriteria().orLike("name", "%" + key + "%");
        }
        //排序
        if (StringUtils.isNotBlank(sortBy)) {
            example.setOrderByClause(sortBy + (desc ? " DESC" : " ASC"));
            //这里id ASC之间要有空格隔开，要不然分页助手会识别不了
        }
        //查询
        List<User> userList = userMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(userList)) {
            throw new McException(ExceptionEnum.USER_NOT_FOUND);
        }
        List<Long> uIds = userList.stream().map(User::getId).collect(Collectors.toList());
        List<UserRole> roleList = userRoleMapper.selectByIdList(uIds);
        int flag = 0;
        for (User user : userList) {
            user.setRole(roleList.get(flag).getRole());
            flag++;
        }
        //解析分页结果
        PageInfo<User> info = new PageInfo<>(userList);
        return new PageResult<>(info.getTotal(), userList);
    }

    @Override
    @Transactional
    public void UpdateUserRole(UserRole userRole) {
        int count = userRoleMapper.updateByPrimaryKeySelective(userRole);
        if (count != 1) {
            throw new McException(ExceptionEnum.USER_UPDATE_ROLE_ERROR);
        }
    }

    @Override
    @Transactional
    public void updatePassword(String username, String oldPassword, String newPassword) {
        User user = queryUserByUsernameAndPassword(username,oldPassword);
        if(user == null){
            throw new McException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
        }
        String salt = CodecUtils.generateSalt();
        user.setSalt(salt);
        user.setPassword(CodecUtils.md5Hex(newPassword, salt));
        userMapper.updateByPrimaryKeySelective(user);
    }

    @Override
    @Transactional
    public void findPassword(String phone, String password, String code) {
        //从redis中取出验证码
        String cacheCode = redisTemplate.opsForValue().get(KEY_PREFIX + phone);
        //校验验证码
        if (!StringUtils.equals(code, cacheCode)) {
            throw new McException(ExceptionEnum.INVALID_VERIFY_CODE);
        }
        User record = new User();
        record.setPhone(phone);
        User user = userMapper.selectOne(record);
        String salt = CodecUtils.generateSalt();
        user.setSalt(salt);
        user.setPassword(CodecUtils.md5Hex(password, salt));
        userMapper.updateByPrimaryKeySelective(user);
    }

    @Override
    @Transactional
    public void perfectUser(User user) {
        userMapper.updateByPrimaryKeySelective(user);
    }

    @Override
    public User queryUserById(Long id) {
        return userMapper.selectByPrimaryKey(id);
    }


}
