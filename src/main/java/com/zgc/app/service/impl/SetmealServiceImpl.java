package com.zgc.app.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zgc.app.entity.Setmeal;
import com.zgc.app.entity.SetmealDish;
import com.zgc.app.mapper.SetmealMapper;
import com.zgc.app.service.ISetmealDishService;
import com.zgc.app.service.ISetmealService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 套餐 服务实现类
 * </p>
 *
 * @author DancingHorse
 * @since 2023-03-16
 */
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements ISetmealService {

    @Autowired
    private ISetmealDishService setmealDishService;

    /**
     * 1 默认状态——为停用
     * 2. 为详情绑定套餐ID
     */
    @Override
    @Transactional
    public boolean save(Setmeal entity) {
        entity.setStatus(0);
        super.save(entity);
        Long setMealId = entity.getId();
        List<SetmealDish> setmealDishes = entity.getSetmealDishes().stream().map(item -> {
            item.setSetmealId(setMealId.toString());
            return item;
        }).collect(Collectors.toList());
        return setmealDishService.saveBatch(setmealDishes);
    }

    /**
     *  查询套餐信息【包含套餐详情】
     * @param id 主键ID
     * @return
     */
    @Override
    public Setmeal getById(Serializable id) {
        Setmeal setmeal = super.getById(id);
        QueryWrapper<SetmealDish> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> list = setmealDishService.list(queryWrapper);
        setmeal.setSetmealDishes(list);
        return setmeal;
    }

    /**
     * 修改套餐详情：
     *  1. 修改本身信息
     *  2. 修改所属套餐菜品详情
     *      2.1 删除当前套餐中所有套餐详情
     *      2.2 将前端提交的套餐详情【最新的、所有的】信息添加的数据库
     * @param entity 实体对象
     * @return
     */
    @Override
    @Transactional
    public boolean updateById(Setmeal entity) {
        super.updateById(entity);
        QueryWrapper<SetmealDish> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SetmealDish::getSetmealId,entity.getId());
        setmealDishService.remove(queryWrapper);
        List<SetmealDish> setmealDishes = entity.getSetmealDishes().stream().map(item -> {
            item.setSetmealId(entity.getId().toString());
            return item;
        }).collect(Collectors.toList());
        return setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    @Transactional
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        // 1. 基础实现
       /* for (Serializable stmealId : idList) {
            QueryWrapper<SetmealDish> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(SetmealDish::getSetmealId,stmealId);
            setmealDishService.remove(queryWrapper);
        }*/
        // 2. 优化实现
        QueryWrapper<SetmealDish> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(SetmealDish::getSetmealId,idList);
        setmealDishService.remove(queryWrapper);
        return  super.removeByIds(idList);
    }
}
