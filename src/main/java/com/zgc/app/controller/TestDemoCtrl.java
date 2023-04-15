package com.zgc.app.controller;

import com.zgc.app.common.R;
import com.zgc.app.exception.ServiceException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Api(tags = "测试接口")
@RestController
@RequestMapping("/demo")
public class TestDemoCtrl {

    @ApiOperation("基础测试:Hello")
    @GetMapping("/hello")
    public R hello(){
        return R.success();
    }

    @ApiOperation("异常测试:Error")
    @GetMapping("/error")
    public R error(@RequestParam("i") int i){
        int reset = 10/i;
        return R.success(reset);
    }

    @ApiOperation("自定义异常测试:service")
    @GetMapping("/service")
    public R service(@RequestParam("i") int i){
        if(i==1){
            throw new ServiceException("用户输入的参数不能为1");
        }else if(i==100){
            throw new ServiceException("参数等于100，执行错误");
        }
        return R.success(i);
    }

    @PostMapping("/update")
    public R update(MultipartFile updateFile) throws IOException {
        // 将用户提交上来的文件 写入到 指定的路径D:/reggie-img/
        String path = "D:/reggie-img/";
        String webPath = "/img/";
        //获取的文件的原始文件名
        String originalFilename = updateFile.getOriginalFilename();
        // 获取 . 出现的最后位置
        int i = originalFilename.indexOf(".");
        // 通过字符截取
        String hz=(originalFilename.substring(i));
        // 原始文件名 【如果两个用户提交同一个名字的文件，会造成覆盖】
        String randomStr = UUID.randomUUID().toString()+hz;
        // 内存中 创建了一个文件对象 —— 指定了改文件的存储位置
        File file = new File(path+randomStr);
        //Api的调用 将上传的文件对象，转移至我们创建的文件上
        updateFile.transferTo(file);
        return R.success().setData(webPath+randomStr);
    }

    public static void main(String[] args) {
        String originalFilename = "asdadada.png";
        for (String s : originalFilename.split("\\.")) {
            System.out.println(s);
        }

    }
}

/**
 *  统一异常处理 某一个控制器【webAPI接口只要出现异常，都会被捕获，进行统一处理。】
 */
