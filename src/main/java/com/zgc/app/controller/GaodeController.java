package com.zgc.app.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zgc.app.common.R;
import com.zgc.app.entity.AddressBook;
import com.zgc.app.service.IAddressBookService;
import com.zgc.app.utils.BaseContext;
import com.zgc.app.utils.GaodeAddressUtils;
import com.zgc.app.utils.GaodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/map")
public class GaodeController {

    @Autowired
    private GaodeUtils gaodeUtils;
    @Autowired
    private IAddressBookService addressBookService;
    @GetMapping
    public HashMap getDistanceAndTime(@RequestParam("zb") String zb){
        /**
         * 通过计算返回用户的距离以及配送时间 - 调用第三方接口
         */
        Map<String, Object> map = new HashMap<>();
        QueryWrapper<AddressBook> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(AddressBook::getUserId, BaseContext.getCurrentId());
        queryWrapper.lambda().eq(AddressBook::getIsDefault,1);
        AddressBook addressBook = addressBookService.getOne(queryWrapper);
        String addressBookDetail = addressBook.getDetail();
        //String lonLat = GaodeAddressUtils.getLonAndLatByAddress(addressBookDetail);//拿到默认地址
        HashMap<String,String> map1 = gaodeUtils.getDistanceAndDuration(zb);//将计算的距离存入map对象
        System.out.println("map1"+map1);
        String distance=map1.get("distance");
        String duration=map1.get("duration");
        String price=map1.get("price");
        System.out.println(distance);
        map1.put("distance",distance);
        map1.put("duration",duration);
        map1.put("price",price);
        return map1;
    }


}
