package com.zgc.app.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zgc.app.common.R;
import com.zgc.app.entity.Category;
import com.zgc.app.entity.Employee;
import com.zgc.app.exception.ServiceException;
import com.zgc.app.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * <p>
 * 菜品及套餐分类 前端控制器
 * </p>
 *
 * @author DancingHorse
 * @since 2023-03-16
 */
@RestController
@RequestMapping("/category")
public class CategoryController {

    /**
     * 想要引入 对应的 业务层依赖
     * 完成对应的五个接口Rest风格
     */
    @Autowired
    private ICategoryService baseService;

    @PostMapping
    public R save(@RequestBody Category entity) {
        /**
         * 判断当前分类名称是否已有
         */
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(!StringUtils.isEmpty(entity.getName()), Category::getName, entity.getName());
        if (!ObjectUtils.isEmpty(baseService.getOne(queryWrapper))) {
            throw new ServiceException("当前分类已存在");
        }
        if (baseService.save(entity)) {
            return R.success();
        }
        return R.error();
    }
    @DeleteMapping
    public R remove(@RequestParam("id") Long id){
        if (baseService.removeById(id)) {
            return R.success();
        }
        return R.error();
    }

    @PutMapping
    public R edit(@RequestBody Category entity) {
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(!StringUtils.isEmpty(entity.getName()), Category::getName, entity.getName());
        queryWrapper.lambda().ne(Category::getId, entity.getId());
        if (!ObjectUtils.isEmpty(baseService.getOne(queryWrapper))) {
            throw new ServiceException("当前分类已存在");
        }
        entity.setUpdateUser(null);
        entity.setUpdateTime(null);
        // 目的：如果用户名冲突了，我们给用户一个良好的提示
        if (baseService.updateById(entity)) {
            return R.success();
        }
        return R.error();
    }

    @GetMapping("/page")
    public R search(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer current,
            @RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer size) {
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().orderByAsc(Category::getSort);
        Page<Category> page = baseService.page(new Page<>(current, size), queryWrapper);
        return R.success(page);
    }

    /**
     * list 接口 返回菜品类型
     */
    @GetMapping("/list")
    public R search(
            @RequestParam(value = "type", required = false) Integer type) {
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(type!=null,Category::getType,type);
        List<Category> list = baseService.list(queryWrapper);
        return R.success(list);
    }
}
