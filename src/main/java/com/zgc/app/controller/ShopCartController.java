package com.zgc.app.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zgc.app.common.Constant;
import com.zgc.app.common.R;
import com.zgc.app.entity.ShoppingCart;
import com.zgc.app.entity.User;
import com.zgc.app.service.IShoppingCartService;
import com.zgc.app.service.IUserService;
import com.zgc.app.utils.BaseContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
public class ShopCartController {

    @Autowired
    private IShoppingCartService shoppingCartService;
    @Autowired
    private IUserService userService;

    @GetMapping("/list")
    public R list() {
        /**
         *  获取 当前登录用户的购物车信息
         *  能够确认用户的信息，就是用户的【电话号码】
         *  购物车数据库 通过用户ID来绑定数据
         */
        QueryWrapper<ShoppingCart> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        return R.success(list);
    }

    @PostMapping("/add")
    public R add(@RequestBody ShoppingCart shoppingCart) {
        //1 user.getId();shoppingCart.getDishID  OR shoppingCart.setmealId
        // 查询数据库的信息是否存在，存在则修改数据，不存在则添加数据
        QueryWrapper<ShoppingCart> shopWrapper = new QueryWrapper<>();
        shopWrapper.lambda().eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        shopWrapper.lambda().eq(!StringUtils.isEmpty(shoppingCart.getDishId()),ShoppingCart::getDishId,shoppingCart.getDishId());
        shopWrapper.lambda().eq(!StringUtils.isEmpty(shoppingCart.getSetmealId()),ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        ShoppingCart one = shoppingCartService.getOne(shopWrapper);
        if (ObjectUtils.isEmpty(one)) {
            //需要注入+ 当前登录用户的ID + 创建时间 √  - - 价格 [订单生成的时候，重新获取，此时冗余提交信息即可]
            shoppingCart.setUserId(BaseContext.getCurrentId());
            shoppingCart.setNumber(1);
            if (shoppingCartService.save(shoppingCart)) {
                return R.success(shoppingCart);
            }
        }else{
            one.setNumber(one.getNumber()+1);
            if (shoppingCartService.updateById(one)) {
                return R.success(one);
            }
        }
        return R.error();
    }

    @DeleteMapping("/clean")
    public R clean(){
        /**
         * 清空购物车 ———— 删除当前用户绑定的购物车数据
         *  // 获取用户ID
         */
        // 删除制定条件【userid】的购物车数据
        QueryWrapper<ShoppingCart> charWrapper = new QueryWrapper<>();
        charWrapper.lambda().eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        if (shoppingCartService.remove(charWrapper)) {
            return R.success();
        }
        return R.error();
    }

    @PostMapping("/sub")
    public R sub(@RequestBody ShoppingCart shoppingCart){
        //1 user.getId() == BaseContext.getCurrentId();shoppingCart.getDishID  OR shoppingCart.setmealId
        // 查询数据库的信息是否存在，存在则修改数据，不存在则添加数据
        QueryWrapper<ShoppingCart> shopWrapper = new QueryWrapper<>();
        shopWrapper.lambda().eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        shopWrapper.lambda().eq(!StringUtils.isEmpty(shoppingCart.getDishId()),ShoppingCart::getDishId,shoppingCart.getDishId());
        shopWrapper.lambda().eq(!StringUtils.isEmpty(shoppingCart.getSetmealId()),ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        ShoppingCart one = shoppingCartService.getOne(shopWrapper);
        one.setNumber(one.getNumber()-1);
        if (one.getNumber()==0) {
            //当前菜品的数量为0 删除数据即可
            if (shoppingCartService.removeById(one.getId())) {
                return R.success(one);
            }
        }else{
            // 直接修改数据
            if (shoppingCartService.updateById(one)) {
                return R.success(one);
            }
        }
        /**
         *  >1 =1  两个操作： 删除，修改
         */
        return R.error();
    }
}
