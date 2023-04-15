package com.zgc.app.config.security;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zgc.app.entity.Employee;
import com.zgc.app.service.IEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class MyUserDetailsService implements UserDetailsService {
    //获取员工
    @Autowired
    private IEmployeeService employeeService;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 获取用户
        QueryWrapper<Employee> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Employee::getUsername,username);
        Employee employee = employeeService.getOne(queryWrapper);
        System.out.println(employee);
        User user = new User(employee.getUsername(), employee.getPassword(), AuthorityUtils.createAuthorityList("EMPLOYEE"));
        return user;
    }
}
