package com.zgc.app.mapper;

import com.zgc.app.entity.ShoppingCart;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.math.BigDecimal;

/**
 * <p>
 * 购物车 Mapper 接口
 * </p>
 *
 * @author DancingHorse
 * @since 2023-03-16
 */
public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {

    BigDecimal getShopCartAmountByUserId(Long currentId);
}
