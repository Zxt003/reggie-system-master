package com.zgc.app.service;

import com.zgc.app.entity.Employee;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 员工信息 服务类
 * </p>
 *
 * @author DancingHorse
 * @since 2023-03-16
 */
public interface IEmployeeService extends IService<Employee> {

    Employee login(Employee employee);

}
