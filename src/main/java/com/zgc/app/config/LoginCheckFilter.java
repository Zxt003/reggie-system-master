package com.zgc.app.config;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zgc.app.common.Constant;
import com.zgc.app.common.R;
import com.zgc.app.entity.Employee;
import com.zgc.app.entity.User;
import com.zgc.app.service.IUserService;
import com.zgc.app.utils.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ObjectUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
@Slf4j
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    @Autowired
    private IUserService userService;

    /**
     * 1. 所有的链接都会被过滤器识别
     *      有些链接，资源不需要被过滤
     *      有些链接，需要被过滤
     *
     * 2. 判断资源过滤的条件【什么链接才需要过滤】
     *
     * 3. 判断用户是否登录【登录成功执行，不成功返回前端 NOTLOGIN信息】
     */
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        //log.warn("过滤器执行中~~~"+request.getRequestURI());
        //log.error(Thread.currentThread().getName());

        if (checkRequest(request)) {
            //需要登录
            if (!checkEmployeeLogin(request)) {
                //未登录
                String jsonR = JSON.toJSONString(R.error().setMsg(Constant.EMPLOYEE_NOT_LOGIN));
                response.setContentType("application/json");
                response.getWriter().print(jsonR);
                return;
            }else{
                HttpSession session = request.getSession();
                Object userSession = session.getAttribute(Constant.EMPLOYEE_SEESION);
                if(userSession instanceof Employee){
                    Employee attribute = (Employee)userSession;
                    BaseContext.setCurrentId(attribute.getId());
                }else{
                    String phone = userSession.toString();
                    QueryWrapper<User> userWrapper = new QueryWrapper<>();
                    userWrapper.lambda().select(User::getId);
                    userWrapper.lambda().eq(User::getPhone, phone);
                    User user = userService.getOne(userWrapper);
                    BaseContext.setCurrentId(user.getId());
                }
                // 将当前登录用户的ID存入【1.静态变量Map /2. 线程本身的存储区域】
                // 1. Constant.map.put(Thread.currentThread().getName(), attribute.getId());
            }
        }
        chain.doFilter(request,response);//放行
    }

    /**
     * 检查是否有员工登录
     * @param request
     * @return 如果员工登录了 返回true 未登录 返回false
     */
    private boolean checkEmployeeLogin(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Object attribute = session.getAttribute(Constant.EMPLOYEE_SEESION);
        if (ObjectUtils.isEmpty(attribute)) {
            return false;
        }
        return true;
    }

    /**
     * 判断资源过滤的条件
     *  【什么链接才需要过滤】
     * @param request
     * @return 是否需要登录 是 true 否 false
     */
    private boolean checkRequest(HttpServletRequest request) {
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        String uri = request.getRequestURI();
        List<String> unLogin = Arrays.asList(
                "/employee/login",
                "/employee/logout",
                "/user/send",
                "/user/login",
                "/backend/**",
                "/front/**");
        for (String unLoginUrl : unLogin) {
            boolean match = antPathMatcher.match(unLoginUrl, uri);
            if(match){
                return false;
            }
        }
        return true;
    }
}
