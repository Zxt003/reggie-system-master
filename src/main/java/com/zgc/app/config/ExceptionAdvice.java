package com.zgc.app.config;

import com.zgc.app.common.R;
import com.zgc.app.exception.ServiceException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

//@ControllerAdvice
@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(ServiceException.class)
    public R serviceExceptionHandler(ServiceException e){
        return R.error(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public R baseException(Exception e){
        // 见异常信息输出在控制台
            e.printStackTrace();
            return R.error("后端异常");
    }//修改2
}

/**
 *  统一异常处理的机制
 *  ——
 *  我们自己去创建一个新的异常
 *  通过处理器去捕获【自定义的异常】
 *      然后返回结果【通过异常来传递信息-统一返回结果】
 */
