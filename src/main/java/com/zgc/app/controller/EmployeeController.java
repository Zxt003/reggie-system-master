package com.zgc.app.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zgc.app.common.Constant;
import com.zgc.app.common.R;
import com.zgc.app.entity.Employee;
import com.zgc.app.exception.ServiceException;
import com.zgc.app.service.IEmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;

/**
 * <p>
 * 员工信息 前端控制器
 * </p>
 *
 * @author DancingHorse
 * @since 2023-03-16
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    /**
     * 想要引入 对应的 业务层依赖
     * 完成对应的五个接口Rest风格
     */
    @Autowired
    private IEmployeeService baseService;

    /**
     * 前端需要的数据——————
     * 用户登录成功：返回 R 1 + 用户详情
     * 用户登录失败：返回 R 0
     * ——————
     * 后端：用户登录通过Session来控制
     * 用户登陆成功之后，将用户信息存入Session对象中
     * 用户登录失败就不需要了
     */
    @PostMapping("/login")
    public R login(@RequestBody Employee employee, HttpSession session) {
        Employee userInfo = baseService.login(employee);// 登录失败，业务层内部抛异常，通过统一异常处理
        session.setAttribute(Constant.EMPLOYEE_SEESION, userInfo);
        return R.success(userInfo);
    }

    @PostMapping("/logout")
    public R logout(HttpSession session) {
        session.invalidate();
        return R.success();
    }

    /* 判断用户是否登录，Session，如果未登录，返回R 0 NOTLOGIN
    Object attribute = session.getAttribute(Constant.EMPLOYEE_SEESION);
    if (ObjectUtils.isEmpty(attribute)) {
        return R.error("NOTLOGIN");
    } 过滤器已实现*/
    @GetMapping("/page")
    public R search(HttpSession session,
                    @RequestParam(value = "name", required = false) String name,
                    @RequestParam(value = "page", required = false, defaultValue = "1") Integer current,
                    @RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer size) {
        QueryWrapper<Employee> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().like(!StringUtils.isEmpty(name), Employee::getName, name);
        Page<Employee> page = baseService.page(new Page<>(current, size), queryWrapper);
        return R.success(page);
    }

    @GetMapping("/{id}")
    public R getOne(@PathVariable("id") Long id) {
        Employee employee = baseService.getById(id);
        return R.success(employee);
    }


    @PutMapping
    public R edit(@RequestBody Employee entity) {
        QueryWrapper<Employee> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Employee::getUsername, entity.getUsername());
        queryWrapper.lambda().ne(Employee::getId, entity.getId());
        if (!ObjectUtils.isEmpty(baseService.getOne(queryWrapper))) {
            // 添加时，通过用户名查询出来的结果 不为空【说明 该用户已存在】
            throw new ServiceException("当前用户名已存在");
        }
        // 目的：如果用户名冲突了，我们给用户一个良好的提示
        entity.setUpdateUser(null);
        entity.setUpdateTime(null);
        entity.setPassword(null);
        //---
        if (baseService.updateById(entity)) {
            return R.success();
        }
        return R.error();
    }

    @PostMapping
    public R save(@RequestBody Employee entity) {
        // 目的：如果用户名冲突了，我们给用户一个良好的提示
        QueryWrapper<Employee> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Employee::getUsername, entity.getUsername());
        if (!ObjectUtils.isEmpty(baseService.getOne(queryWrapper))) {
            // 添加时，通过用户名查询出来的结果 不为空【说明 该用户已存在】
            throw new ServiceException("当前用户名已存在");
        }
        //保存员工 需要给与数据的默认值
        entity.setPassword(Constant.EMPLOYEE_DEFAULT_PASSWORD);
        if (baseService.save(entity)) {
            return R.success();
        }
        return R.error();
    }
}
















