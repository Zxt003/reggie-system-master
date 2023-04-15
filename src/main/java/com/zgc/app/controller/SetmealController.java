package com.zgc.app.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zgc.app.common.R;
import com.zgc.app.entity.Setmeal;
import com.zgc.app.entity.SetmealDish;
import com.zgc.app.service.ISetmealDishService;
import com.zgc.app.service.ISetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 套餐 前端控制器
 * </p>
 *
 * @author DancingHorse
 * @since 2023-03-16
 */
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    /**
     * 想要引入 对应的 业务层依赖
     * 完成对应的五个接口Rest风格
     */
    @Autowired
    private ISetmealService baseService;

    @Autowired
    private ISetmealDishService dishService;

    @GetMapping("/page")
    public R page(@RequestParam(value = "name", required = false) String name,
                  @RequestParam(value = "page", required = false, defaultValue = "1") Integer current,
                  @RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer size) {
        /**
         * 查询列表
         */
        Page page = new Page(current, size);
        QueryWrapper<Setmeal> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(!StringUtils.isEmpty(name), Setmeal::getName, name);
        queryWrapper.select("id,category_id,`name`,image,(select name from category where id = category_id) as category_Name,price,`status`,update_time ");
        baseService.page(page, queryWrapper);
        return R.success(page);
    }

    /**
     * 新增套餐接口
     */
    @PostMapping
    public R save(@RequestBody Setmeal setmeal) {
        if (baseService.save(setmeal)) {
            return R.success();
        }
        return R.error();
    }

    @GetMapping("/{id}")
    public R getOne(@PathVariable("id") Long id) {
        Setmeal setmeal = baseService.getById(id);
        return R.success(setmeal);
    }

    @PutMapping
    public R edit(@RequestBody Setmeal setmeal) {
        if (baseService.updateById(setmeal)) {
            return R.success();
        }
        return R.error();
    }

    @DeleteMapping
    public R remove(@RequestParam("ids") List<Long> ids) {
        baseService.removeByIds(ids);
        return R.success();
    }

    @PostMapping("/status/{type}")
    public R status(@PathVariable("type") Integer type, @RequestParam("ids") List<Long> ids) {
        // 数据安全性分析： 0 or 1
        List<Setmeal> collect = ids.stream().map(id -> {
            Setmeal setmeal = new Setmeal();
            setmeal.setId(id);
            setmeal.setStatus((type == 1) ? 1 : 0);
            return setmeal;
        }).collect(Collectors.toList());
        baseService.updateBatchById(collect);
        return R.success();
    }

    @GetMapping("/list")
    public R list(@RequestParam("categoryId") Long categoryId,
                  @RequestParam("status") Integer status) {
        //** 套餐列表 根据 categoryId 获取 正在销售的套餐
        QueryWrapper<Setmeal> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Setmeal::getCategoryId, categoryId);
        queryWrapper.lambda().eq(Setmeal::getStatus, status);
        List<Setmeal> list = baseService.list(queryWrapper);
        return R.success(list);
    }

    @GetMapping("/dish/{setmealId}")
    public R getDish(@PathVariable("setmealId") Long setmealId) {
        /**
         * 通过数据关系，查询出来的内容应该还包括菜品的图片
         */
        QueryWrapper<SetmealDish> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(" *,(select image from dish where id = dish_id) as image");
        queryWrapper.lambda().eq(SetmealDish::getSetmealId, setmealId);
        List<SetmealDish> list = dishService.list(queryWrapper);
        return R.success(list);
    }

}
