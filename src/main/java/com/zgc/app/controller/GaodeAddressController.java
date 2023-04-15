package com.zgc.app.controller;

import com.zgc.app.common.R;
import com.zgc.app.utils.GaodeAddressUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/gdd")
public class GaodeAddressController {

    @Autowired
    private GaodeAddressUtils gaodeAddressUtils;

    @GetMapping
    public R test(@RequestParam("address") String address) {

//        String address = "广西科技师范学院12栋";

        String addressjw = gaodeAddressUtils.getLonAndLatByAddress(address);
        System.out.println(1);
        System.out.println(addressjw);
        System.out.println(2);
        return R.success(addressjw);

    }
}
