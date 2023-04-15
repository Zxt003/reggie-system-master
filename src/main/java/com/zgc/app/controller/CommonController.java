package com.zgc.app.controller;

import com.zgc.app.common.R;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/common")
public class CommonController {

    @PostMapping("/upload")
    public R update(MultipartFile file) throws IOException {
        // 将用户提交上来的文件 写入到 指定的路径D:/reggie-img/
        String path = "C:\\Users\\46647\\Desktop\\大三下\\资料\\图片资源";
        String webPath = "";
            //获取的文件的原始文件名
            String originalFilename = file.getOriginalFilename();
            // 获取 . 出现的最后位置
            int i = originalFilename.indexOf(".");
            // 通过字符截取
            String hz=(originalFilename.substring(i));
            // 原始文件名 【如果两个用户提交同一个名字的文件，会造成覆盖】
            String randomStr = UUID.randomUUID().toString()+hz;
        // 内存中 创建了一个文件对象 —— 指定了改文件的存储位置
        File newFile = new File(path+randomStr);
        //Api的调用 将上传的文件对象，转移至我们创建的文件上
        file.transferTo(newFile);
        return R.success().setData(webPath+randomStr);
    }
}
