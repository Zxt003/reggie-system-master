package com.zgc.app.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.zgc.app.common.R;
import com.zgc.app.entity.Category;
import com.zgc.app.entity.Dish;
import com.zgc.app.entity.DishFlavor;
import com.zgc.app.exception.ServiceException;
import com.zgc.app.service.IDishFlavorService;
import com.zgc.app.service.IDishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;


import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜品管理 前端控制器
 * </p>
 *
 * @author DancingHorse
 * @since 2023-03-16
 */
@RestController
@RequestMapping("/dish")
public class DishController {

    /**
     * 想要引入 对应的 业务层依赖
     * 完成对应的五个接口Rest风格
     */
    @Autowired
    private IDishService baseService;
    @Autowired
    private IDishFlavorService dishFlavorService;


    /**
     * 目标：获取菜品的【分类名称】
     * --- 拓展字段
     * 实现的方式：1，通过前端接口复用的方式来获取对应数据
     * 实现的方式：2. 修改查询语句传输数据
     */
    @GetMapping("/page")
    public R search(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer current,
            @RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer size) {
        QueryWrapper<Dish> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().like(!StringUtils.isEmpty(name),Dish::getName,name);
        queryWrapper.lambda().orderByAsc(Dish::getSort);
        queryWrapper.lambda().eq(Dish::getIsDeleted,0);
        Page<Dish> page = baseService.page(new Page<>(current, size), queryWrapper);
        return R.success(page);
    }

    @PostMapping
    public R save(@RequestBody Dish entity) {
        if (baseService.save(entity)) {
            return R.success();
        }
        return R.error();
    }

    @GetMapping("/{id}")
    public R getOne(@PathVariable("id") Long id){
        Dish dish = baseService.getById(id);
        return R.success(dish);
    }
    /**
     *   MP 原本的修改 单表的【List】
     */
    @PutMapping
    public R edit(@RequestBody Dish dish){
        if (baseService.updateById(dish)) {
            return R.success();
        }
        return R.error();
    }

    @DeleteMapping
    public R remove(@RequestParam("ids")List<Long> ids){
        if (baseService.removeByIds(ids)) {
            return R.success();
        }
        return R.error();
    }

    @PostMapping("/status/{type}")
    public R status(@PathVariable("type") Integer type,@RequestParam("ids")List<Long> ids){
        //批量修改状态
        List<Dish> list = ids.stream().map(id -> {
            Dish dish = new Dish();
            dish.setId(id);
            dish.setStatus(type);
            return dish;
        }).collect(Collectors.toList());
        if (baseService.updateBatchById(list)) {
            return R.success();
        }
        return R.error();
    }


    @GetMapping("/list")
    public R list(
            @RequestParam("categoryId")Long categoryId,
            @RequestParam(value="status",required = false,defaultValue = "1")Integer status
    ){
        QueryWrapper<Dish> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Dish::getCategoryId,categoryId);
        queryWrapper.lambda().eq(Dish::getStatus,status);//补充条件 根据起售禁售状态查询
        List<Dish> list = baseService.list(queryWrapper);

        // 范湖信息中，除了菜品本身的信息之外还需要菜品的详情
        for (Dish dish : list) {
            QueryWrapper<DishFlavor> flavorMapper = new QueryWrapper();
            flavorMapper.lambda().eq(DishFlavor::getDishId,dish.getId());
            List<DishFlavor> flavorList = dishFlavorService.list(flavorMapper);
            dish.setFlavors(flavorList);
        }
        return R.success(list);
    }
}
