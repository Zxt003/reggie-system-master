package com.zgc.app.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zgc.app.entity.User;
import com.zgc.app.mapper.UserMapper;
import com.zgc.app.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

/**
 * <p>
 * 用户信息 服务实现类
 * </p>
 *
 * @author DancingHorse
 * @since 2023-03-16
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Override
    public boolean login(String phone) {
        // 检索数据库，用户是否存在【存在：登录，不存在：注册】
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(User::getPhone, phone);
        User one = this.getOne(queryWrapper);
        if (ObjectUtils.isEmpty(one)) {
            // 不存在 - 注册
            User newUser = new User();
            // newUser.setId(0L);
            newUser.setName("手机用户+" + phone);
            newUser.setPhone(phone);
            return this.save(newUser);
        }
        // 用户存在 - 登录
        return true;
    }
}
