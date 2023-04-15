package com.zgc.app.service.impl;

import com.zgc.app.entity.Orders;
import com.zgc.app.mapper.OrdersMapper;
import com.zgc.app.service.IOrdersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author DancingHorse
 * @since 2023-03-16
 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements IOrdersService {

}
