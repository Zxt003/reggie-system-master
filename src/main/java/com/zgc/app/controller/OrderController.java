package com.zgc.app.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zgc.app.common.R;
import com.zgc.app.entity.AddressBook;
import com.zgc.app.entity.OrderDetail;
import com.zgc.app.entity.Orders;
import com.zgc.app.entity.ShoppingCart;
import com.zgc.app.service.IAddressBookService;
import com.zgc.app.service.IOrderDetailService;
import com.zgc.app.service.IOrdersService;
import com.zgc.app.service.IShoppingCartService;
import com.zgc.app.utils.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private IOrdersService ordersService;
    @Autowired
    private IAddressBookService addressBookService;
    @Autowired
    private IShoppingCartService shoppingCartService;
    @Autowired
    private IOrderDetailService orderDetailService;

    @GetMapping("/page")
    public R page(
            @RequestParam(value = "number", required = false) String number,
            @RequestParam(value = "beginTime", required = false) String beginTime,
            @RequestParam(value = "endTime", required = false) String endTime,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer current,
            @RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer size
    ) {
        Page<Orders> page = new Page<>(current, size);
        QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(!StringUtils.isEmpty(number), Orders::getNumber, number);
        queryWrapper.lambda().between(!StringUtils.isEmpty(beginTime) && !StringUtils.isEmpty(endTime)
                , Orders::getOrderTime, beginTime, endTime);
        ordersService.page(page, queryWrapper);
        return R.success(page);
    }

    @PutMapping
    public R edit(@RequestBody Orders orders) {
        if (ordersService.updateById(orders)) {
            return R.success();
        }
        return R.error();
    }

    /**
     * 接取的信息包括，备注，地址ID，支付方式【固定】
     */
    @PostMapping("/submit")
    public R submit(@RequestBody Orders orders) {
        //orders.setId();
        orders.setNumber(UUID.randomUUID().toString());
        orders.setUserId(BaseContext.getCurrentId());
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());// 与订单生成时间相同，保证数据正常
        //---- 订单金额【购物车所有商品计算后的结果】 shoppingCartService

        // 获取当前用户购物车所有的商品统计金额
        orders.setAmount(getAmountBySQL());//订单金额 -- 需要从购物车中计算
        //---- 订单收获信息冗余   orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());
        orders.setPhone(addressBook.getPhone());//冗余信息
        orders.setAddress(addressBook.getDetail());//冗余信息
        orders.setUserName(addressBook.getConsignee());//收货人-用户昵称[]
        orders.setConsignee(addressBook.getConsignee());//收货人-地址信息的冗余
        //订单创建
        if (ordersService.save(orders)) {
            //获取订单详情 通过购物车内容转换
            QueryWrapper<ShoppingCart> cartWrapper = new QueryWrapper<>();
            cartWrapper.lambda().eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
            List<ShoppingCart> list = shoppingCartService.list(cartWrapper);
            List<OrderDetail> orderDetailList =  list.stream().map(cart ->{
                OrderDetail detail = new OrderDetail();
                detail.setName(cart.getName());
                detail.setImage(cart.getImage());
                detail.setOrderId(orders.getId());
                detail.setDishId(cart.getDishId());
                detail.setSetmealId(cart.getSetmealId());
                detail.setDishFlavor(cart.getDishFlavor());
                detail.setNumber(cart.getNumber());
                detail.setAmount(cart.getAmount());
                return detail;
            }).collect(Collectors.toList());
            //订单创建完成，录入订单详情
            orderDetailService.saveBatch(orderDetailList);
            // 订单生成完成，清空购物车
            QueryWrapper<ShoppingCart> charWrapper = new QueryWrapper<>();
            charWrapper.lambda().eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
            if (shoppingCartService.remove(cartWrapper)) {
                return R.success();
            }
        }
        return R.error();
    }

    private BigDecimal getAmountByJava(){
        QueryWrapper<ShoppingCart> cartWrapper = new QueryWrapper<>();
        cartWrapper.lambda().eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        List<ShoppingCart> list = shoppingCartService.list(cartWrapper);
        BigDecimal amount = new BigDecimal(0);
        for (ShoppingCart cart : list) {
            BigDecimal multiply = cart.getAmount().multiply(new BigDecimal(cart.getNumber()));
            amount = amount.add(multiply);
        }
        return amount;
    }

    private BigDecimal getAmountBySQL(){
        //SELECT sum(amount*number) FROM `shopping_cart` where user_id=1640607066227871745 GROUP BY	user_id
        return shoppingCartService.getShopCartAmountByUserId(BaseContext.getCurrentId());
    }

    @GetMapping("/userPage")
    public R userPage(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer current,
            @RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer size
    ){
        // 数据是当前登录用户[√]
        // 按照时间排序[]
        // 数据像包括-订单的详情信息
        Page<Orders> page = new Page(current, size);
        QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Orders::getUserId,BaseContext.getCurrentId());
        queryWrapper.lambda().orderByDesc(Orders::getOrderTime);
        ordersService.page(page,queryWrapper);
        for (Orders record : page.getRecords()) {
            QueryWrapper<OrderDetail> detailsWrapper = new QueryWrapper();
            detailsWrapper.lambda().eq(OrderDetail::getOrderId,record.getId());
            List<OrderDetail> list = orderDetailService.list(detailsWrapper);
            record.setOrderDetails(list);
        }
        return R.success(page);
    }

}
