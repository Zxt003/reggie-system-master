package com.zgc.app.config.security;

import com.alibaba.fastjson2.JSON;
import com.zgc.app.common.Constant;
import com.zgc.app.common.R;
import com.zgc.app.entity.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private RenttyPoint renttyPoint;
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 放行资源 js/css/图片/  后台的首页，前台的首页，
        http.authorizeRequests().antMatchers("/login").permitAll();
        http.authorizeRequests().antMatchers("/user/login","/user/send").permitAll();
        http.authorizeRequests().antMatchers("/backend/**","/front/**").permitAll();
        //-----------------
        http.formLogin().successHandler((request,response,authentication)->{
            // 框架认证成功后 回调
            Object userInfo= authentication.getPrincipal();//需要当前登录的用户对象信息
            request.getSession().setAttribute(Constant.EMPLOYEE_SEESION, userInfo);
            String jsonR = JSON.toJSONString(R.success(userInfo));
            response.setContentType("application/json");
            response.getWriter().print(jsonR);
        }).failureHandler((request,response,authenticationException)->{
            // 框架认证失败后 回调
            String jsonR = JSON.toJSONString(R.error().setMsg(Constant.EMPLOYEE_NOT_LOGIN));
            response.setContentType("application/json");
            response.getWriter().print(jsonR);
        });
        // 用户身份校验异常
        // http.exceptionHandling().authenticationEntryPoint(renttyPoint);
        // 页面嵌套 安全参数
        http.headers().frameOptions().sameOrigin();
        // 验证过程由框架完成
        // 验证后的结果处理，我们需要对接自己的业务
        // 返回Json
        http.csrf().disable();
        http.cors().disable();
        super.configure(http);
    }
}
