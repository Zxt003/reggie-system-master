package com.zgc.app.controller;

import com.zgc.app.common.Constant;
import com.zgc.app.common.R;
import com.zgc.app.entity.User;
import com.zgc.app.service.IUserService;
import com.zgc.app.utils.AliyunMsgUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService userService;
    @Autowired
    private AliyunMsgUtils msgUtils;

    @PostMapping("/login")
    public R login(@RequestBody User user, HttpSession session) {
        //1. 获取用户提交的电话与 输入的验证码 user.getPhone() user.getCode()
        //2. 获取系统发给用户的验证码
        String sysCode = session.getAttribute(user.getPhone()).toString();
        //3. 对验证码进行匹配
        if (!user.getCode().equals(sysCode)) {
            //3.1 匹配失败则返回失败信息
            return R.error("验证码不正确，请重新输入！");
        }

        //4. 检索数据库，用户是否存在【存在：登录，不存在：注册】
        boolean b = userService.login(user.getPhone());
        if (b) {
            session.setAttribute(Constant.EMPLOYEE_SEESION, user.getPhone());
            return R.success();
        }
        return R.error();
    }

    /**
     * 验证码发送接口
     * 1. 接受用户的手机号
     * 2. 生成对应的验证码 6 位
     * 3. 发送验证到用户手机-
     * 4. 保存验证码 等待校验
     *
     * @param phoneNumber 手机号码
     * @return
     */
    @GetMapping("/send")
    public R sendMessage(@RequestParam("phoneNumber") String phoneNumber, HttpSession session) {
        //1. phoneNumber
        //2. code
        //3. 生成验证码+发送验证码+返回生产的验证码
//         String code = this.msgUtils.sendCodeMsg(phoneNumber);
        String code="000";
        //4. 保存信息等待验证[键phoneNumber，值code]
        session.setAttribute(phoneNumber, code);
        return R.success();
    }

}
