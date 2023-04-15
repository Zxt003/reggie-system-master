package com.zgc.app.service;

import com.zgc.app.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户信息 服务类
 * </p>
 *
 * @author DancingHorse
 * @since 2023-03-16
 */
public interface IUserService extends IService<User> {

    boolean login(String phone);
}
