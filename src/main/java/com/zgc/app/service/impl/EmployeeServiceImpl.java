package com.zgc.app.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zgc.app.common.Constant;
import com.zgc.app.common.LoginMsg;
import com.zgc.app.entity.Employee;
import com.zgc.app.exception.ServiceException;
import com.zgc.app.mapper.EmployeeMapper;
import com.zgc.app.service.IEmployeeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.ObjectUtils;

/**
 * <p>
 * 员工信息 服务实现类
 * </p>
 *
 * @author DancingHorse
 * @since 2023-03-16
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements IEmployeeService {
    /**
     * 实现登录业务 判断用户登录是否成功，成功返回用户信息，失败抛异常与对应提示【通过统一异常来处理】
     * ---
     *  密码需要加密
     *      用户提交来的是明文
     *      数据存储的是密文
     *      设定明文与明文的匹配方式
     */
    @Override
    public Employee login(Employee employee) {
        QueryWrapper<Employee> queryWrapper = new QueryWrapper<Employee>();
        queryWrapper.lambda().eq(Employee::getUsername,employee.getUsername());
        Employee result = this.getOne(queryWrapper);
        // 如果result有值 说明通过当前用户名作为条件 查询出来数据了，
        if (!ObjectUtils.isEmpty(result)) {
            // 说明密码输入正确
            if (checkPassword(employee.getPassword(),result.getPassword())) {
                //如果密码输入成功，判断用户是否可用
                if (checkEmployeeNormal(result)) {
                    return result;
                }
                throw new ServiceException(LoginMsg.EMPLOYEE_BLOCK);
            }
            throw new ServiceException(LoginMsg.EMPLOYEE_PASSWORD_ERROR);
        }
        throw new ServiceException(LoginMsg.EMPLOYEE_NO_EXIST);
    }

    private boolean checkEmployeeNormal(Employee result) {
        return result.getStatus()== Constant.EMPLOYEE_NORMAL;
    }

    /**
     *  处理密码的匹配逻辑
     *       // 确认当前系统的密码加密方式 使用MD5
     * @param uPs 明文
     * @param dataPs 密文
     * @return
     */
    private boolean checkPassword(String uPs,String dataPs){
        String uPsMd5 = DigestUtils.md5DigestAsHex(uPs.getBytes());
        return dataPs.equals(uPsMd5);
    }
}
