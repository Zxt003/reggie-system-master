package com.zgc.app.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zgc.app.common.R;
import com.zgc.app.entity.Dish;
import com.zgc.app.entity.DishFlavor;
import com.zgc.app.mapper.DishMapper;
import com.zgc.app.service.IDishFlavorService;
import com.zgc.app.service.IDishService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜品管理 服务实现类
 * </p>
 *
 * @author DancingHorse
 * @since 2023-03-16
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements IDishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private IDishFlavorService dishFlavorService;
    @Override
    public <E extends IPage<Dish>> E page(E page, Wrapper<Dish> queryWrapper) {
        System.out.println("=====================");
        // 分页，查询获取 取菜品的【分类名称】
        /**
         * TODO:需要更换接口，使业务合理化
         */
        Page<Dish> dishes = dishMapper.selectPageWithCategory(page,queryWrapper);
        return (E) dishes;
    }

    /**
     * 业务 ，添加菜品的基本信息：id、价格 名称、 图片、、、
     *
     * 添加菜品的口味信息【DishF】 菜品的id
     *
     */
    @Override
    @Transactional
    public boolean save(Dish entity) {
        super.save(entity);
        List<DishFlavor> flavors = entity.getFlavors().stream().map(item->{
            item.setDishId(entity.getId());
            return item;
        }).collect(Collectors.toList());
        return dishFlavorService.saveBatch(flavors);
    }

    @Override
    public Dish getById(Serializable id) {
        Dish dish = super.getById(id);
        QueryWrapper<DishFlavor> queryWrapper = new QueryWrapper();
        queryWrapper.lambda().eq(DishFlavor::getDishId,id);
        List<DishFlavor> list = dishFlavorService.list(queryWrapper);
        dish.setFlavors(list);
        return dish;
    }
    /**
     * 修改内容 的口味List 所有的口味
     *  先删除，在添加
     */
    @Override
    @Transactional
    public boolean updateById(Dish entity) {
        super.updateById(entity);
        QueryWrapper<DishFlavor> queryWrapper = new QueryWrapper();
        queryWrapper.lambda().eq(DishFlavor::getDishId,entity.getId());
        dishFlavorService.remove(queryWrapper);
        List<DishFlavor> flavors = entity.getFlavors().stream().map(item->{
            item.setDishId(entity.getId());
            return item;
        }).collect(Collectors.toList());
        return dishFlavorService.saveBatch(flavors);
    }
}
