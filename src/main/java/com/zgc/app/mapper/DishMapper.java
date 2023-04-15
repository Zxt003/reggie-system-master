package com.zgc.app.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zgc.app.entity.Dish;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 菜品管理 Mapper 接口
 * </p>
 *
 * @author DancingHorse
 * @since 2023-03-16
 */
public interface DishMapper extends BaseMapper<Dish> {
    Page<Dish> selectPageWithCategory(IPage<Dish> page, @Param("ew") Wrapper<Dish> queryWrapper);
}
