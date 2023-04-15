package com.zgc.app.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.zgc.app.common.Constant;
import com.zgc.app.common.R;
import com.zgc.app.entity.AddressBook;
import com.zgc.app.entity.User;
import com.zgc.app.service.IAddressBookService;
import com.zgc.app.service.IUserService;
import com.zgc.app.utils.BaseContext;
import com.zgc.app.utils.GaodeAddressUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/addressBook")
public class AddressBookController {

    @Autowired
    private IAddressBookService addressBookService;

    @PostMapping
    public R add(@RequestBody AddressBook addressBook) {
        /**
         * 添加流程优化，判断用户是否有默认地址，没有没人地址，将新添加的地址设为默认。
         */
        QueryWrapper<AddressBook> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(AddressBook::getIsDefault, 1);
        AddressBook one = addressBookService.getOne(queryWrapper);
        if (ObjectUtils.isEmpty(one)) {
            addressBook.setIsDefault(1);
        }
        addressBook.setUserId(BaseContext.getCurrentId());
        if (addressBookService.save(addressBook)) {
            return R.success();
        }
        return R.error();
    }

    @GetMapping("/list")
    public R list() {
        // 创建条件 【当前登录用户的ID】
        QueryWrapper<AddressBook> addressWrapper = new QueryWrapper<>();
        addressWrapper.lambda().eq(AddressBook::getUserId, BaseContext.getCurrentId());
        // 查询结果并返回
        List<AddressBook> list = addressBookService.list(addressWrapper);
        return R.success(list);
    }

    @GetMapping("/{id}")
    public R getOne(@PathVariable("id") Long id) {
        AddressBook addressBook = addressBookService.getById(id);
        return R.success(addressBook);
    }

    @PutMapping
    public R edit(@RequestBody AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        if (addressBookService.updateById(addressBook)) {
            return R.success();
        }
        return R.error();
    }

    @DeleteMapping
    public R remove(@RequestParam("ids") List<Long> ids) {
        /**
         * 如何保证接口的安全
         */
        if (addressBookService.removeByIds(ids)) {
            return R.success();
        }
       return R.error();
    }

    @PutMapping("/default")
    public R setDefault(@RequestBody AddressBook addressBook){
        /**
         * 设置默认的收货地址
         *      业务问题：默认地址只能有一个
         *
         *      1. 将当前用户的所有地址设为非默认 id  BaseContext.getCurrentId();
         *      2. 将当前用户提交的地址改为默认
         */

        UpdateWrapper<AddressBook> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(AddressBook::getIsDefault,0);
        updateWrapper.lambda().eq(AddressBook::getUserId, BaseContext.getCurrentId());
        if (addressBookService.update(updateWrapper)) {
            addressBook.setIsDefault(1);
            if (addressBookService.updateById(addressBook)) {
                return R.success();
            }
        }
        //设为默认
        return R.error();
    }

    @GetMapping("/default")
    public R getDefault(){
        // 当前登录用户    BaseContext.getCurrentId();
        QueryWrapper<AddressBook> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(AddressBook::getUserId,BaseContext.getCurrentId());
        queryWrapper.lambda().eq(AddressBook::getIsDefault,1);
        AddressBook defaultAddress = addressBookService.getOne(queryWrapper);
        return R.success(defaultAddress);
    }
    @PostMapping("/locationdemo")
    public String tolocation(String location){
        location= GaodeAddressUtils.getLonAndLatByAddress(location);
        return location;
    }

}
