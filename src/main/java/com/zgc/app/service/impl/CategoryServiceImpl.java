package com.zgc.app.service.impl;

import com.zgc.app.entity.Category;
import com.zgc.app.mapper.CategoryMapper;
import com.zgc.app.service.ICategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 菜品及套餐分类 服务实现类
 * </p>
 *
 * @author DancingHorse
 * @since 2023-03-16
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements ICategoryService {

}
