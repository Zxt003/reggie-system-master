package com.zgc.app.service.impl;

import com.zgc.app.entity.ShoppingCart;
import com.zgc.app.mapper.ShoppingCartMapper;
import com.zgc.app.service.IShoppingCartService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * <p>
 * 购物车 服务实现类
 * </p>
 *
 * @author DancingHorse
 * @since 2023-03-16
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements IShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Override
    public BigDecimal getShopCartAmountByUserId(Long currentId) {
        return shoppingCartMapper.getShopCartAmountByUserId(currentId);
    }
}
