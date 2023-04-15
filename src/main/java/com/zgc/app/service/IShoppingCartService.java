package com.zgc.app.service;

import com.zgc.app.entity.ShoppingCart;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;

/**
 * <p>
 * 购物车 服务类
 * </p>
 *
 * @author DancingHorse
 * @since 2023-03-16
 */
public interface IShoppingCartService extends IService<ShoppingCart> {

    BigDecimal getShopCartAmountByUserId(Long currentId);

}
